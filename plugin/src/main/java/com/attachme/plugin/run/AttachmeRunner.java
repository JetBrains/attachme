package com.attachme.plugin.run;

import com.attachme.plugin.AttachmeRegistry;
import com.attachme.plugin.ProcessRegisterMsg;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

public class AttachmeRunner implements RunProfileState, AttachmeRegistry.Listener {

	final Project project;
	private MProcHandler procHandler;

	public AttachmeRunner(Project project) {
		this.project = project;
	}

	@Nullable
	@Override
	public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
		Thread thread = AttachmeRegistry.makeThread(7857, this, new AttachmeRegistry.Console() {
			@Override
			public void info(String str) {
				procHandler.notifyTextAvailable(str + System.lineSeparator(), ProcessOutputType.STDOUT);
			}

			@Override
			public void error(String str) {
				procHandler.notifyTextAvailable(str + System.lineSeparator(), ProcessOutputType.STDERR);
			}
		});
		this.procHandler = new MProcHandler(thread);
		ConsoleViewImpl console = new ConsoleViewImpl(this.project, false);
		console.attachToProcess(this.procHandler);
		thread.start();
		return new DefaultExecutionResult(console, this.procHandler);
	}

	@Override
	public void onDebuggeeProcess(ProcessRegisterMsg msg) {
		if (msg.getPorts().isEmpty()) {
			procHandler.notifyTextAvailable("Receieved message with no ports", ProcessOutputType.STDERR);
			return;
		}
		RemoteConnection config = new RemoteConnection(true, "localhost", msg.getPorts().get(0) + "", false);
		AttachmeDebugger.attach(project, config, msg.getPid());
	}

	@Override
	public void onFinished() {
		this.procHandler.shutdown();
	}

	static class MProcHandler extends ProcessHandler {

		final Thread t;

		MProcHandler(Thread t) {
			this.t = t;
		}

		@Override
		protected void destroyProcessImpl() {
			if (!t.isInterrupted())
				t.interrupt();
		}

		@Override
		protected void detachProcessImpl() {
			destroyProcessImpl();
		}

		@Override
		public boolean detachIsDefault() {
			return false;
		}

		@Nullable
		@Override
		public OutputStream getProcessInput() {
			return null;
		}

		public void shutdown() {
			super.notifyProcessTerminated(0);
		}
	}
}
