package com.attachme.agent;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Agent {

	private static class DebugPortTask implements Runnable {
		@Override
		public void run() {
			try {
				doRun();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		void pipeScript(Process proc) throws IOException {
			BufferedReader script = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("util.py"))));
			BufferedWriter procInput = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
			String line = null;
			while ((line = script.readLine()) != null) {
				procInput.write(line + System.lineSeparator());
			}
			procInput.close();
		}

		private void doRun() throws InterruptedException, IOException {
			Process python = new ProcessBuilder()
				.command("python3")
				.redirectErrorStream(true)
				.start();
			pipeScript(python);
			BufferedReader script = new BufferedReader(new InputStreamReader(python.getInputStream()));
			Scanner sc = new Scanner(script);
			while(sc.hasNext()){
				System.err.println("[python] " + sc.nextLine());
			}
			python.waitFor();
			if (python.exitValue() != 0) {
				throw new RuntimeException("The python subprocess exited with error code " + python.exitValue());
			}
		}
	}

	public static void premain(String args, Instrumentation instrumentation) {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				new DebugPortTask().run();
			}
		}, 500);

	}
}
