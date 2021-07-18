package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.handler.SocketChannelHandler;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketSender;
import net.anweisen.utilities.common.concurrent.task.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	Packet sendQuery(@Nonnull Packet packet);

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
