package com.attachme.agent;


import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PortCommandHandler {

  abstract Function<String, Optional<Integer>> outputParser(int pid);

  abstract String createCommand(int pid);

  protected Function<String, Optional<Integer>> parserByRegex(Pattern r, int grId) {
    return str -> {
      Matcher m = r.matcher(str);
      if (m.find()) {
        String group = m.group(grId);
        return Optional.of(Integer.parseInt(group));
      } else return Optional.empty();
    };
  }

}
