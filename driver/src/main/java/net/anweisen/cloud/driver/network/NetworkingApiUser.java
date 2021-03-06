package net.anweisen.cloud.driver.network;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.chunk.ChunkedQueryResponse;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.utility.common.concurrent.task.Task;
import net.anweisen.utility.document.Document;

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
		return getTargetChannel().sendPacketQuery(packet);
	}

	@Nonnull
	default Task<Packet> sendPacketQueryAsync(@Nonnull Packet packet) {
		return getTargetChannel().sendPacketQueryAsync(packet);
	}

	@Nonnull
	default <R> Task<R> sendPacketQueryAsync(@Nonnull Packet packet, @Nonnull Function<? super PacketBuffer, ? extends R> mapper) {
		return getTargetChannel().sendPacketQueryAsync(packet).map(response -> mapper.apply(response.getBuffer()));
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
