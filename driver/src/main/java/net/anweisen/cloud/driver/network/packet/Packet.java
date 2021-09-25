package net.anweisen.cloud.driver.network.packet;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Packet {

	public static final Packet EMPTY_RESPONSE = new Packet(PacketConstants.RESPONSE_CHANNEL, Document.empty(), newBuffer());

	protected final long creationMillis = System.currentTimeMillis();

	protected int channel;
	protected UUID uniqueId;
	protected Document header;
	protected PacketBuffer buffer;

	public Packet(int channel, @Nullable Document header) {
		this(channel, header, null);
	}

	public Packet(int channel, @Nullable PacketBuffer buffer) {
		this(channel, (Document) null, buffer);
	}

	public Packet(int channel, @Nullable Document header, @Nullable PacketBuffer buffer) {
		this(channel, UUID.randomUUID(), header, buffer);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header) {
		this(channel, uniqueId, header, null);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable PacketBuffer buffer) {
		this(channel, uniqueId, null, buffer);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header, @Nullable PacketBuffer buffer) {
		this.channel = channel;
		this.uniqueId = uniqueId;
		this.header = header;
		this.buffer = buffer;
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header, @Nullable PacketBuffer buffer) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), header, buffer);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), header);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable PacketBuffer buffer) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), Document.empty(), buffer);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet) {
		return new Packet(PacketConstants.RESPONSE_CHANNEL, packet.getUniqueId(), Document.empty());
	}

	@Nonnull
	protected static PacketBuffer newBuffer() {
		return CloudDriver.getInstance().getSocketComponent().newPacketBuffer();
	}

	@Nonnull
	protected static Document newDocument() {
		return Document.create();
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

	@Nonnull
	public Document getHeader() {
		return header == null ? Document.empty() : header;
	}
	@Nullable
	public Document getRealHeader() {
		return header;
	}

	@Nonnull
	public PacketBuffer getBuffer() {
		return buffer == null ? EMPTY_RESPONSE.buffer : buffer;
	}

	@Nullable
	public PacketBuffer getRealBuffer() {
		return buffer;
	}

	public long getCreationMillis() {
		return creationMillis;
	}

	@Override
	public String toString() {
		return "Packet[channel=" + PacketConstants.getChannelName(channel) + " uuid=" + uniqueId + " header=" + getHeader().toJson() + " buffer=" + getBuffer().length() + "]";
	}
}
