package com.attachme.plugin;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/* FIXME: com.intellij.diagnostic.PluginException: The default implementation of method 'getId' is deprecated, you need to override it in 'class com.attachme.plugin.AttachmeRunFactory'. The default implementation delegates to 'getName' which may be localized, but return value of this method must not depend on current localization. [Plugin: com.attachme] */
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
