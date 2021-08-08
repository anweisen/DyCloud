package net.anweisen.cloud.driver.service.specific;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ServiceEnvironment {

	MINECRAFT   (ServiceType.SERVER,    "server.properties"),
	BUKKIT      (ServiceType.SERVER,    "server.properties", "bukkit.yml"),
	SPIGOT      (ServiceType.SERVER,    "server.properties", "bukkit.yml", "spigot.yml"),
	PAPER       (ServiceType.SERVER,    "server.properties", "bukkit.yml", "spigot.yml", "paper.yml"),
	GLOWSTONE   (ServiceType.SERVER,    "server.properties", "glowstone.yml"),
	BUNGEECORD  (ServiceType.PROXY,     "config.yml"),
	VELOCITY    (ServiceType.PROXY,     "velocity.toml");

	private final ServiceType serviceType;
	private final String[] defaultConfig;

	ServiceEnvironment(@Nonnull ServiceType serviceType, @Nonnull String... configs) {
		this.serviceType = serviceType;
		this.defaultConfig = configs;
	}

	@Nonnull
	public ServiceType getServiceType() {
		return serviceType;
	}

	@Nonnull
	public String[] getConfigs() {
		return defaultConfig;
	}

	public boolean isProxy() {
		return serviceType == ServiceType.PROXY;
	}

	public boolean isServer() {
		return serviceType == ServiceType.SERVER;
	}
}
