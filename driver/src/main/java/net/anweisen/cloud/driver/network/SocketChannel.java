package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.network.packet.PacketSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SocketChannel extends PacketSender {

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
