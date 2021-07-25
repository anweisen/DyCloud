package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListenerRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class DefaultSocketComponent implements SocketComponent {

	protected final Executor packetDispatcher = NettyUtils.newPacketDispatcher();
	protected final PacketListenerRegistry listenerRegistry = new PacketListenerRegistry();

	protected final Collection<SocketChannel> channels = new ConcurrentLinkedQueue<>();

	protected final Supplier<SocketChannelHandler> handler;

	public DefaultSocketComponent(@Nonnull Supplier<SocketChannelHandler> handler) {
		this.handler = handler;
	}

	@Override
	public void closeChannels() {
		for (SocketChannel channel : getChannels()) {
			try {
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
	public Supplier<SocketChannelHandler> getHandler() {
		return handler;
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
