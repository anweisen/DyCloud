package net.anweisen.cloud.base.setup.cnl.commands;

import net.anweisen.cloud.base.setup.cnl.CNLCommand;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class EchoCNLCommand implements CNLCommand {

	@Override
	public void executeCommand(@Nonnull String[] args, @Nonnull String input, @Nonnull String commandLine) {
		System.out.println(input);
	}
}
