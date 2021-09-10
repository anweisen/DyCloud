package net.anweisen.cloud.master.service.specific;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.ServicePublishType;
import net.anweisen.cloud.driver.service.specific.ServiceControlState;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterServiceController implements ServiceController {

	private final CloudService service;

	public MasterServiceController(@Nonnull CloudService service) {
		this.service = service;
	}

	@Nonnull
	@Override
	public ServiceInfo getService() {
		return service.getInfo();
	}

	@Nonnull
	@Override
	public Task<Void> startAsync() {
		return executeAction(ServiceControlType.START, ServiceControlState.STARTING);
	}

	@Nonnull
	@Override
	public Task<Void> stopAsync() {
		return executeAction(ServiceControlType.STOP, ServiceControlState.STOPPING);
	}

	@Nonnull
	@Override
	public Task<Void> restartAsync() {
		return executeAction(ServiceControlType.RESTART, ServiceControlState.RESTARTING);
	}

	@Nonnull
	@Override
	public Task<Void> killAsync() {
		return executeAction(ServiceControlType.KILL, ServiceControlState.KILLING);
	}

	@Nonnull
	@Override
	public Task<Void> deleteAsync() {
		return executeAction(ServiceControlType.DELETE, ServiceControlState.DELETING);
	}

	@Nonnull
	private Task<Void> executeAction(@Nonnull ServiceControlType type, @Nonnull ServiceControlState state) {
		service.getInfo().setControlState(state);
		CloudMaster.getInstance().publishUpdate(ServicePublishType.UPDATE, service.getInfo());
		NodeServer nodeServer = CloudMaster.getInstance().getNodeManager().getNodeServer(service.getInfo().getNodeName());
		CloudMaster.getInstance().getLogger().debug("=> {}:{} -> {} -> {}", type, state, service, nodeServer);
		Preconditions.checkNotNull(nodeServer, "NodeServer of service " + service + " is null");
		Preconditions.checkNotNull(nodeServer.getChannel(), "SocketChannel of NodeServer of service " + service + " is null");
		return nodeServer.getChannel().sendPacketQueryAsync(new ServiceControlPacket(type, service.getInfo().getUniqueId())).mapVoid();
	}

}
