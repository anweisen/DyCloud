package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.event.player.*;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerEventPacket extends Packet {

	public static PlayerEventPacket forProxyLoginRequest(@Nonnull UUID playerUniqueId, @Nonnull String playerName, @Nonnull PlayerConnection playerConnection) {
		return new PlayerEventPacket(PlayerEventPayload.PROXY_LOGIN_REQUEST, buffer -> buffer.writeUUID(playerUniqueId).writeString(playerName).writeObject((SerializableObject) playerConnection));
	}

	public static PlayerEventPacket forProxyLoginRequest(@Nonnull CloudPlayer player) {
		return new PlayerEventPacket(PlayerEventPayload.PROXY_LOGIN_REQUEST, buffer -> buffer.writeObject((SerializableObject) player));
	}

	public static PlayerEventPacket forProxyLoginSuccess(@Nonnull UUID playerUniqueId, @Nonnull PlayerSettings playerSettings) {
		return new PlayerEventPacket(PlayerEventPayload.PROXY_LOGIN_SUCCESS, buffer -> buffer.writeUUID(playerUniqueId).writeObject((SerializableObject) playerSettings));
	}

	public static PlayerEventPacket forProxyServerConnectRequest(@Nonnull UUID playerUniqueId, @Nonnull UUID targetUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.PROXY_SERVER_CONNECT_REQUEST, buffer -> buffer.writeUUID(playerUniqueId).writeUUID(targetUniqueId));
	}

	public static PlayerEventPacket forProxyServerSwitch(@Nonnull UUID playerUniqueId, @Nonnull UUID fromUniqueId, @Nonnull UUID toUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.PROXY_SERVER_SWITCH, buffer -> buffer.writeUUID(playerUniqueId).writeUUID(fromUniqueId).writeUUID(toUniqueId));
	}

	public static PlayerEventPacket forProxyDisconnect(@Nonnull UUID playerUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.PROXY_DISCONNECT, buffer -> buffer.writeUUID(playerUniqueId));
	}

	public static PlayerEventPacket forServerLoginRequest(@Nonnull UUID playerUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.SERVER_LOGIN_REQUEST, buffer -> buffer.writeUUID(playerUniqueId));
	}

	public static PlayerEventPacket forServerLoginRequest(@Nonnull UUID playerUniqueId, @Nonnull UUID serviceUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.SERVER_LOGIN_REQUEST, buffer -> buffer.writeUUID(playerUniqueId).writeUUID(serviceUniqueId));
	}

	public static PlayerEventPacket forServerLoginSuccess(@Nonnull UUID playerUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.SERVER_LOGIN_SUCCESS, buffer -> buffer.writeUUID(playerUniqueId));
	}

	public static PlayerEventPacket forServerLoginSuccess(@Nonnull UUID playerUniqueId, @Nonnull UUID serviceUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.SERVER_LOGIN_SUCCESS, buffer -> buffer.writeUUID(playerUniqueId).writeUUID(serviceUniqueId));
	}

	public static PlayerEventPacket forServerDisconnect(@Nonnull UUID playerUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.SERVER_DISCONNECT, buffer -> buffer.writeUUID(playerUniqueId));
	}

	public static PlayerEventPacket forServerDisconnect(@Nonnull UUID playerUniqueId, @Nonnull UUID serviceUniqueId) {
		return new PlayerEventPacket(PlayerEventPayload.SERVER_DISCONNECT, buffer -> buffer.writeUUID(playerUniqueId).writeUUID(serviceUniqueId));
	}

	public static PlayerEventPacket forPlayerSettingsChange(@Nonnull UUID playerUniqueId, @Nonnull PlayerSettings settings) {
		return new PlayerEventPacket(PlayerEventPayload.PLAYER_SETTINGS_CHANGE, buffer -> buffer.writeUUID(playerUniqueId).writeObject((SerializableObject) settings));
	}

	protected PlayerEventPacket(@Nonnull PlayerEventPayload payload, @Nonnull Consumer<? super Buffer> modifier) {
		super(PacketConstants.PLAYER_EVENT_CHANNEL, Buffer.create().writeEnumConstant(payload));
		modifier.accept(buffer);
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
	public enum PlayerEventPayload {

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
		SERVER_DISCONNECT,


		/** @see PlayerSettingsChangeEvent */
		PLAYER_SETTINGS_CHANGE;

		private final ServiceType type;

		PlayerEventPayload() {
			if (name().startsWith("PLAYER")) {
				type = null;
			} else {
				type = ServiceType.valueOf(name().substring(0, name().indexOf("_")));
			}
		}

		@Nullable
		public ServiceType getType() {
			return type;
		}

	}

}
