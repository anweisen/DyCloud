package net.anweisen.cloud.driver.console.jline3;

import net.anweisen.cloud.driver.console.Console;
import net.anweisen.cloud.driver.console.ConsoleColor;
import net.anweisen.utilities.common.concurrent.task.Task;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class JLine3Console implements Console {

	private final ConsoleReadThread consoleReadThread = new ConsoleReadThread(this);
	private final ExecutorService animationThreadPool = Executors.newCachedThreadPool();

	private final Terminal terminal;
	private final LineReaderImpl lineReader;

	private final String promptTemplate = System.getProperty("cloud.console.prompt", "&8| &bCloud &8» &b%screen% &8» &r");
	private String prompt = null;
	private String screenName = "Console";
	private boolean printingEnabled = true;

	public JLine3Console() throws Exception {
		System.setProperty("library.jansi.version", "MinecraftCloud");

		try {
			AnsiConsole.systemInstall();
		} catch (Throwable ignored) {
		}

		this.terminal = TerminalBuilder.builder().system(true).encoding(StandardCharsets.UTF_8).build();
		this.lineReader = new InternalLineReaderBuilder(this.terminal)
//			.completer(new JLine3Completer(this))
			.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
			.variable(LineReader.BELL_STYLE, "off")
			.build();

		this.resetPrompt();
		this.consoleReadThread.start();
	}

	@Override
	public boolean isPrintingEnabled() {
		return printingEnabled;
	}

	@Override
	public void setPrintingEnabled(boolean enabled) {
		this.printingEnabled = enabled;
	}

	@Override
	public void setCommandInputValue(@Nonnull String commandInputValue) {
		this.lineReader.getBuffer().write(commandInputValue);
	}

	@Nonnull
	@Override
	public Task<String> readLine() {
		return this.consoleReadThread.getCurrentTask();
	}

	@Nonnull
	@Override
	public Console write(@Nonnull String text) {
		if (printingEnabled) {
			forceWrite(text);
		}

		return this;
	}

	@Nonnull
	@Override
	public Console writeLine(@Nonnull String text) {
		if (printingEnabled) {
			forceWriteLine(text);
		}

		return this;
	}

	@Nonnull
	@Override
	public Console forceWrite(@Nonnull String text) {
		return writeRaw(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + text + ConsoleColor.DEFAULT);
	}

	@Nonnull
	@Override
	public Console writeRaw(@Nonnull String rawText) {
		print(rawText);
		return this;
	}

	@Nonnull
	@Override
	public Console forceWriteLine(@Nonnull String text) {
		if (!text.endsWith(System.lineSeparator())) {
			text += System.lineSeparator();
		}

		print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + text + Ansi.ansi().reset().toString());
		return this;
	}

	@Override
	public void resetPrompt() {
		prompt = promptTemplate;
		updatePrompt();
	}

	@Override
	public void removePrompt() {
		prompt = null;
		updatePrompt();
	}

	@Override
	public void emptyPrompt() {
		prompt = ConsoleColor.DEFAULT.toString();
		updatePrompt();
	}

	@Override
	public void clearScreen() {
		terminal.puts(InfoCmp.Capability.clear_screen);
		terminal.flush();
	}

	@Override
	public void close() throws Exception {
		animationThreadPool.shutdownNow();
		consoleReadThread.interrupt();

		terminal.flush();
		terminal.close();

		AnsiConsole.systemUninstall();
	}

	@Nonnull
	@Override
	public String getPrompt() {
		return prompt;
	}

	@Override
	public void setPrompt(@Nonnull String prompt) {
		this.prompt = prompt;
		updatePrompt();
	}

	@Nonnull
	@Override
	public String getScreenName() {
		return screenName;
	}

	@Override
	public void setScreenName(@Nonnull String screenName) {
		this.screenName = screenName;
		resetPrompt();
	}

	private void updatePrompt() {
		prompt = ConsoleColor.toColoredString('&', prompt).replace("%screen%", screenName);
		lineReader.setPrompt(this.prompt);
	}

	private void print(@Nonnull String text) {
		lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
		lineReader.getTerminal().writer().print(text);
		lineReader.getTerminal().writer().flush();

		redisplay();
	}

	private void redisplay() {
		if (!lineReader.isReading()) {
			return;
		}

		lineReader.callWidget(LineReader.REDRAW_LINE);
		lineReader.callWidget(LineReader.REDISPLAY);
	}

	@Nonnull
	protected LineReader getLineReader() {
		return lineReader;
	}

	private final class InternalLineReader extends LineReaderImpl {

		private InternalLineReader(Terminal terminal, String appName, Map<String, Object> variables) {
			super(terminal, appName, variables);
		}

		@Override
		protected boolean historySearchBackward() {
			if (history.previous()) {
				setBuffer(this.history.current());
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean historySearchForward() {
			if (history.next()) {
				setBuffer(this.history.current());
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean upLineOrSearch() {
			return historySearchBackward();
		}

		@Override
		protected boolean downLineOrSearch() {
			return historySearchForward();
		}
	}

	private final class InternalLineReaderBuilder {

		private final Terminal terminal;
		private final Map<String, Object> variables = new HashMap<>();
		private final Map<LineReader.Option, Boolean> options = new HashMap<>();
		private Completer completer;

		private InternalLineReaderBuilder(@Nonnull Terminal terminal) {
			this.terminal = terminal;
		}

		@Nonnull
		public InternalLineReaderBuilder variable(@Nonnull String name, @Nonnull Object value) {
			variables.put(name, value);
			return this;
		}

		@Nonnull
		public InternalLineReaderBuilder option(@Nonnull LineReader.Option option, boolean value) {
			options.put(option, value);
			return this;
		}

		@Nonnull
		public InternalLineReaderBuilder completer(@Nonnull Completer completer) {
			this.completer = completer;
			return this;
		}

		@Nonnull
		public InternalLineReader build() {
			InternalLineReader reader = new InternalLineReader(terminal, "CloudConsole", variables);
			if (completer != null) {
				reader.setCompleter(completer);
			}

			for (Map.Entry<LineReader.Option, Boolean> e : options.entrySet()) {
				reader.option(e.getKey(), e.getValue());
			}

			return reader;
		}
	}

}
