package net.anweisen.cloud.driver.node;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.NodeInfoPublishPacket.NodePublishType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
		return getNodeInfos().stream().filter(info -> info.getName().equals(name)).findFirst().orElse(null);
	}

	@Nonnull
	default Collection<String> getNodeNames() {
		return getNodeInfos().stream().map(NodeInfo::getName).collect(Collectors.toList());
	}

	void handleNodeUpdate(@Nonnull NodePublishType publishType, @Nonnull NodeInfo info);

}
