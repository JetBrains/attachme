package com.attachme.plugin;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.SimpleConfigurationType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NotNull;


public class AttachmeRunConfType extends SimpleConfigurationType {

  private static final String name = "Attachme Debugger Registry";

  protected AttachmeRunConfType() {
    super("ATTACHME_RUN_CONFIG",
          name,
          name,
          NotNullLazyValue.createConstantValue(AllIcons.Debugger.AttachToProcess));
  }

  @Override
  public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
    return new AttachmeRunConfig(project, this, name);
  }
}
