package net.anweisen.cloud.driver.event.node;

import net.anweisen.cloud.driver.event.DefaultEvent;
import net.anweisen.cloud.driver.node.NodeInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class NodeEvent extends DefaultEvent {

	private final NodeInfo node;

	public NodeEvent(@Nonnull NodeInfo node) {
		this.node = node;
	}

	@Nonnull
	public NodeInfo getNode() {
		return node;
	}
}
