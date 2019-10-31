package com.attachme.agent;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.*;

public class Agent {


  private static Map<String, String> parseArgs(String args) {
    if (args == null || args.trim().isEmpty())
      return Collections.emptyMap();
    Set<String> allowed = new HashSet<>(Arrays.asList("port", "python"));
    Map<String, String> ans = new HashMap<>();
    for (String arg : args.split(",")) {
      String kv[] = arg.split(":");
      if (kv.length != 2) {
        throw new IllegalArgumentException("Illegal argument format");
      }
      ans.put(kv[0], kv[1]);
    }
    if (ans.keySet().retainAll(allowed)) {
      throw new IllegalArgumentException("Illegal argument format");
    }
    // Validate the values
    try {
      if (ans.containsKey("port"))
        Integer.parseInt(ans.get("port"));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Illegal port number", e);
    }
    if (ans.containsKey("python") && !new File(ans.get("python")).exists()) {
      throw new IllegalArgumentException("Python path does not exist");
    }
    return ans;
  }

  public static void premain(String strArgs, Instrumentation instrumentation) {
    Map<String, String> args;
    try {
      args = parseArgs(strArgs);
    } catch (IllegalArgumentException e) {
      System.err.println("[attachme] FATAL ERROR: " + e.getMessage());
      System.exit(1);
      return;
    }
    // It's started from a thread to actively wait for jdwp to attach, it's required when suspend=y
    Thread task = new Thread(new DebugPortTask(args), "AttachmeAgentTread");
    task.setDaemon(true);
    task.start();
  }

  private static class DebugPortTask implements Runnable {

    final Map<String, String> args;

    private DebugPortTask(Map<String, String> args) {
      this.args = args;
    }

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
      ProcessBuilder builder = new ProcessBuilder()
        .command(args.getOrDefault("python", "python"))
        .redirectErrorStream(true);
      builder.environment().put("ATTACHME_PORT", args.getOrDefault("port", "7857"));
      Process python = builder.start();
      pipeScript(python);
      BufferedReader script = new BufferedReader(new InputStreamReader(python.getInputStream()));
      Scanner sc = new Scanner(script);
      while (sc.hasNext()) {
        System.err.println("[attachme] " + sc.nextLine());
      }
      python.waitFor();
      if (python.exitValue() != 0) {
        throw new RuntimeException("The python subprocess exited with error code " + python.exitValue());
      }
    }
  }
}
