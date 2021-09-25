package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket;
import net.anweisen.cloud.driver.network.packet.def.ServiceControlPacket.ServiceControlPayload;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceControlListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();
		Buffer buffer = packet.getBuffer();

		ServiceControlPayload payload = buffer.readEnumConstant(ServiceControlPayload.class);
		switch (payload) {
			case CREATE: {
				ServiceTask task = buffer.readObject(ServiceTask.class);
				debug("{} -> {}", task, task);
				cloud.getServiceFactory().createServiceAsync(task)
					.onComplete(service -> channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeOptionalObject(service))))
					.onFailure(ex       -> channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeOptionalObject(null))))
					.onCancelled(()     -> channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeOptionalObject(null))));
				return;
			}
		}

		UUID uuid = buffer.readUUID();
		CloudService service = cloud.getServiceManager().getServiceByUniqueId(uuid);
		debug("{} -> {}", payload, service.getInfo().getName());
		NodeServer node = cloud.getNodeManager().getNodeServer(service.getInfo().getNodeName());
		node.getChannel().sendPacketQueryAsync(new ServiceControlPacket(payload, uuid))
			.onComplete(response -> channel.sendPacket(Packet.createResponseFor(packet)));
	}

}
