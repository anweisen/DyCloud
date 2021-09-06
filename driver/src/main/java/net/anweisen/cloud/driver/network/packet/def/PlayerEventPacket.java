package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.event.player.*;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.service.specific.ServiceType;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerEventPacket extends Packet {

	protected PlayerEventPacket(@Nonnull PlayerEventType type) {
		super(PacketConstants.PLAYER_EVENT_CHANNEL, Buffer.create().writeEnumConstant(type));
	}

	public PlayerEventPacket(@Nonnull PlayerEventType type, @Nonnull UUID player, @Nonnull SerializableObject... objects) {
		this(type);
		buffer.writeUUID(player);
		for (SerializableObject object : objects) {
			buffer.writeObject(object);
		}
	}

	public PlayerEventPacket(@Nonnull PlayerEventType type, @Nonnull UUID player, @Nonnull String name, @Nonnull UUID service) {
		this(type, player);
		buffer.writeString(name);
		buffer.writeUUID(service);
	}

	public PlayerEventPacket(@Nonnull PlayerEventType type, @Nonnull UUID player, @Nonnull UUID service, @Nonnull SerializableObject... objects) {
		this(type, player);
		buffer.writeUUID(service);
		for (SerializableObject object : objects) {
			buffer.writeObject(object);
		}
	}

	public PlayerEventPacket(@Nonnull PlayerEventType type, @Nonnull UUID player, @Nonnull UUID serviceA, @Nonnull UUID serviceB) {
		this(type, player);
		buffer.writeUUID(serviceA);
		buffer.writeUUID(serviceB);
	}

	public PlayerEventPacket(@Nonnull PlayerEventType object, @Nonnull SerializableObject player, @Nonnull SerializableObject... objects) {
		this(object);
		buffer.writeObject(player);
		for (SerializableObject current : objects) {
			buffer.writeObject(current);
		}
	}

	/**
	 * Login Order:
	 * 1. {@link #PROXY_LOGIN_REQUEST}
	 * 2. {@link #PROXY_LOGIN_SUCCESS}
	 * 3. {@link #PROXY_SERVER_CONNECT_REQUEST}
	 * 4. {@link #SERVER_LOGIN_REQUEST}
	 * 5. {@link #PROXY_SERVER_SWITCH}
	 * 6. {@link #SERVER_LOGIN_SUCCESS}
	 */
	public enum PlayerEventType {

		/** @see PlayerProxyLoginRequestEvent */
		PROXY_LOGIN_REQUEST,

		/** @see PlayerProxyLoginSuccessEvent */
		PROXY_LOGIN_SUCCESS,

		/** @see PlayerProxyServerConnectRequestEvent */
		PROXY_SERVER_CONNECT_REQUEST,

		/** @see PlayerProxyServerSwitchEvent */
		PROXY_SERVER_SWITCH,

		/** @see PlayerProxyDisconnectEvent */
		PROXY_DISCONNECT,


		/** @see PlayerServerLoginRequestEvent */
		SERVER_LOGIN_REQUEST,

		/** @see PlayerServerLoginSuccessEvent */
		SERVER_LOGIN_SUCCESS,

		/** @see PlayerServerDisconnectEvent */
		SERVER_DISCONNECT;

		private final ServiceType type;

		PlayerEventType() {
			type = ServiceType.valueOf(name().substring(0, name().indexOf("_")));
		}

		@Nonnull
		public ServiceType getType() {
			return type;
		}

	}

}
