package com.attachme.plugin.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class AttachmeRunConfType implements ConfigurationType {
	@NotNull
	@Override
	public String getDisplayName() {
		return "Attachme debugger registry";
	}

	@Nls
	@Override
	public String getConfigurationTypeDescription() {
		return getDisplayName();
	}

	@Override
	public Icon getIcon() {
		return AllIcons.Debugger.Db_watch;
	}

	@NotNull
	@Override
	public String getId() {
		return "ATTACHME_RUN_CONFIG";
	}

	@Override
	public ConfigurationFactory[] getConfigurationFactories() {
		return new AttachmeRunFactory[]{new AttachmeRunFactory(this)};
	}
}
