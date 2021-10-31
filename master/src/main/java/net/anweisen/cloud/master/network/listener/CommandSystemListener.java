package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.base.command.sender.PlayerCommandSender;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.CommandSystemPacket.CommandSystemPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.master.CloudMaster;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CommandSystemListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		PacketBuffer buffer = packet.getBuffer();

		CommandSystemPayload payload = buffer.readEnum(CommandSystemPayload.class);
		UUID uniqueId = buffer.readUniqueId();
		String command = buffer.readString();

		debug("CommandSystemPayload.{} -> {} '{}'", payload, uniqueId, command);

		CloudPlayer player = CloudMaster.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(uniqueId);
		PlayerCommandSender sender = PlayerCommandSender.of(player);

		switch (payload) {
			case EXECUTE: {
				CloudMaster.getInstance().getCommandManager().executeCommand(sender, command);
				break;
			}
			case COMPLETE: {
				Collection<String> result = CloudMaster.getInstance().getCommandManager().completeCommand(sender, command);
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeStringCollection(result)));
				break;
			}
		}

	}
}
