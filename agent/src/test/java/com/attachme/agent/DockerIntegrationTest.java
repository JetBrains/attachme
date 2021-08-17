package com.attachme.agent;

import com.spotify.docker.client.AnsiProgressHandler;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Ignore
public class DockerIntegrationTest extends BaseIntegrationTest {

  public DockerIntegrationTest() {
    super(1, 9090);
  }

  @Test
  public void receivesMessageFromContainer() throws Exception {
    super.assertReceivesMessage();
  }

  @Override
  AutoCloseable spawnBackgroundProcess() {
    try {
      DockerClient client = DefaultDockerClient.fromEnv().build();
      String imageId = client.build(Paths.get("./"), new AnsiProgressHandler(System.out), DockerClient.BuildParam.dockerfile(Paths.get("src/test/resources/container/Dockerfile")));
      ContainerConfig conf = ContainerConfig.builder()
        .image(imageId)
        .build();
      ContainerCreation containerCreation = client.createContainer(conf);
      String containerId = containerCreation.id();
      client.startContainer(containerId);

      return () -> {
        System.out.println(client.logs(containerId, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr()).readFully());
        try {
          System.out.println("Stopping container...");
          client.stopContainer(containerId, 2);
        } catch (ContainerNotFoundException e) {
          System.out.println("Already stopped");
        }
        System.out.printf("Removing image %s\nRemoving container %s\n", imageId, containerId);
        client.removeContainer(containerId);
        client.removeImage(imageId);
      };
    } catch (DockerException | InterruptedException | IOException | DockerCertificateException e) {
      throw new RuntimeException(e);
    }
  }
}
