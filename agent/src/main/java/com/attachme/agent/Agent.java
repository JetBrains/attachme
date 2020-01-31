package com.attachme.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.*;

public class Agent {

  static final String DEFAULT_PORT = "7857";
  static final String DEFAULT_HOST = "localhost";

  private static Map<String, String> parseArgs(String args) {
    if (args == null || args.trim().isEmpty())
      return Collections.emptyMap();
    Set<String> allowed = new HashSet<>(Arrays.asList("port", "host"));
    Map<String, String> ans = new HashMap<>();
    for (String arg : args.split(",")) {
      String[] kv = arg.split(":");
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
    task.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
    task.start();
  }

  private static class DebugPortTask implements Runnable {

    final Map<String, String> args;

    private DebugPortTask(Map<String, String> args) {
      this.args = args;
    }

    private int getPid() {
      final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
      final int index = jvmName.indexOf('@');
      if (index < 1) {
        return -1;
      }
      try {
        return Integer.parseInt(jvmName.substring(0, index));
      } catch (NumberFormatException e) {
        return -1;
      }
    }

    @Override
    public void run() {
      int pid = getPid();
      if (pid == -1) {
        System.err.println("[attachme] Could not determine the pid of the process");
        System.exit(1);
        return;
      }
      PortResolver portResolver = PortResolver.createPerOS();
      if (portResolver == null) {
        System.err.println("[attachme] Your OS is not supported os.name=" + System.getProperty("os.name"));
        System.exit(1);
        return;
      }
      System.err.println("[attachme] Initialized agent for process PID=" + pid);

      long start = System.currentTimeMillis();
      List<Integer> ports = Collections.emptyList();
      while (System.currentTimeMillis() - start < 1000 && ports.isEmpty()) {
        ports = portResolver.getPortCandidates(pid);
        if (ports.isEmpty()) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } else {
          try (AttachmeClient client = new AttachmeClient(
            args.getOrDefault("host", DEFAULT_HOST),
            Integer.parseInt(args.getOrDefault("port", DEFAULT_PORT)))) {
            client.sendBoundPorts(ports, pid);
          } catch (IOException e) {
            System.err.println("[attachme] IOException: Please turn on attachme listener in IntelliJ IDEA");
            e.printStackTrace();
          } catch (Exception e) {
            System.err.println("[attachme] Unknown error happened, please report in github");
            e.printStackTrace();
          }
        }
      }
      if (ports.isEmpty()) {
        System.err.println("[attachme] Could not find bound ports, maybe you did not attach a debugger");
      }
    }
  }
}
