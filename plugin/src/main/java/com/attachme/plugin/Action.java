package com.attachme.plugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class Action extends com.intellij.openapi.actionSystem.AnAction {
	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		Project project = event.getProject();
		Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon());
	}
}
