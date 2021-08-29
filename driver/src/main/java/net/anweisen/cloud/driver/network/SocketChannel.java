package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketSender;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketChannel extends PacketSender {

	@Nonnull
	Task<Packet> registerQueryResponseHandler(@Nonnull UUID uniqueId);

	@Nonnull
	Task<Packet> sendQueryAsync(@Nonnull Packet packet);

	@Nullable
	@CheckReturnValue
	Packet sendQuery(@Nonnull Packet packet);

	@Nonnull
	Task<ChunkedQueryResponse> sendChunkedPacketQuery(@Nonnull Packet packet);

	boolean sendChunkedPacketsResponse(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream) throws IOException;

	boolean sendChunkedPackets(@Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException;

	boolean sendChunkedPackets(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException;

	void close() throws Exception;

	boolean isActive();

	boolean isWritable();

	@Nullable
	SocketChannelHandler getHandler();

	void setHandler(@Nullable SocketChannelHandler handler);

	@Nonnull
	HostAndPort getServerAddress();

	@Nonnull
	HostAndPort getClientAddress();

	boolean isClientSide();

}
