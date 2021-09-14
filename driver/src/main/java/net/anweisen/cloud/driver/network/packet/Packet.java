package net.anweisen.cloud.driver.network.packet;

import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Packet {

	/** A one length size byte[] for empty packet bodies */
	public static final byte[] EMPTY_PACKET_BYTE_ARRAY = new byte[] { 0 };
	public static final Packet EMPTY_RESPONSE = new Packet(PacketConstants.RESPONSE_CHANNEL, Document.empty(), EMPTY_PACKET_BYTE_ARRAY);

	protected final long creationMillis = System.currentTimeMillis();

	protected int channel;
	protected UUID uniqueId;
	protected Document header;
	protected Buffer buffer;

	public Packet(int channel, @Nullable Document header) {
		this(channel, header, (Buffer) null);
	}

	public Packet(int channel, @Nullable Document header, @Nullable byte[] buffer) {
		this(channel, UUID.randomUUID(), header, buffer);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header, @Nullable byte[] buffer) {
		this.channel = channel;
		this.uniqueId = uniqueId;
		this.header = header;
		this.buffer = buffer == null ? null : Buffer.wrap(buffer);
	}

	public Packet(int channel, @Nullable Buffer buffer) {
		this(channel, Document.empty(), buffer);
	}

	public Packet(int channel, @Nullable Document header, @Nullable Buffer buffer) {
		this(channel, UUID.randomUUID(), header, buffer);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header) {
		this(channel, uniqueId, header, (Buffer) null);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header, @Nullable Buffer buffer) {
		this.channel = channel;
		this.uniqueId = uniqueId;
		this.header = header;
		this.buffer = buffer;
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header, @Nullable Buffer buffer) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), header, buffer);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), header);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Buffer buffer) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), Document.empty(), buffer);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), Document.empty());
	}

	public int getChannel() {
		return channel;
	}

	@Nonnull
	public UUID getUniqueId() {
		if (uniqueId == null) {
			uniqueId = UUID.randomUUID();
		}
		return uniqueId;
	}

	public Document getHeader() {
		return header;
	}

	public Buffer getBuffer() {
		return buffer;
	}

	public byte[] getBufferAsArray() {
		return buffer == null ? EMPTY_PACKET_BYTE_ARRAY : buffer.toArray();
	}

	public long getCreationMillis() {
		return creationMillis;
	}

	@Override
	public String toString() {
		return "Packet[channel=" + PacketConstants.getChannelName(channel) + " uuid=" + uniqueId + " header=" + header.toJson() + " buffer=" + (buffer != null ? buffer.readableBytes() + buffer.readerIndex() : 0) + "]";
	}
}