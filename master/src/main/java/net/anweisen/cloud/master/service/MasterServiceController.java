package net.anweisen.cloud.master.service;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlType;
import net.anweisen.cloud.driver.service.specific.ServiceController;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;
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
		return sendPacket(ServiceControlType.START);
	}

	@Nonnull
	@Override
	public Task<Void> stopAsync() {
		return sendPacket(ServiceControlType.STOP);
	}

	@Nonnull
	@Override
	public Task<Void> restartAsync() {
		return sendPacket(ServiceControlType.RESTART);
	}

	@Nonnull
	@Override
	public Task<Void> killAsync() {
		return sendPacket(ServiceControlType.KILL);
	}

	@Nonnull
	@Override
	public Task<Void> deleteAsync() {
		return sendPacket(ServiceControlType.DELETE);
	}

	@Nonnull
	private Task<Void> sendPacket(@Nonnull ServiceControlType type) {
		NodeServer nodeServer = CloudMaster.getInstance().getNodeManager().getNodeServer(service.getInfo().getNodeName());
		Preconditions.checkNotNull(nodeServer, "NodeServer of service is null");
		Preconditions.checkNotNull(nodeServer.getChannel(), "SocketChannel of NodeServer of service is null");
		return nodeServer.getChannel().sendQueryAsync(new ServiceControlPacket(type, service.getInfo().getUniqueId())).mapVoid();
	}

}
