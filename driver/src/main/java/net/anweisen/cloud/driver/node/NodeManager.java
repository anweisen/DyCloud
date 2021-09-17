package net.anweisen.cloud.driver.node;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.NodeInfoPublishPacket.NodePublishType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudDriver#getNodeManager()
 */
public interface NodeManager {

	@Nonnull
	List<NodeInfo> getNodeInfos();

	@Nullable
	default NodeInfo getNodeInfo(@Nonnull String name) {
		for (NodeInfo node : getNodeInfos()) {
			if (node.getName().equalsIgnoreCase(name))
				return node;
		}
		return null;
	}

	@Nonnull
	default Collection<String> getNodeNames() {
		Collection<NodeInfo> infos = getNodeInfos();
		Collection<String> names = new ArrayList<>(infos.size());
		for (NodeInfo info : infos) {
			names.add(info.getName());
		}
		return names;
	}

	void handleNodeUpdate(@Nonnull NodePublishType publishType, @Nonnull NodeInfo info);

}
