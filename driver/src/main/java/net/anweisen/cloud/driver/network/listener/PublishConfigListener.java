package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ConfigInitPacket;
import net.anweisen.cloud.driver.network.packet.def.ConfigInitPacket.PropertySection;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.permission.impl.DefaultPermissionGroup;
import net.anweisen.cloud.driver.service.config.RemoteTemplateStorage;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.utilities.common.collection.ArrayWalker;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ConfigInitPacket#create()
 */
public class PublishConfigListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		info("Received published configs from master..");
		readNext(channel, packet.getBuffer());
	}

	private void readNext(@Nonnull SocketChannel channel, @Nonnull Buffer buffer) {
		if (buffer.readableBytes() < 1) return;

		PropertySection section = buffer.readEnumConstant(PropertySection.class);
		read(section, channel, buffer);
		readNext(channel, buffer);
	}

	private void read(@Nonnull PropertySection section, @Nonnull SocketChannel channel, @Nonnull Buffer buffer) {
		CloudDriver cloud = CloudDriver.getInstance();
		switch (section) {
			case LOG_LEVEL: {
				cloud.getLogger().setMinLevel(buffer.readEnumConstant(LogLevel.class));
				break;
			}
			case TASKS: {
				cloud.getServiceConfigManager().setServiceTasks(buffer.readObjectCollection(ServiceTask.class));
				break;
			}
			case SERVICES: {
				cloud.getServiceManager().setServiceInfos(buffer.readObjectCollection(ServiceInfo.class));
				break;
			}
			case START_PORTS: {
				ArrayWalker.walk(ServiceType.values()).forEach(type -> type.setStartPort(buffer.readVarInt()));
				break;
			}
			case TEMPLATE_STORAGES: {
				for (String name : buffer.readStringCollection()) {
					cloud.getServiceConfigManager().registerTemplateStorage(new RemoteTemplateStorage(name, channel));
				}
				break;
			}
			case ONLINE_PLAYERS: {
				cloud.getPlayerManager().setOnlinePlayerCache(buffer.readObjectCollection(DefaultCloudPlayer.class));
				break;
			}
			case PERMISSION_GROUPS: {
				cloud.getPermissionManager().setGroupsCache(buffer.readObjectCollection(DefaultPermissionGroup.class));
				break;
			}
		}
	}

}
