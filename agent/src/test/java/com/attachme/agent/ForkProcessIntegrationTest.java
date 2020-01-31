package com.attachme.agent;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ForkProcessIntegrationTest {

  final int processNumber = 4;
  final int port = 9090;
  CountDownLatch latch = new CountDownLatch(processNumber);
  boolean allPortsNonEmpty = true;


  AttachmeServer.Listener listener = new AttachmeServer.Listener() {

    @Override
    public void onDebuggeeProcess(ProcessRegisterMsg msg) {
      latch.countDown();
      if (msg.getPorts().isEmpty()) allPortsNonEmpty = false;
    }

    @Override
    public void onFinished() {
    }
  };

  private String getAgentJar() {
    String path = new File("").getAbsolutePath() + "/build/libs/attachme-agent.jar";
    if (!new File(path).exists()) {
      throw new IllegalStateException("The agent lib is not present at " + path + ". Run the test with gradle");
    }
    return path;
  }

  private Process spawnProcess() {
    String java = System.getProperty("java.home") + "/bin/java";
    String javaToolOptions = String.format(
      "-javaagent:%s=port:%d " +
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:0", getAgentJar(), port);
    System.out.println("Using JAVA_TOOL_OPTIONS: " + javaToolOptions);
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command(java, "ForkBomb.java", (processNumber - 1) + "");
      builder.environment().put("JAVA_TOOL_OPTIONS", javaToolOptions);
      builder.environment().put("NO_WAIT", "YES");
      //Add builder.inheritIO() to troubleshoot a failing test
      builder.directory(new File("src/test/resources"));
      return builder.start();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void serverReceivesMessageFromForkedJVM() throws InterruptedException {
    Thread t = AttachmeServer.makeThread(9090, listener, AttachmeServer.Console.dummy);
    t.start();
    Process proc = null;
    try {
      proc = spawnProcess();
      Assert.assertTrue("Timeout reached, no process sent a message", latch.await(10, TimeUnit.SECONDS));
      Assert.assertTrue(allPortsNonEmpty);
      Assert.assertEquals(0, proc.waitFor());
      t.interrupt();
      t.join();
    } finally {
      if (proc != null) {
        proc.waitFor();
      }
    }

  }
}
