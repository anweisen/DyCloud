package net.anweisen.cloud.driver.node;

import net.anweisen.cloud.driver.network.packet.def.NodePublishPacket.NodePublishPayload;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteNodeManager extends DefaultNodeManager {

	private final List<NodeInfo> nodes = new CopyOnWriteArrayList<>();

	@Nonnull
	@Override
	public List<NodeInfo> getNodeInfos() {
		return nodes;
	}

	@Override
	public void handleNodeUpdate(@Nonnull NodePublishPayload payload, @Nonnull NodeInfo info) {
		if (payload == NodePublishPayload.CONNECTED) {
			nodes.add(info);
		} else if (payload == NodePublishPayload.DISCONNECTED) {
			nodes.remove(info);
		}

		super.handleNodeUpdate(payload, info);
	}
}
