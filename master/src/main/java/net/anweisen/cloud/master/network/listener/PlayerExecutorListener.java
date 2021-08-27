package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.PlayerExecutorPacket.PlayerExecutorType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerExecutorListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		Buffer buffer = packet.getBuffer();

		PlayerExecutorType type = buffer.readEnumConstant(PlayerExecutorType.class);
		UUID playerUniqueId = buffer.readUUID();

		// TODO check and maybe respond
		CloudPlayer player = CloudMaster.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(playerUniqueId);
		CloudService proxyService = CloudMaster.getInstance().getServiceManager().getServiceByName(player.getProxyConnectionData().getName());
		proxyService.getChannel().sendPacket(new Packet(PacketConstants.PLAYER_EXECUTOR_CHANNEL, buffer.resetReaderIndex().retain()));

	}

}
