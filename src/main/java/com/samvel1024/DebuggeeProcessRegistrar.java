package com.samvel1024;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;

public class DebuggeeProcessRegistrar implements Runnable {

	final int port;
	final Consumer<ProcessRegisterMsg> listener;
	static final Gson gson = new Gson();

	public DebuggeeProcessRegistrar(int port, Consumer<ProcessRegisterMsg> listener) {
		this.port = port;
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void doRun() throws IOException {
		try (ServerSocket server = new ServerSocket(this.port)) {
			while (true) {
				try (Socket accept = server.accept()) {
					try {
						Scanner s = new Scanner(accept.getInputStream()).useDelimiter("\\A");
						ProcessRegisterMsg msg = gson.fromJson(s.next(), ProcessRegisterMsg.class);
						listener.accept(msg);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
