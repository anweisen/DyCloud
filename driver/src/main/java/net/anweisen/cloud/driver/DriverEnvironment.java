package net.anweisen.cloud.driver;

/**
 * TODO docs
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum DriverEnvironment {

	MASTER(true, false),
	NODE(true, true),
	CORD(false, true),
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
