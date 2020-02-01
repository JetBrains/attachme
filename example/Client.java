import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

class Client {

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
    String addr = args[0];
    int port = Integer.parseInt(args[1].trim());
    String message = args[2];
    Objects.requireNonNull(addr, message);
    while (true) {
      send(addr, port, message);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
  }

}
