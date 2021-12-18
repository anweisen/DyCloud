package net.anweisen.cloud.base.console.handler;

import net.anweisen.cloud.base.console.Console;
import net.anweisen.utility.common.logging.handler.LogEntry;
import net.anweisen.utility.common.logging.handler.LogHandler;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ConsoleLogHandler implements LogHandler {

	private final Console console;

	public ConsoleLogHandler(@Nonnull Console console) {
		this.console = console;
	}

	@Override
	public void handle(@Nonnull LogEntry entry) {
		console.writeLine(ColoredMessageFormatter.format(entry));
	}

}
