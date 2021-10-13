package net.anweisen.cloud.base.console;

import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface Console {

	boolean isPrintingEnabled();

	void setPrintingEnabled(boolean enabled);

	void setCommandInputValue(@Nonnull String commandInputValue);

	void resetPrompt();

	void removePrompt();

	void emptyPrompt();

	void clearScreen();

	void close() throws Exception;

	@Nonnull
	String getPrompt();

	void setPrompt(@Nonnull String prompt);

	@Nonnull
	String getScreenName();

	void setScreenName(@Nonnull String screen);

	@Nonnull
	Task<String> readLine();

	@Nonnull
	Console writeRaw(@Nonnull String rawText);

	@Nonnull
	Console forceWrite(@Nonnull String text);

	@Nonnull
	Console forceWriteLine(@Nonnull String text);

	@Nonnull
	Console write(@Nonnull String text);

	@Nonnull
	Console writeLine(@Nonnull String text);

}
