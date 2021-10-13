package net.anweisen.cloud.base.command;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum CommandScope {

	CONSOLE,
	CONSOLE_AND_INGAME,
	INGAME;

	private final boolean console, ingame;

	CommandScope() {
		console = name().contains("CONSOLE");
		ingame = name().contains("INGAME");
	}

	public boolean isConsole() {
		return console;
	}

	public boolean isIngame() {
		return ingame;
	}

	public boolean hasCloudPrefix() {
		return this == CONSOLE_AND_INGAME;
	}
}
