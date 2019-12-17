package com.attachme.plugin;

import com.attachme.plugin.AttachmeRunTask;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AttachmeInstaller {

  final AttachmeRunTask.Console log;

  public AttachmeInstaller(AttachmeRunTask.Console log) {
    this.log = log;
  }

  public void verifyInstallation() throws IOException {
    try {
      doVerifyInstallation();
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      throw e;
    }
  }

  private void doVerifyInstallation() throws IOException {
    List<String> files = new ArrayList<>();
    files.add(packedJarName());
    files.add("conf.sh");

    File installDir = getInstallDir();
    if (installDir.exists() && !installDir.isDirectory())
      throw new RuntimeException("A file under installation dir exists and is not a directory, please remove it " + installDir);
    if (!installDir.exists() && !installDir.mkdirs()) {
      throw new RuntimeException("Could not create installation dir " + installDir);
    }

    boolean installed = true;
    for (String file : files) {
      File f = new File(installDir, file);
      if (!f.exists()) {
        installed = false;
      } else if (!f.isFile()) {
        throw new RuntimeException("The file already exists, and is directory, please remove it " + f);
      }
    }

    if (!installed) {
      log.info("Exporting AttachMe JVM agent to " + installDir);
      for (String file : files) {
        exportFile(file, installDir);
      }
      log.info("Done");
    }
  }


  private File getInstallDir() {
    if (SystemInfo.isUnix) {
      String home = Objects.requireNonNull(System.getenv("HOME"), "HOME env variable is not set");
      String location = home + "/.attachme/";
      return new File(location);
    } else {
      throw new RuntimeException();
    }
  }

  private String packedJarName() {
    String version = Objects.requireNonNull(PluginManager.getPlugin(PluginId.getId("com.attachme")),
                                            "Plugin version cannot be null").getVersion();
    return String.format("attachme-agent-%s.jar", version);
  }

  private void exportFile(String name, File dir) throws IOException {
    InputStream jarAsStream = Objects
      .requireNonNull(getClass().getClassLoader().getResourceAsStream(name),
                      "Could not find the agent jar " + name + " in classpath");

    if (!dir.exists()) {
      throw new RuntimeException("Directory does not exist " + dir);
    }
    OutputStream outputStream = new FileOutputStream(new File(dir, name));
    FileUtil.copy(jarAsStream, outputStream);
  }
}