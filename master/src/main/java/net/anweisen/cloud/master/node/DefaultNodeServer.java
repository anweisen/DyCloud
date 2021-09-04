package net.anweisen.cloud.master.node;

import net.anweisen.cloud.base.node.NodeCycleData;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.node.NodeInfo;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultNodeServer implements NodeServer {

	private final NodeInfo info;
	private final SocketChannel channel;
	private NodeCycleData cycleData;

	public DefaultNodeServer(@Nonnull NodeInfo info, @Nonnull SocketChannel channel) {
		this.info = info;
		this.channel = channel;
	}

	@Nonnull
	@Override
	public SocketChannel getChannel() {
		return channel;
	}

//	@Override
//	public void setChannel(@Nonnull SocketChannel channel) {
//		Preconditions.checkNotNull(channel);
//		this.channel = channel;
//	}

	@Nonnull
	@Override
	public NodeInfo getInfo() {
		return info;
	}

	@Nullable
	@Override
	public NodeCycleData getLastCycleData() {
		return cycleData;
	}

	@Override
	public void setLastCycleData(@Nonnull NodeCycleData data) {
		this.cycleData = data;
	}

	@Override
	public boolean isAvailable() {
		return cycleData != null && !cycleData.hasTimeouted();
	}

	@Nonnull
	@Override
	public Collection<CloudService> findServices() {
		return CloudMaster.getInstance().getServiceManager().getServicesByNode(info.getName());
	}

	@Nonnull
	@Override
	public Collection<ServiceInfo> findServiceInfos() {
		return CloudMaster.getInstance().getServiceManager().getServiceInfosByNode(info.getName());
	}

	@Override
	public String toString() {
		return "NodeServer[name=" + info.getName() + " address=" + info.getAddress() + (!isAvailable() ? " available=false" : "") + " data=" + cycleData + "]";
	}
}
