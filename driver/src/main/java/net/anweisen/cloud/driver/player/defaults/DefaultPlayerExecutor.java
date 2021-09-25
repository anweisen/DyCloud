package net.anweisen.cloud.driver.player.defaults;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.def.PlayerExecutorPacket;
import net.anweisen.cloud.driver.network.packet.def.PlayerExecutorPacket.PlayerExecutorPayload;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.chat.ChatText;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultPlayerExecutor implements PlayerExecutor {

	public static final UUID GLOBAL_UUID = new UUID(0, 0);

	protected final UUID playerUniqueId;

	public DefaultPlayerExecutor(@Nonnull UUID playerUniqueId) {
		this.playerUniqueId = playerUniqueId;
	}

	@Nonnull
	@Override
	public UUID getPlayerUniqueId() {
		return playerUniqueId;
	}

	@Override
	public boolean isGlobal() {
		return playerUniqueId.equals(GLOBAL_UUID);
	}

	@Override
	public void sendMessage(@Nullable String permission, @Nonnull ChatText... message) {
		sendPacket(PlayerExecutorPayload.SEND_MESSAGE, buffer -> buffer.writeOptionalString(permission).writeObjectArray(message));
	}

	@Override
	public void sendActionbar(@Nonnull String message) {
		sendPacket(PlayerExecutorPayload.SEND_ACTIONBAR, buffer -> buffer.writeString(message));
	}

	@Override
	public void sendTitle(@Nonnull String title, @Nonnull String subtitle, int fadeIn, int stay, int fadeOut) {
		sendPacket(PlayerExecutorPayload.SEND_TITLE, buffer -> buffer.writeString(title).writeString(subtitle).writeVarInt(fadeIn).writeVarInt(stay).writeVarInt(fadeOut));
	}

	@Override
	public void connect(@Nonnull String serverName) {
		sendPacket(PlayerExecutorPayload.CONNECT_SERVER, buffer -> buffer.writeString(serverName));
	}

	@Override
	public void connectFallback() {
		sendPacket(PlayerExecutorPayload.CONNECT_FALLBACK, buffer -> {});
	}

	@Override
	public void disconnect(@Nullable String kickReason) {
		sendPacket(PlayerExecutorPayload.DISCONNECT, buffer -> buffer.writeString(kickReason == null ? "§cNo kick reason given" : kickReason));
	}

	private void sendPacket(@Nonnull PlayerExecutorPayload payload, @Nonnull Consumer<? super Buffer> modifier) {
		sendPacket(new PlayerExecutorPacket(payload, playerUniqueId, modifier));
	}

	protected abstract void sendPacket(@Nonnull Packet packet);

}
