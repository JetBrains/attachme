package com.attachme.plugin.attach;

import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.attach.XAttachDebugger;
import com.intellij.xdebugger.attach.XAttachDebuggerProvider;
import com.intellij.xdebugger.attach.XAttachHost;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AttachmeAttachDebuggerProvider implements XAttachDebuggerProvider {
	@Override
	public boolean isAttachHostApplicable(@NotNull XAttachHost attachHost) {
		return false;
	}

	@NotNull
	@Override
	public List<XAttachDebugger> getAvailableDebuggers(@NotNull Project project, @NotNull XAttachHost hostInfo, @NotNull ProcessInfo process, @NotNull UserDataHolder contextHolder) {
		return null;
	}
}
