package net.anweisen.cloud.driver.network.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.internal.InternalQueryResponseManager;
import net.anweisen.cloud.driver.network.object.HostAndPort;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedPacketBuilder;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;
import net.anweisen.utility.common.concurrent.task.CompletableTask;
import net.anweisen.utility.common.concurrent.task.Task;
import net.anweisen.utility.document.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettySocketChannel implements SocketChannel {

	protected final HostAndPort serverAddress, clientAddress;
	protected final boolean client;
	protected final Channel channel;

	protected SocketChannelHandler handler;

	public NettySocketChannel(@Nonnull Channel channel, @Nullable SocketChannelHandler handler, @Nonnull HostAndPort serverAddress, @Nonnull HostAndPort clientAddress, boolean client) {
		Preconditions.checkNotNull(channel, "Channel cannot be null");
		Preconditions.checkNotNull(serverAddress, "ServerAddress cannot be null");
		Preconditions.checkNotNull(clientAddress, "ClientAddress cannot be null");

		this.channel = channel;
		this.handler = handler;
		this.serverAddress = serverAddress;
		this.clientAddress = clientAddress;
		this.client = client;
	}

	@Nonnull
	@Override
	public Task<Packet> registerQueryResponseHandler(@Nonnull UUID uniqueId) {
		Preconditions.checkNotNull(uniqueId, "UUID cannot be null");

		CompletableTask<Packet> task = Task.completable();
		InternalQueryResponseManager.registerQueryHandler(uniqueId, task::complete);
		return task;
	}

	@Nonnull
	@Override
	public Task<Packet> sendPacketQueryAsync(@Nonnull Packet packet) {
		Preconditions.checkNotNull(packet, "Packet cannot be null");

		Task<Packet> task = registerQueryResponseHandler(packet.getUniqueId());
		sendPacket(packet);
		return task;
	}

	@Nullable
	@Override
	public Packet sendPacketQuery(@Nonnull Packet packet) {
		return sendPacketQueryAsync(packet).getOrDefault(5, TimeUnit.SECONDS, null);
	}

	@Nonnull
	@Override
	public Task<ChunkedQueryResponse> sendChunkedPacketQuery(@Nonnull Packet packet) {
		Preconditions.checkNotNull(packet, "Packet cannot be null");

		CompletableTask<ChunkedQueryResponse> task = Task.completable();
		InternalQueryResponseManager.registerChunkedQueryHandler(packet.getUniqueId(), task::complete);
		sendPacket(packet);
		return task;
	}

	@Override
	public boolean sendChunkedPackets(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException {
		return ChunkedPacketBuilder.newBuilder(channel, inputStream)
			.uniqueId(uniqueId)
			.header(header)
			.target(this)
			.complete()
			.isSuccess();
	}

	@Override
	public boolean sendChunkedPacketsResponse(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream) throws IOException {
		return sendChunkedPackets(uniqueId, header, inputStream, PacketChannels.RESPONSE_CHANNEL);
	}

	@Override
	public boolean sendChunkedPackets(@Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException {
		return sendChunkedPackets(UUID.randomUUID(), header, inputStream, channel);
	}

	@Override
	public void sendPacket(@Nonnull Packet packet) {
		Preconditions.checkNotNull(packet, "Packet cannot be null");

		if (channel.eventLoop().inEventLoop()) {
			writePacket(packet);
		} else {
			channel.eventLoop().execute(() -> writePacket(packet));
		}
	}

	@Override
	public void sendPacketSync(@Nonnull Packet packet) {
		Preconditions.checkNotNull(packet, "Packet cannot be null");

		ChannelFuture future = writePacket(packet);
		if (future != null) {
			try {
				future.sync();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private ChannelFuture writePacket(@Nonnull Packet packet) {
		try {
			return channel.writeAndFlush(packet);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void close() throws Exception {
		channel.close();
	}

	@Override
	public boolean isActive() {
		return channel.isActive();
	}

	@Override
	public boolean isWritable() {
		return channel.isWritable();
	}

	@Nullable
	@Override
	public SocketChannelHandler getHandler() {
		return handler;
	}

	@Override
	public void setHandler(@Nullable SocketChannelHandler handler) {
		this.handler = handler;
	}

	@Nonnull
	@Override
	public HostAndPort getServerAddress() {
		return serverAddress;
	}

	@Nonnull
	@Override
	public HostAndPort getClientAddress() {
		return clientAddress;
	}

	@Override
	public boolean isClientSide() {
		return client;
	}

	@Override
	public String toString() {
		return "SocketChannel[client=" + clientAddress + " server=" + serverAddress + "]";
	}
}
