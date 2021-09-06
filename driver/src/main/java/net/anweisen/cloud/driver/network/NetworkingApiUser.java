package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface NetworkingApiUser {

	@Nonnull
	default SocketChannel getTargetChannel() {
		return CloudDriver.getInstance().getSocketComponent().getFirstChannel();
	}

	default void sendPacket(@Nonnull Packet packet) {
		getTargetChannel().sendPacket(packet);
	}

	default void sendPackets(@Nonnull Packet... packets) {
		getTargetChannel().sendPackets(packets);
	}

	default void sendPacketSync(@Nonnull Packet packet) {
		getTargetChannel().sendPacketSync(packet);
	}

	default void sendPacketsSync(@Nonnull Packet... packets) {
		getTargetChannel().sendPacketsSync(packets);
	}

	@Nullable
	default Packet sendPacketQuery(@Nonnull Packet packet) {
		return getTargetChannel().sendQuery(packet);
	}

	@Nonnull
	default Task<Packet> sendPacketQueryAsync(@Nonnull Packet packet) {
		return getTargetChannel().sendQueryAsync(packet);
	}

	@Nonnull
	default <R> Task<R> sendPacketQueryAsync(@Nonnull Packet packet, @Nonnull Function<? super Buffer, ? extends R> mapper) {
		return getTargetChannel().sendQueryAsync(packet).map(response -> mapper.apply(response.getBuffer()));
	}

	@Nonnull
	default Task<ChunkedQueryResponse> sendChunkedPacketQuery(@Nonnull Packet packet) {
		return getTargetChannel().sendChunkedPacketQuery(packet);
	}

	default boolean sendChunkedPacketsResponse(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream) throws IOException {
		return getTargetChannel().sendChunkedPacketsResponse(uniqueId, header, inputStream);
	}

	default boolean sendChunkedPackets(@Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException {
		return getTargetChannel().sendChunkedPackets(header, inputStream, channel);
	}

	default boolean sendChunkedPackets(@Nonnull UUID uniqueId, @Nonnull Document header, @Nonnull InputStream inputStream, int channel) throws IOException {
		return getTargetChannel().sendChunkedPackets(uniqueId, header, inputStream, channel);
	}

}
