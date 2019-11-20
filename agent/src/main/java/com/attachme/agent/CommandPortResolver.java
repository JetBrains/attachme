package com.attachme.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandPortResolver implements PortResolver {

  public static PortResolver forUnix() {
    return new CommandPortResolver("lsof -a -iTCP -Pi -p ${PID}", ":([0-9]+)\\s\\(LISTEN\\)", 1);
  }

  // TODO
  public static PortResolver forWindows() {
    return null;
  }

  private final String command;
  private final Pattern regex;
  private final int regexGroup;


  public CommandPortResolver(String command, String regex, int regexGroup) {
    this.command = command;
    this.regex = Pattern.compile(regex);
    this.regexGroup = regexGroup;
  }

  @Override
  public List<Integer> getPortCandidates(int pid) {
    String output;
    int status = 0;
    try {
      Process proc = new ProcessBuilder()
        .command(command.replace("${PID}", pid + "").split(" "))
        .redirectErrorStream(true)
        .start();
      BufferedReader script = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = script.readLine()) != null) {
        sb.append(line).append(System.lineSeparator());
      }
      output = sb.toString();
      status = proc.waitFor();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (status != 0) {
      System.err.println("[attachme] The command " + command + " finished with status code " + status);
    }

    Matcher matcher = regex.matcher(output);
    List<Integer> ans = new ArrayList<>();
    while (matcher.find()) {
      String group = matcher.group(regexGroup);
      ans.add(Integer.parseInt(group));
    }
    if (ans.isEmpty()) {
      System.err.println("[attachme] Command " + command + " could not find open ports");
    }
    return ans;
  }
}
