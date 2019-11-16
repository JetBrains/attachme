package com.attachme.agent;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class AttachmeClient implements AutoCloseable {

  // Avoid JSON library for now
  static String MSG_FORMAT = "{\"pid\":${PID},\"ports\":[${PORTS_CS}]}";
  private final Socket sock;

  public AttachmeClient(int port) throws IOException {
    sock = new Socket("localhost", port);
  }

  private String commaSeparated(List<Integer> ports) {
    StringBuilder sb = new StringBuilder();
    for (int i : ports) {
      if (sb.length() > 0)
        sb.append(',');
      sb.append(i);
    }
    return sb.toString();
  }

  private String makeMsg(List<Integer> ports, int pid) {
    return MSG_FORMAT.replace("${PID}", pid + "").replace("${PORTS_CS}", commaSeparated(ports));
  }

  public void sendBoundPorts(List<Integer> ports, int pid) throws IOException {
    String msg = makeMsg(ports, pid);
    sock.getOutputStream().write((msg + System.lineSeparator()).getBytes());
    System.err.println("[attachme] Successfully notified attachme listener");
  }


  @Override
  public void close() throws Exception {
    sock.close();
  }
}
