package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultSocketComponent implements SocketComponent, LoggingApiUser {

	protected final Executor packetDispatcher = NettyUtils.newPacketDispatcher();
	protected final PacketListenerRegistry listenerRegistry = new PacketListenerRegistry();

	protected final Collection<SocketChannel> channels = new ConcurrentLinkedQueue<>();

	protected final Supplier<SocketChannelHandler> handlerSupplier;

	public DefaultSocketComponent(@Nonnull Supplier<SocketChannelHandler> handlerSupplier) {
		this.handlerSupplier = handlerSupplier;
	}

	@Override
	public void closeChannels() {
		for (SocketChannel channel : getChannels()) {
			try {
				info("Closing Channel[client={} server={}]..", channel.getClientAddress(), channel.getServerAddress());
				channel.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		getChannels().clear();
	}

	@Nonnull
	@Override
	public PacketListenerRegistry getListenerRegistry() {
		return listenerRegistry;
	}

	@Nonnull
	@Override
	public Executor getPacketDispatcher() {
		return packetDispatcher;
	}

	@Nonnull
	public Supplier<SocketChannelHandler> getHandlerSupplier() {
		return handlerSupplier;
	}

	@Override
	public void sendPacket(@Nonnull Packet packet, @Nonnull SocketChannel... skipChannels) {
		List<SocketChannel> channelList = Arrays.asList(skipChannels);
		for (SocketChannel channel : channels) {
			if (channelList.contains(channel))
				continue;

			channel.sendPacket(packet);
		}
	}

	@Override
	public void sendPacketSync(@Nonnull Packet packet, @Nonnull SocketChannel... skipChannels) {
		List<SocketChannel> channelList = Arrays.asList(skipChannels);
		for (SocketChannel channel : channels) {
			if (channelList.contains(channel))
				continue;

			channel.sendPacketSync(packet);
		}
	}

	@Override
	public void sendPacket(@Nonnull Packet packet) {
		for (SocketChannel channel : channels) {
			channel.sendPacket(packet);
		}
	}

	@Override
	public void sendPackets(@Nonnull Packet... packets) {
		for (SocketChannel channel : channels) {
			channel.sendPackets(packets);
		}
	}

	@Override
	public void sendPacketSync(@Nonnull Packet packet) {
		for (SocketChannel channel : channels) {
			channel.sendPacketSync(packet);
		}
	}

	@Override
	public void sendPacketsSync(@Nonnull Packet... packets) {
		for (SocketChannel channel : channels) {
			channel.sendPacketsSync(packets);
		}
	}
}
