package net.anweisen.cloud.driver.console;

import net.anweisen.utilities.common.concurrent.task.CompletableTask;
import net.anweisen.utilities.common.concurrent.task.Task;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConsoleReadThread extends Thread {

	private final JLine3Console console;
	private CompletableTask<String> currentTask;

	public ConsoleReadThread(@Nonnull JLine3Console console) {
		this.console = console;
	}

	@Override
	public void run() {
		String line;
		while (!Thread.interrupted() && (line = this.readLine()) != null) {
			if (this.currentTask != null) {
				this.currentTask.complete(line);
				this.currentTask = null;
			}
		}
	}

	@Nullable
	private	String readLine() {
		try {
			return this.console.getLineReader().readLine(this.console.getPrompt());
		} catch (EndOfFileException ignored) {
		} catch (UserInterruptException exception) {
			System.exit(-1);
		}

		return null;
	}

	@Nonnull
	protected Task<String> getCurrentTask() {
		if (this.currentTask == null) {
			this.currentTask = new CompletableTask<>();
		}

		return this.currentTask;
	}
}
