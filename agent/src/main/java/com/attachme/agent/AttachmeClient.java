package com.attachme.agent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class AttachmeClient implements AutoCloseable {

  private final Socket sock;

  public AttachmeClient(String host, int port) throws IOException {
    sock = new Socket(host, port);
  }

  public void sendBoundPorts(List<Integer> ports, int pid) throws IOException {
    ProcessRegisterMsg msg = new ProcessRegisterMsg();
    msg.setPid(pid);
    msg.setPorts(ports);
    ObjectOutputStream stream = new ObjectOutputStream(sock.getOutputStream());
    stream.writeObject(msg);
    System.err.println("[attachme] Successfully notified attachme listener");
  }


  @Override
  public void close() throws Exception {
    sock.close();
  }
}
