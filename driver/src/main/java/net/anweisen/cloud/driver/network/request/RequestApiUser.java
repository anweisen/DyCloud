package net.anweisen.cloud.driver.network.request;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.packets.RequestPacket;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.function.ExceptionallyFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface RequestApiUser {

	@Nonnull
	SocketChannel getChannel();

	@Nonnull
	default Task<Packet> executeRequest(@Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier) {
		return getChannel().sendQueryAsync(new RequestPacket(requestType, modifier));
	}

	@Nonnull
	default <T> Task<T> executeRequest(@Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier, @Nullable ExceptionallyFunction<? super Packet, ? extends T> mapper) {
		return getChannel().sendQueryAsync(new RequestPacket(requestType, modifier)).map(mapper);
	}

	@Nonnull
	default Task<Void> executeVoidRequest(@Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier) {
		return executeRequest(requestType, modifier).mapVoid();
	}

}
