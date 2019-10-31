package com.attachme.plugin;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

class AttachmeRunFactory extends ConfigurationFactory {

  protected AttachmeRunFactory(@NotNull ConfigurationType type) {
    super(type);
  }

  @NotNull
  @Override
  public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
    return new AttachmeRunConfig(project, this, "ATTACHME_RUN_CONFIG");
  }

  @NotNull
  @Override
  public RunConfigurationSingletonPolicy getSingletonPolicy() {
    return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
  }
}
