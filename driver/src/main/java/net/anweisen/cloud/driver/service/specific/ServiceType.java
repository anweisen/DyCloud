package net.anweisen.cloud.driver.service.specific;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ServiceEnvironment#getServiceType()
 */
public enum ServiceType {

	SERVER(30000),
	PROXY(25565),
	OTHER(0);

	private int startPort;

	ServiceType(int defaultStartPort) {
		this.startPort = defaultStartPort;
	}

	public int getStartPort() {
		return startPort;
	}

	public void setStartPort(int startPort) {
		this.startPort = startPort;
	}
}
