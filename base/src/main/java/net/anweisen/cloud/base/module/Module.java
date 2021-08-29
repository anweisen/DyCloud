package net.anweisen.cloud.base.module;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface Module {

	@Nonnull
	ModuleController getController();

}
