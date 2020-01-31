package com.attachme.agent;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ForkProcessIntegrationTest extends BaseIntegrationTest {

  public ForkProcessIntegrationTest() {
    super(2, 9090);
  }

  private String getAgentJar() {
    String path = new File("").getAbsolutePath() + "/build/libs/attachme-agent.jar";
    if (!new File(path).exists()) {
      throw new IllegalStateException("The agent lib is not present at " + path + ". Run the test with gradle");
    }
    return path;
  }

  @Override
  AutoCloseable spawnBackgroundProcess() {
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
      Process start = builder.start();
      return () -> {
        if (0 != start.waitFor()) {
          throw new IllegalStateException("Process exited with non 0 status");
        }
      };
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void receivesMessageFromForkedProcess() throws Exception {
    super.assertReceivesMessage();
  }

}
