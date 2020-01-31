
import java.io.IOException;
import java.util.Scanner;

public class ForkBomb {
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_RESET = "\u001B[0m";

  static void print(String message){
    System.out.println(ANSI_GREEN + message + ANSI_RESET);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    int index = Integer.parseInt(args[0]);
    print("Spawned new process with id " + index);
    if (index <= 0){
      if (System.getenv().get("NO_WAIT") == null) {
        print("Reached the last level. Press any key...");
        Scanner sc = new Scanner(System.in);
        sc.next();
      }
    }else {
      new ProcessBuilder()
        .command("java", "ForkBomb.java", "" + (index - 1))
        .inheritIO()
        .start()
        .waitFor();
    }
    print("Exiting from process with id " + index);
  }
}
