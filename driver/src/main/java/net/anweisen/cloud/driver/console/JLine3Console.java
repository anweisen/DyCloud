package net.anweisen.cloud.driver.console;

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

		this.updatePrompt();
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
		if (this.printingEnabled) {
			this.forceWrite(text);
		}

		return this;
	}

	@Nonnull
	@Override
	public Console writeLine(@Nonnull String text) {
		if (this.printingEnabled) {
			this.forceWriteLine(text);
		}

		return this;
	}

	@Nonnull
	@Override
	public Console forceWrite(@Nonnull String text) {
		return this.writeRaw(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + text + ConsoleColor.DEFAULT);
	}

	@Nonnull
	@Override
	public Console writeRaw(@Nonnull String rawText) {
		this.print(ConsoleColor.toColoredString('&', rawText));
		return this;
	}

	@Nonnull
	@Override
	public Console forceWriteLine(@Nonnull String text) {
		text = ConsoleColor.toColoredString('&', text);
		if (!text.endsWith(System.lineSeparator())) {
			text += System.lineSeparator();
		}

		this.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + text + Ansi.ansi().reset().toString());
		return this;
	}

	@Override
	public void resetPrompt() {
		this.prompt = System.getProperty("cloudnet.console.prompt", "&c%user%&r@&7%screen% &f=> &r");
		this.updatePrompt();
	}

	@Override
	public void removePrompt() {
		this.prompt = null;
		this.updatePrompt();
	}

	@Override
	public void emptyPrompt() {
		this.prompt = ConsoleColor.DEFAULT.toString();
		this.updatePrompt();
	}

	@Override
	public void clearScreen() {
		this.terminal.puts(InfoCmp.Capability.clear_screen);
		this.terminal.flush();
	}

	@Override
	public void close() throws Exception {
		this.animationThreadPool.shutdownNow();
		this.consoleReadThread.interrupt();

		this.terminal.flush();
		this.terminal.close();

		AnsiConsole.systemUninstall();
	}

	@Nonnull
	@Override
	public String getPrompt() {
		return this.prompt;
	}

	@Override
	public void setPrompt(@Nonnull String prompt) {
		this.prompt = prompt;
		this.updatePrompt();
	}

	@Nonnull
	@Override
	public String getScreenName() {
		return screenName;
	}

	@Override
	public void setScreenName(@Nonnull String screenName) {
		this.screenName = screenName;
		this.updatePrompt();
	}

	private void updatePrompt() {
		this.prompt = ConsoleColor.toColoredString('&', this.promptTemplate)
			.replace("%screen%", this.screenName);
		this.lineReader.setPrompt(this.prompt);
	}

	private void print(@Nonnull String text) {
		this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
		this.lineReader.getTerminal().puts(InfoCmp.Capability.clr_eol);
		this.lineReader.getTerminal().writer().print(text);
		this.lineReader.getTerminal().writer().flush();

		this.redisplay();
	}

	private void redisplay() {
		if (!this.lineReader.isReading()) {
			return;
		}

		this.lineReader.callWidget(LineReader.REDRAW_LINE);
		this.lineReader.callWidget(LineReader.REDISPLAY);
	}

	@Nonnull
	protected LineReader getLineReader() {
		return this.lineReader;
	}

	private final class InternalLineReader extends LineReaderImpl {

		private InternalLineReader(Terminal terminal, String appName, Map<String, Object> variables) {
			super(terminal, appName, variables);
		}

		@Override
		protected boolean historySearchBackward() {
			if (this.history.previous()) {
				this.setBuffer(this.history.current());
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean historySearchForward() {
			if (this.history.next()) {
				this.setBuffer(this.history.current());
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean upLineOrSearch() {
			return this.historySearchBackward();
		}

		@Override
		protected boolean downLineOrSearch() {
			return this.historySearchForward();
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
			this.variables.put(name, value);
			return this;
		}

		@Nonnull
		public InternalLineReaderBuilder option(@Nonnull LineReader.Option option, boolean value) {
			this.options.put(option, value);
			return this;
		}

		@Nonnull
		public InternalLineReaderBuilder completer(@Nonnull Completer completer) {
			this.completer = completer;
			return this;
		}

		@Nonnull
		public InternalLineReader build() {
			InternalLineReader reader = new InternalLineReader(this.terminal, "CloudConsole", this.variables);
			if (this.completer != null) {
				reader.setCompleter(this.completer);
			}

			for (Map.Entry<LineReader.Option, Boolean> e : this.options.entrySet()) {
				reader.option(e.getKey(), e.getValue());
			}

			return reader;
		}
	}

}
