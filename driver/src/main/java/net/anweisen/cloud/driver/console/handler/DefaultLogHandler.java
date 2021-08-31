package net.anweisen.cloud.driver.console.handler;

import net.anweisen.utilities.common.logging.handler.LogEntry;
import net.anweisen.utilities.common.logging.handler.LogHandler;

import javax.annotation.Nonnull;
import java.io.PrintStream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultLogHandler implements LogHandler {

	private final PrintStream out = System.out, err = System.err;

	@Override
	public void handle(@Nonnull LogEntry entry) {
		PrintStream stream = entry.getLevel().isColorized() ? err : out;
		stream.println(MessageFormatter.formatUncolored(entry));
	}

}
