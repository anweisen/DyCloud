package net.anweisen.cloud.base.module.config;

import net.anweisen.cloud.base.module.ModuleController;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ModuleState {

	/**
	 * @see ModuleController#loadModule()
	 */
	LOADED,

	/**
	 * @see ModuleController#enableModule()
	 */
	ENABLED,

	/**
	 * @see ModuleController#disableModule()
	 */
	DISABLED,

	/**
	 * @see ModuleController#unregisterModule()
	 */
	UNREGISTERED

}
