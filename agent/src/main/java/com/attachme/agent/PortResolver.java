package com.attachme.agent;

import java.util.List;

public interface PortResolver {
  List<Integer> getPortCandidates(int pid);

  static PortResolver createPerOS() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("windows")) {
      return new WindowsPortResolver();
    } else if (os.contains("linux") || os.contains("mac") || os.contains("darwin")) {
      return new UnixPortResolver();
    } else {

      return null;
    }
  }

}
