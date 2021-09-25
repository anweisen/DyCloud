package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationResponsePacket;
import net.anweisen.cloud.driver.network.packet.def.AuthenticationResponsePacket.PropertySection;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.player.defaults.DefaultCloudPlayer;
import net.anweisen.cloud.driver.player.permission.impl.DefaultPermissionGroup;
import net.anweisen.cloud.driver.service.config.RemoteTemplateStorage;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.utilities.common.collection.ArrayWalker;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see net.anweisen.cloud.driver.network.packet.def.AuthenticationPacket
 * @see net.anweisen.cloud.driver.network.packet.def.AuthenticationResponsePacket
 */
public class AuthenticationResponseListener implements PacketListener, LoggingApiUser {

	private final Lock lock;
	private final Condition condition;

	private SocketChannel channel;
	private boolean result;
	private String message;
	private PacketBuffer buffer;

	public AuthenticationResponseListener(@Nonnull Lock lock, @Nonnull Condition condition) {
		this.lock = lock;
		this.condition = condition;
	}

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		this.channel = channel;

		Document header = packet.getHeader();
		if (!header.contains("access")) return;

		trace("Received authentication response from master");

		result = header.getBoolean("access");
		message = header.getString("message", "");
		buffer = packet.getBuffer();

		try {
			lock.lock();
			condition.signalAll();
		} finally {
			lock.unlock();
		}

	}

	public boolean getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * @see AuthenticationResponsePacket#appendConfigProperties()
	 */
	public void readConfigProperties() {
		readConfigProperties0(channel, buffer);
	}

	private void readConfigProperties0(@Nonnull SocketChannel channel, @Nonnull PacketBuffer buffer) {
		if (buffer.remaining() < 1) return;

		PropertySection section = buffer.readEnum(PropertySection.class);
		readConfigProperty(section, channel, buffer);
		readConfigProperties0(channel, buffer);
	}

	private void readConfigProperty(@Nonnull PropertySection section, @Nonnull SocketChannel channel, @Nonnull PacketBuffer buffer) {
		CloudDriver cloud = CloudDriver.getInstance();
		cloud.getLogger().debug("=> PropertySection.{}", section);
		switch (section) {
			case LOG_LEVEL: {
				cloud.getLogger().setMinLevel(buffer.readEnum(LogLevel.class));
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
					cloud.getServiceConfigManager().registerTemplateStorage(new RemoteTemplateStorage(name));
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
			case GLOBAL_CONFIG: {
				cloud.getGlobalConfig().setRawData(buffer.readDocument());
				break;
			}
		}
	}
}
