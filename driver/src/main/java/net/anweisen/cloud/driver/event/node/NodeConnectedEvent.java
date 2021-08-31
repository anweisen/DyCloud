package net.anweisen.cloud.driver.event.node;

import net.anweisen.cloud.driver.node.NodeInfo;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NodeConnectedEvent extends NodeEvent {

	public NodeConnectedEvent(@Nonnull NodeInfo node) {
		super(node);
	}
}
