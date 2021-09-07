package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.service.config.ServiceTask;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ServiceTask#getEnvironment()
 * @see ServiceInfo#getEnvironment()
 */
public enum ServiceEnvironment {

	MINECRAFT   (ServiceType.SERVER,    false,  "plugins",  "server.properties"),
	BUKKIT      (ServiceType.SERVER,    true,   "plugins",  "server.properties", "bukkit.yml"),
	SPIGOT      (ServiceType.SERVER,    true,   "plugins",  "server.properties", "bukkit.yml", "spigot.yml"),
	PAPER       (ServiceType.SERVER,    true,   "plugins",  "server.properties", "bukkit.yml", "spigot.yml", "paper.yml"),
	GLOWSTONE   (ServiceType.SERVER,    false,  "plugins",  "server.properties", "glowstone.yml"),
	BUNGEECORD  (ServiceType.PROXY,     true,   "plugins",  "config.yml"),
	VELOCITY    (ServiceType.PROXY,     false,  "plugins",  "velocity.toml"),
	OTHER       (ServiceType.OTHER,     false,  "plugins");

	private final ServiceType serviceType;
	private final String pluginsFolder;
	private final String[] defaultConfig;
	private final boolean hasBridge;

	ServiceEnvironment(@Nonnull ServiceType serviceType, boolean hasBridge, @Nonnull String pluginsFolder, @Nonnull String... configs) {
		this.serviceType = serviceType;
		this.hasBridge = hasBridge;
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

	public boolean hasBridge() {
		return hasBridge;
	}
}
