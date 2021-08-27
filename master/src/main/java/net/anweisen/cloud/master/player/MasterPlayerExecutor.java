package net.anweisen.cloud.master.player;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.defaults.DefaultPlayerExecutor;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterPlayerExecutor extends DefaultPlayerExecutor {

	public MasterPlayerExecutor(@Nonnull UUID playerUniqueId) {
		super(playerUniqueId);
	}

	@Override
	protected void sendPacket(@Nonnull Packet packet) {
		CloudPlayer player = CloudMaster.getInstance().getPlayerManager().getOnlinePlayerByUniqueId(playerUniqueId);
		Preconditions.checkNotNull(player, "CloudPlayer is null");
		CloudService proxyService = CloudMaster.getInstance().getServiceManager().getServiceByUniqueId(player.getCurrentProxy().getUniqueId());
		Preconditions.checkNotNull(proxyService, "CloudService is null");
		Preconditions.checkNotNull(proxyService.getChannel(), "SocketChannel is null");
		proxyService.getChannel().sendPacket(packet);
	}
}
