package com.samvel1024;

public class Server {
	public static void main(String[] args) {
		new DebuggeeProcessRegistrar(5555, (x) -> System.out.println(x.toString())).run();
	}
}
