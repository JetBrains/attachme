package com.attachme.agent;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UnixHandler extends PortCommandHandler {

  private final Pattern p = Pattern.compile(":([0-9]+)\\s\\(LISTEN\\)");

  @Override
  Function<String, Optional<Integer>> outputParser(int pid) {
    return parserByRegex(p, 1);
  }

  @Override
  String createCommand(int pid) {
    return "lsof -a -iTCP -Pi -p " + pid;
  }

}
