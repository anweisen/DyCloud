package net.anweisen.cloud.base.console.jline3;

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
		while (!Thread.interrupted() && (line = readLine()) != null) {
			if (currentTask != null) {
				currentTask.complete(line);
				currentTask = null;
			}
		}
	}

	@Nullable
	private	String readLine() {
		try {
			return console.getLineReader().readLine(console.getPrompt());
		} catch (EndOfFileException ex) {
		} catch (UserInterruptException ex) {
			System.exit(-1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Nonnull
	protected Task<String> getCurrentTask() {
		if (currentTask == null) {
			currentTask = new CompletableTask<>();
		}

		return currentTask;
	}
}
