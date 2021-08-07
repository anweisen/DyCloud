package net.anweisen.cloud.base.module;

import net.anweisen.utilities.common.config.FileDocument;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface Module {

	@Nonnull
	ModuleController getController();

	@Nonnull
	FileDocument getConfig();

	@Nonnull
	FileDocument reloadConfig();

}
