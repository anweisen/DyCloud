package net.anweisen.cloud.base.setup.cnl;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CNLCommand {

	void executeCommand(@Nonnull String[] args, @Nonnull String input, @Nonnull String commandLine);

}
