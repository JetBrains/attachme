package com.attachme.agent;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class WindowsHandler extends PortCommandHandler {

  @Override
  public Function<String, Optional<Integer>> outputParser(int pid) {
    final Pattern p = Pattern.compile("(?i)\\s*tcp\\s*[0-9.*]*:([0-9]+)\\s*[0-9.:]*\\s*listening\\s*" + pid);
    return parserByRegex(p, 1);
  }

  @Override
  public String createCommand(int pid) {
    return "netstat -a -o -n";
  }
}
