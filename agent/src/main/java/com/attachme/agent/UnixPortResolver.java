package com.attachme.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Runs lsof to determine open ports
 * The output should look something like this
 * COMMAND PID USER FD TYPE DEVICE SIZE/OFF NODE NAME
 * java 36831 muser 3u IPv4 0xa3d79356337c9c21 0t0 TCP *:8083 (LISTEN)
 */
public class UnixPortResolver implements PortResolver {

  static String COMMAND = "lsof -a -iTCP -Pi -p ${PID}";

  @Override
  public List<Integer> getPortCandidates(int pid) {
    String output;
    int status = 0;
    try {
      Process proc = new ProcessBuilder()
        .command(COMMAND.replace("${PID}", pid + "").split(" "))
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
      System.err.println("[attachme] The command " + COMMAND + " finished with status code " + status);
    }

    Pattern p = Pattern.compile(":([0-9]+)\\s\\(LISTEN\\)");
    Matcher matcher = p.matcher(output);
    List<Integer> ans = new ArrayList<>();
    while (matcher.find()) {
      String group = matcher.group(1);
      ans.add(Integer.parseInt(group));
    }
    if (ans.isEmpty()) {
      System.err.println("[attachme] Command " + COMMAND + " could not find open ports");
    }
    return ans;
  }
}
