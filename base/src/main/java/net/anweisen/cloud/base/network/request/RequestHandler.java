package net.anweisen.cloud.base.network.request;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface RequestHandler {

	@Nullable
	Buffer handle(@Nonnull SocketChannel channel, @Nonnull Packet packet, @Nonnull Buffer buffer) throws Exception;

}
