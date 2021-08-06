package net.anweisen.cloud.driver.service.specific;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum ServiceType {

	SERVER(30000),

	PROXY(25565);

	private final int startPort;

	ServiceType(int startPort) {
		this.startPort = startPort;
	}

	public int getStartPort() {
		return startPort;
	}
}
