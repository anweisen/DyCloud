package net.anweisen.cloud.master.service;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlPayload;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishPayload;
import net.anweisen.cloud.driver.service.ServiceFactory;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceControlState;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceState;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;
import net.anweisen.cloud.master.service.specific.DefaultCloudService;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterServiceFactory implements ServiceFactory, LoggingApiUser {

	@Nullable
	@Override
	public ServiceInfo createService(@Nonnull ServiceTask task) {
		return createServiceAsync(task).getOrDefault(null);
	}

	@Nonnull
	@Override
	public synchronized Task<ServiceInfo> createServiceAsync(@Nonnull ServiceTask task) {
		CloudMaster cloud = CloudMaster.getInstance();

		debug("Creating new service of '{}'..", task.getName());

		int taskServicesRunning = task.findServices().size();

		if (task.getMaxCount() > 0 && taskServicesRunning >= task.getMaxCount()) {
			cloud.getLogger().warn("The max service count for '{}' of {} is already reached", task.getName(), task.getMaxCount());
			return Task.empty();
		}

		Collection<String> nodes = new ArrayList<>(task.getNodes());
		if (nodes.isEmpty())
			nodes.addAll(cloud.getNodeManager().getNodeNames());

		List<NodeServer> allowedNodes = new ArrayList<>();
		for (String node : nodes) {
			NodeServer server = cloud.getNodeManager().getNodeServer(node);
			if (server == null) continue;
			if (!server.isAvailable()) continue;

			allowedNodes.add(server);
		}

		if (allowedNodes.isEmpty()) {
			warn("No nodes are available to start a new service for {}", task);
			return Task.empty();
		}

		NodeServer node = allowedNodes.get(0); // TODO choose based on running services and load
		debug("Chosen '{}' to start new service of '{}'", node.getInfo().getName(), task.getName());

		int serviceNumber = 1;
		while (cloud.getServiceManager().getServiceByName(task.getName() + "-" +  serviceNumber) != null)
			serviceNumber++;

		int port = getNextFreePort(node, task);
		debug("Chosen port {} to start new service of '{}' on '{}'", port, task.getName(), node.getInfo().getName());
		ServiceInfo info = new ServiceInfo(
				UUID.randomUUID(), null, task.getName(), serviceNumber, task.getEnvironment(),
				ServiceState.DEFINED, ServiceControlState.CREATING, false, node.getInfo().getName(), node.getInfo().getAddress().getHost(),
				port, true, Document.create()
		);

		CloudService service = new DefaultCloudService(info);
		cloud.getServiceManager().registerService(service);
		cloud.publishUpdate(ServicePublishPayload.REGISTER, info, node.getChannel());
		cloud.getServiceManager().handleServiceUpdate(ServicePublishPayload.REGISTER, info);

		info("Told '{}' to create '{}'", node.getInfo().getName(), info.getName());
		extended("- {}", node);
		extended("- {}", info);
		extended("- {}", task);

		return node.getChannel().sendPacketQueryAsync(new ServiceControlPacket(ServiceControlPayload.CREATE, info)).map(packet -> {
			ServiceInfo response = packet.getBuffer().readObject(ServiceInfo.class);
			cloud.getServiceManager().handleServiceUpdate(ServicePublishPayload.UPDATE, response);
			cloud.publishUpdate(ServicePublishPayload.UPDATE, response, node.getChannel());
			return response;
		});
	}

	private int getNextFreePort(@Nonnull NodeServer node, @Nonnull ServiceTask task) {
		Collection<ServiceInfo> nodeServices = node.findServiceInfos();
		int port = task.getEnvironment().getServiceType().getStartPort();
		loop: while (true) {
			for (ServiceInfo service : nodeServices) {
				if (service.getPort() == port) {
					port++;
					continue loop;
				}
			}
			return port;
		}
	}

}
