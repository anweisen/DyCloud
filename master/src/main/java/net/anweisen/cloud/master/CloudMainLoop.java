package net.anweisen.cloud.master;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudMainLoop implements Runnable {

	private final CloudMaster cloud;

	public CloudMainLoop(@Nonnull CloudMaster cloud) {
		this.cloud = cloud;
	}

	@Override
	public void run() {

	}

}
