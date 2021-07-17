package net.anweisen.cloud.driver.console;

import org.fusesource.jansi.Ansi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum ConsoleColor {

	DEFAULT("default", 'r', Ansi.ansi().reset().fg(Ansi.Color.DEFAULT).boldOff().toString()),
	BLACK("black", '0', Ansi.ansi().reset().fg(Ansi.Color.BLACK).boldOff().toString()),
	DARK_BLUE("dark_blue", '1', Ansi.ansi().reset().fg(Ansi.Color.BLUE).boldOff().toString()),
	GREEN("green", '2', Ansi.ansi().reset().fg(Ansi.Color.GREEN).boldOff().toString()),
	CYAN("cyan", '3', Ansi.ansi().reset().fg(Ansi.Color.CYAN).boldOff().toString()),
	DARK_RED("dark_red", '4', Ansi.ansi().reset().fg(Ansi.Color.RED).boldOff().toString()),
	PURPLE("purple", '5', Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).boldOff().toString()),
	ORANGE("orange", '6', Ansi.ansi().reset().fg(Ansi.Color.RED).fg(Ansi.Color.YELLOW).boldOff().toString()),
	GRAY("gray", '7', Ansi.ansi().reset().fg(Ansi.Color.WHITE).boldOff().toString()),
	DARK_GRAY("dark_gray", '8', Ansi.ansi().reset().fg(Ansi.Color.BLACK).bold().toString()),
	BLUE("blue", '9', Ansi.ansi().reset().fg(Ansi.Color.BLUE).bold().toString()),
	LIGHT_GREEN("light_green", 'a', Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString()),
	AQUA("aqua", 'b', Ansi.ansi().reset().fg(Ansi.Color.CYAN).bold().toString()),
	RED("red", 'c', Ansi.ansi().reset().fg(Ansi.Color.RED).bold().toString()),
	PINK("pink", 'd', Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString()),
	YELLOW("yellow", 'e', Ansi.ansi().reset().fg(Ansi.Color.YELLOW).bold().toString()),
	WHITE("white", 'f', Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString());

	private final String name;
	private final String ansiCode;
	private final char index;

	ConsoleColor(@Nonnull String name, char index, @Nonnull String ansiCode) {
		this.name = name;
		this.index = index;
		this.ansiCode = ansiCode;
	}

	@Nonnull
	public static String toColoredString(char triggerChar, @Nonnull String text) {
		for (ConsoleColor consoleColour : values()) {
			text = text.replace(triggerChar + "" + consoleColour.index, consoleColour.ansiCode);
		}

		return text;
	}

	@Nullable
	public static ConsoleColor getByChar(char index) {
		for (ConsoleColor color : values()) {
			if (color.index == index) {
				return color;
			}
		}

		return null;
	}

	@Nullable
	public static ConsoleColor getLastColor(char triggerChar, @Nonnull String text) {
		text = text.trim();
		if (text.length() > 2 && text.charAt(text.length() - 2) == triggerChar) {
			return getByChar(text.charAt(text.length() - 1));
		}

		return null;
	}

	@Nonnull
	@Override
	public String toString() {
		return this.ansiCode;
	}

	@Nonnull
	public String getName() {
		return this.name;
	}

	@Nonnull
	public String getAnsiCode() {
		return this.ansiCode;
	}

	public char getIndex() {
		return this.index;
	}
}