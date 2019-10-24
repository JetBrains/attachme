package com.attachme.plugin;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class AttachmeRegistry implements Runnable {

	public static Thread makeThread(int port, Listener listener, Console console) {
		Thread thread = new Thread(new AttachmeRegistry(port, listener, console));
		thread.setDaemon(true);
		thread.setName("AttachmeListener");
		return thread;
	}

	static final Gson gson = new Gson();

	public interface Listener {
		void onDebuggeeProcess(ProcessRegisterMsg msg);
		void onFinished();
	}

	public interface Console {
		void info(String str);

		void error(String str);

		Console dummy = new Console() {
			@Override
			public void info(String str) {
				System.out.println(str);
			}

			@Override
			public void error(String str) {
				System.out.println(str);
			}
		};
	}

	final int port;
	final Listener listener;
	final Console log;

	public AttachmeRegistry(int port, Listener listener, Console console) {
		this.port = port;
		this.listener = listener;
		this.log = console;
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (Exception e) {
			this.log.error("Error " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void doRun() throws IOException {
		try (ServerSocket server = new ServerSocket(this.port)) {
			server.setSoTimeout(500);
			this.log.info("Attachme listening for debuggee processes on port " + this.port);
			while (!Thread.currentThread().isInterrupted()) {
				try (Socket accept = server.accept()) {
					try {
						Scanner s = new Scanner(accept.getInputStream()).useDelimiter("\\A");
						ProcessRegisterMsg msg = gson.fromJson(s.next(), ProcessRegisterMsg.class);
						listener.onDebuggeeProcess(msg);
						this.log.info("Registered a debuggee process with pid " + msg.getPid() + " and possible ports " + msg.getPorts().toString());
					} catch (RuntimeException e) {
						e.printStackTrace();
						this.log.error(e.getMessage());
					}
				} catch (SocketTimeoutException ignored) {
				}
			}
			this.log.info("Stopping attachme");
			listener.onFinished();
		}
	}

}
