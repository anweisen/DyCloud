package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyTabListConfig {

	private List<ProxyTabListEntryConfig> frames;
	private double animationInterval;

	private ProxyTabListConfig() {
	}

	public ProxyTabListConfig(@Nonnull List<ProxyTabListEntryConfig> frames, double animationInterval) {
		this.frames = frames;
		this.animationInterval = animationInterval;
	}

	@Nonnull
	public List<ProxyTabListEntryConfig> getFrames() {
		return frames;
	}

	public double getAnimationInterval() {
		return animationInterval;
	}
}
