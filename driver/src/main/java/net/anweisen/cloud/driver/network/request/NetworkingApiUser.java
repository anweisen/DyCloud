package net.anweisen.cloud.driver.network.request;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;
import net.anweisen.cloud.driver.network.packet.def.RequestPacket;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.function.ExceptionallyFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface NetworkingApiUser {

	@Nonnull
	SocketChannel getChannel();

	@Nonnull
	default Task<Packet> sendRequest(@Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier) {
		return RequestUtils.sendRequest(getChannel(), requestType, modifier);
	}

	@Nonnull
	default <T> Task<T> sendRequest(@Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier, @Nonnull ExceptionallyFunction<? super Buffer, ? extends T> mapper) {
		return RequestUtils.sendRequest(getChannel(), requestType, modifier, mapper);
	}

	@Nonnull
	default Task<Void> sendVoidRequest(@Nonnull RequestType requestType, @Nullable Consumer<? super Buffer> modifier) {
		return RequestUtils.sendVoidRequest(getChannel(), requestType, modifier);
	}

	default void sendPacket(@Nonnull Packet packet) {
		getChannel().sendPacket(packet);
	}

	default void sendPackets(@Nonnull Packet... packets) {
		getChannel().sendPackets(packets);
	}

	@Nonnull
	default Task<Packet> sendQueryAsync(@Nonnull Packet packet) {
		return getChannel().sendQueryAsync(packet);
	}

	@Nonnull
	default Task<ChunkedQueryResponse> sendChunkedRequest(@Nonnull RequestType requestType, @Nonnull Consumer<? super Buffer> modifier) {
		return sendChunkedPacketQuery(new RequestPacket(requestType, modifier));
	}

	@Nonnull
	default Task<ChunkedQueryResponse> sendChunkedPacketQuery(@Nonnull Packet packet) {
		return getChannel().sendChunkedPacketQuery(packet);
	}

	default boolean sendChunkedPacketsResponse(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream) throws IOException {
		return getChannel().sendChunkedPacketsResponse(uniqueId, header, inputStream);
	}

	default boolean sendChunkedPackets(@Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException {
		return getChannel().sendChunkedPackets(header, inputStream, channel);
	}

	default boolean sendChunkedPackets(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException {
		return getChannel().sendChunkedPackets(uniqueId, header, inputStream, channel);
	}

	@Nullable
	default Packet sendQuery(@Nonnull Packet packet) {
		return getChannel().sendQuery(packet);
	}

}
