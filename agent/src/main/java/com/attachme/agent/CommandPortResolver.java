package com.attachme.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class CommandPortResolver implements PortResolver {

  public static CommandPortResolver forWindows() {
    return new CommandPortResolver(new WindowsHandler());
  }

  public static CommandPortResolver forUnix() {
    return new CommandPortResolver(new UnixHandler());
  }

  final PortCommandHandler portCommandHandler;

  public CommandPortResolver(PortCommandHandler portCommandHandler) {
    this.portCommandHandler = portCommandHandler;
  }

  @Override
  public List<Integer> getPortCandidates(int pid) {
    int status = 0;
    List<Integer> ans = new ArrayList<>();
    String command = portCommandHandler.createCommand(pid);
    try {
      Process proc = new ProcessBuilder()
        .command(command.split(" "))
        .redirectErrorStream(true)
        .start();
      BufferedReader script = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      String line;
      Function<String, Optional<Integer>> parser = portCommandHandler.outputParser(pid);
      while ((line = script.readLine()) != null) {
        parser.apply(line).ifPresent(ans::add);
      }
      status = proc.waitFor();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (status != 0) {
      System.err.println("[attachme] The command " + command + " finished with status code " + status);
    }

    if (ans.isEmpty()) {
      System.err.println("[attachme] Command " + command + " could not find open ports");
    }
    return ans;
  }
}
