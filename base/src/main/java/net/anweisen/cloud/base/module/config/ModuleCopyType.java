package net.anweisen.cloud.base.module.config;

import net.anweisen.cloud.driver.service.specific.ServiceType;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ModuleCopyType {

	ALL,
	PROXY,
	SERVER,
	NONE;

	public boolean applies(@Nonnull ServiceType type) {
		switch (this) {
			case ALL:       return true;
			case PROXY:     return type == ServiceType.PROXY;
			case SERVER:    return type == ServiceType.SERVER;
			case NONE:
			default:        return false;
		}
	}

}
