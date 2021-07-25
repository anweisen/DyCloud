package net.anweisen.cloud.driver.network.request;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.def.RequestPacket;
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
public final class RequestUtils {

	@Nonnull
	public static Task<Packet> sendRequest(@Nonnull SocketChannel channel, @Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier) {
		return channel.sendQueryAsync(new RequestPacket(requestType, modifier));
	}

	@Nonnull
	public static <T> Task<T> sendRequest(@Nonnull SocketChannel channel, @Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier, @Nonnull ExceptionallyFunction<? super Buffer, ? extends T> mapper) {
		return sendRequest(channel, requestType, modifier).map(packet -> mapper.apply(packet.getBuffer()));
	}

	@Nonnull
	public static Task<Void> sendVoidRequest(@Nonnull SocketChannel channel, @Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier) {
		return sendRequest(channel, requestType, modifier).mapVoid();
	}

	private RequestUtils() {}

}
