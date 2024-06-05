package com.attachme.agent;

import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class BaseIntegrationTest {

  final int processNumber;
  final int port;
  final CountDownLatch latch;
  boolean allPortsNonEmpty = true;

  private final AttachmeServer.Listener listener = new AttachmeServer.Listener() {

    @Override
    public void onDebuggeeProcess(ProcessRegisterMsg msg, String debuggeeAddress) {
      latch.countDown();
      if (msg.getPorts().isEmpty()) allPortsNonEmpty = false;
    }

    @Override
    public void onFinished() {
    }
  };

  public BaseIntegrationTest(int processNumber, int port) {
    this.processNumber = processNumber;
    this.port = port;
    this.latch = new CountDownLatch(processNumber);
  }


  abstract AutoCloseable spawnBackgroundProcess();

  public void assertReceivesMessage() throws Exception {
    Thread t = AttachmeServer.makeThread(9090, listener, AttachmeServer.Console.dummy);
    t.start();
    try (AutoCloseable ignored = spawnBackgroundProcess()) {
      Assert.assertTrue("Timeout reached, no process sent a message", latch.await(20, TimeUnit.SECONDS));
      Assert.assertTrue(allPortsNonEmpty);
      t.interrupt();
      t.join();
    }
  }

}
