package net.anweisen.cloud.master.command;

import net.anweisen.cloud.base.command.DefaultCommandManager;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterCommandManager extends DefaultCommandManager {

	@Override
	protected void handleCommandChange() {
		updateIngameCommands();
	}
}
