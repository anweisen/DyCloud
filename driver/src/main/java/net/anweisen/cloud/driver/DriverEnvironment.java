package net.anweisen.cloud.driver;

/**
 * The DriverEnvironment indicates in what cloud application the driver is embedded in.
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getEnvironment()
 */
public enum DriverEnvironment {

	MASTER(true, false),
	NODE(true, true),
	WRAPPER(false, true);

	private final boolean base;
	private final boolean remote;

	DriverEnvironment(boolean base, boolean remote) {
		this.base = base;
		this.remote = remote;
	}

	public boolean isBase() {
		return base;
	}

	public boolean isRemote() {
		return remote;
	}
}
