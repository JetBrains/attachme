package com.attachme.plugin.run;

import com.intellij.debugger.engine.RemoteStateState;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AttachmeDebugger {

	private AttachmeDebugger() {
	}

	public static void attach(Project project, RemoteConnection con, Integer pid) {
		RunnerAndConfigurationSettings runSettings =
			RunManager.getInstance(project).createConfiguration("Attachme pid: " + pid, ProcessAttachRunConfigurationType.FACTORY);
		((ProcessAttachRunConfiguration) runSettings.getConfiguration()).connection = con;
		ProgramRunnerUtil.executeConfiguration(runSettings, new ProcessAttachDebugExecutor());
	}

	public static class ProcessAttachDebugExecutor extends DefaultDebugExecutor {
		@NotNull
		@Override
		public String getId() {
			return "ProcessAttachDebugExecutor";
		}
	}

	public static class ProcessAttachDebuggerRunner extends GenericDebuggerRunner {
		@NotNull
		@Override
		public String getRunnerId() {
			return "ProcessAttachDebuggerRunner";
		}

		@Nullable
		@Override
		protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment)
			throws ExecutionException {
			return attachVirtualMachine(state, environment, ((RemoteState) state).getRemoteConnection(), false);
		}

		@Override
		public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
			return executorId.equals("ProcessAttachDebugExecutor");
		}
	}

	public static class ProcessAttachRunConfiguration extends RunConfigurationBase<Element> {
		RemoteConnection connection;

		protected ProcessAttachRunConfiguration(@NotNull Project project) {
			super(project, ProcessAttachRunConfigurationType.FACTORY, "ProcessAttachRunConfiguration");
		}

		@NotNull
		@Override
		public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
			throw new IllegalStateException("Editing is not supported");
		}

		@Nullable
		@Override
		public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
			if (connection == null) throw new NullPointerException();
			return new RemoteStateState(getProject(), connection);
		}
	}

	public static final class ProcessAttachRunConfigurationType implements ConfigurationType {
		static final ProcessAttachRunConfigurationType INSTANCE = new ProcessAttachRunConfigurationType();
		static final ConfigurationFactory FACTORY = new ConfigurationFactory(INSTANCE) {
			@NotNull
			@Override
			public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
				return new ProcessAttachRunConfiguration(project);
			}
		};

		@NotNull
		@Nls
		@Override
		public String getDisplayName() {
			return getId();
		}

		@Nls
		@Override
		public String getConfigurationTypeDescription() {
			return getId();
		}

		@Override
		public Icon getIcon() {
			return null;
		}

		@NotNull
		@Override
		public String getId() {
			return "ProcessAttachRunConfigurationType";
		}

		@Override
		public ConfigurationFactory[] getConfigurationFactories() {
			return new ConfigurationFactory[]{FACTORY};
		}

		@Override
		public String getHelpTopic() {
			return "reference.dialogs.rundebug.ProcessAttachRunConfigurationType";
		}
	}
}
