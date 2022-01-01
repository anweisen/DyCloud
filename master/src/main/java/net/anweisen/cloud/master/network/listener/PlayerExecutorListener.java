package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerExecutorPacket.PlayerExecutorPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.master.CloudMaster;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerExecutorListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		PacketBuffer buffer = packet.getBuffer();

		PlayerExecutorPayload payload = buffer.readEnum(PlayerExecutorPayload.class);
		UUID playerUniqueId = buffer.readUniqueId();

		CloudPlayer player = CloudMaster.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(playerUniqueId);
		PlayerExecutor executor = player.getExecutor();

		switch (payload) {
			case SEND_MESSAGE:
				executor.sendMessage(buffer.readOptionalString(), buffer.readObjectArray(ChatText.class));
				return;
			case SEND_TRANSLATION:
				executor.sendTranslation(buffer.readString(), (Object[]) buffer.readStringArray());
				return;
			case SEND_ACTIONBAR:
				executor.sendActionbar(buffer.readString());
				return;
			case SEND_TITLE:
				executor.sendTitle(buffer.readString(), buffer.readString(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt());
				return;
			case CONNECT_SERVER:
				executor.connect(buffer.readString());
				return;
			case CONNECT_FALLBACK:
				executor.connectFallback();
				return;
			case DISCONNECT:
				executor.disconnect(buffer.readString());
				return;
			case CHAT:
				executor.chat(buffer.readString());
				return;
			case PERFORM_COMMAND:
				executor.performCommand(buffer.readString());
				return;
			default:
				throw new IllegalStateException("Unrecognized PlayerExecutorPayload." + payload);
		}

	}

}
