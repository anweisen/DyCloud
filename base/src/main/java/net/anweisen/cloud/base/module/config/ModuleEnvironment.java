package net.anweisen.cloud.base.module.config;

import net.anweisen.cloud.driver.DriverEnvironment;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ModuleEnvironment {

	ALL(environment -> true),
	MASTER,
	NODE,
	NONE(environment -> false);

	private final Predicate<DriverEnvironment> filter;

	ModuleEnvironment(@Nonnull Predicate<DriverEnvironment> filter) {
		this.filter = filter;
	}

	ModuleEnvironment() {
		DriverEnvironment driverEnvironment = DriverEnvironment.valueOf(this.name());
		this.filter = (environment) -> environment == driverEnvironment;
	}

	public boolean applies(@Nonnull DriverEnvironment environment) {
		return filter.test(environment);
	}

}
