package net.anweisen.cloud.base.setup.cnl.commands;

import net.anweisen.cloud.base.setup.cnl.CNLCommand;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class VarCNLCommand implements CNLCommand {

	@Override
	public void executeCommand(@Nonnull String[] args, @Nonnull String input, @Nonnull String commandLine) {
		System.setProperty(args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
	}
}
