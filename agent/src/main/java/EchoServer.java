import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

class EchoServer {

  public static void receive(int port) {
    try (ServerSocket sock = new ServerSocket(port)) {
      while (!Thread.currentThread().isInterrupted()) {
        try (Socket client = sock.accept()) {
          InputStream inputStream = client.getInputStream();
          ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
          String msg = (String) objectInputStream.readObject();
          System.out.println("Received message " + msg);
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
          objectOutputStream.writeObject(msg);
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }


  public static void send(String host, int port, String message) {
    try (Socket sock = new Socket(host, port)) {
      ObjectOutputStream outStream = new ObjectOutputStream(sock.getOutputStream());
      outStream.writeObject(message);
      System.out.println("Sent message " + message);
      ObjectInputStream inputStream = new ObjectInputStream(sock.getInputStream());
      String echo = (String) inputStream.readObject();
      System.out.println("Received message " + echo);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }


  public static void main(String[] args) {
    switch (args[0]) {
      case "server": {
        int port = Integer.parseInt(args[1]);
        receive(port);
        break;
      }
      case "client": {
        String addr = args[1];
        int port = Integer.parseInt(args[2]);
        String message = args[3];
        Objects.requireNonNull(addr, message);
        send(addr, port, message);
        break;
      }
      default: throw new IllegalArgumentException();
    }
  }

}
