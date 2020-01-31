package com.attachme.agent;

import java.io.Serializable;
import java.util.List;

public class ProcessRegisterMsg implements Serializable {
  private List<Integer> ports;

  private Integer pid;

  public List<Integer> getPorts() {
    return ports;
  }

  public void setPorts(List<Integer> ports) {
    this.ports = ports;
  }

  public Integer getPid() {
    return pid;
  }

  public void setPid(Integer pid) {
    this.pid = pid;
  }

  @Override
  public String toString() {
    return "ProcessRegisterMsg{" +
      "ports=" + ports +
      ", port=" + pid +
      '}';
  }
}
