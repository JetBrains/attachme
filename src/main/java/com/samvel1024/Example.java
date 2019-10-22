package com.samvel1024;


import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class Example {
	public static void main(String[] args) throws IOException, InterruptedException {
		long pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		int level = Integer.parseInt(args[0]);
		System.out.println(pid + " Hello World from level " + level);
		if (level != 0) {
			Process java = new ProcessBuilder(
				"java",
				"com.samvel1024.Example", (level - 1) + "")
				.inheritIO()
				.redirectErrorStream(true).start();
			java.waitFor();
		} else {
			System.out.println(pid + " Waiting for input");
			new Scanner(System.in).next();
		}
	}
}
