package net.anweisen.cloud.driver.service.specific;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ServiceEnvironment {

	MINECRAFT   (ServiceType.SERVER,    "plugins",  "server.properties"),
	BUKKIT      (ServiceType.SERVER,    "plugins",  "server.properties", "bukkit.yml"),
	SPIGOT      (ServiceType.SERVER,    "plugins",  "server.properties", "bukkit.yml", "spigot.yml"),
	PAPER       (ServiceType.SERVER,    "plugins",  "server.properties", "bukkit.yml", "spigot.yml", "paper.yml"),
	GLOWSTONE   (ServiceType.SERVER,    "plugins",  "server.properties", "glowstone.yml"),
	BUNGEECORD  (ServiceType.PROXY,     "plugins",  "config.yml"),
	VELOCITY    (ServiceType.PROXY,     "plugins",  "velocity.toml");

	private final ServiceType serviceType;
	private final String pluginsFolder;
	private final String[] defaultConfig;

	ServiceEnvironment(@Nonnull ServiceType serviceType, @Nonnull String pluginsFolder, @Nonnull String... configs) {
		this.serviceType = serviceType;
		this.pluginsFolder = pluginsFolder;
		this.defaultConfig = configs;
	}

	@Nonnull
	public ServiceType getServiceType() {
		return serviceType;
	}

	@Nonnull
	public String getPluginsFolder() {
		return pluginsFolder;
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
