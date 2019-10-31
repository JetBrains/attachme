package com.attachme.plugin;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AttachmeRegistry implements Runnable {

  final Gson gson = new Gson();
  final int port;
  final Listener listener;
  final Console log;

  public AttachmeRegistry(int port, Listener listener, Console console) {
    this.port = port;
    this.listener = listener;
    this.log = console;
  }

  public static Thread makeThread(int port, Listener listener, Console console) {
    Thread thread = new Thread(new AttachmeRegistry(port, listener, console));
    thread.setDaemon(true);
    thread.setName("AttachmeListener");
    return thread;
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

  private String exceptionToString(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  private void handleError(Throwable e) {
    log.error(exceptionToString(e));
    listener.onFinished();
  }

  private void doRun() throws IOException {
    try (ServerSocket server = new ServerSocket(this.port)) {
      server.setSoTimeout(500);
      this.log.info("AttachMe listening for debuggee processes on port " + this.port);
      while (!Thread.currentThread().isInterrupted()) {
        try (Socket accept = server.accept()) {
          try {
            Scanner s = new Scanner(accept.getInputStream()).useDelimiter("\\A");
            ProcessRegisterMsg msg = gson.fromJson(s.next(), ProcessRegisterMsg.class);
            listener.onDebuggeeProcess(msg);
            this.log.info("Registered a debuggee process with pid " + msg.getPid() + " and possible ports " + msg.getPorts().toString());
          } catch (RuntimeException e) {
            e.printStackTrace();
            this.log.error(exceptionToString(e));
          }
        } catch (IOException ignored) { // This includes SocketTimeoutException as well
        }
      }
      this.log.info("Stopping attachme");
      listener.onFinished();
    } catch (BindException e) {
      log.error("Could not bind to the port " + this.port + ". Maybe another process is using that port (possibly another IntelliJ IDEA).");
      handleError(e);
    } catch (Throwable e) {
      log.error("Unhandled error occurred, please report");
      handleError(e);
      throw e;
    }
  }

  public interface Listener {
    Listener dummy = new Listener() {
      @Override
      public void onDebuggeeProcess(ProcessRegisterMsg msg) {

      }

      @Override
      public void onFinished() {

      }
    };
    void onDebuggeeProcess(ProcessRegisterMsg msg);

    void onFinished();
  }

  public interface Console {
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

    void info(String str);

    void error(String str);
  }

}
