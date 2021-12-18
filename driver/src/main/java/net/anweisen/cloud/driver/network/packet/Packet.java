package net.anweisen.cloud.driver.network.packet;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class Packet {

	public static final Packet EMPTY_RESPONSE = new Packet(PacketChannels.RESPONSE_CHANNEL, Documents.emptyDocument(), newBuffer());

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
		return new Packet(PacketChannels.RESPONSE_CHANNEL, packet.getUniqueId(), header, buffer);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header) {
		return new Packet(PacketChannels.RESPONSE_CHANNEL, packet.getUniqueId(), header);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable PacketBuffer buffer) {
		return new Packet(PacketChannels.RESPONSE_CHANNEL, packet.getUniqueId(), Documents.emptyDocument(), buffer);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet) {
		return new Packet(PacketChannels.RESPONSE_CHANNEL, packet.getUniqueId(), Documents.emptyDocument());
	}

	@Nonnull
	public static PacketBuffer newBuffer() {
		return CloudDriver.getInstance().getSocketComponent().newPacketBuffer();
	}

	@Nonnull
	public static Document newDocument() {
		return Documents.newJsonDocument();
	}

	protected void apply(@Nullable Consumer<? super PacketBuffer> modifier) {
		if (modifier != null)
			modifier.accept(buffer != null ? buffer : (buffer = newBuffer()));
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
		return header == null ? Documents.emptyDocument() : header;
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
		return "Packet[channel=" + PacketChannels.getChannelName(channel) + " uuid=" + uniqueId + " header=" + getHeader().toJson() + " buffer=" + getBuffer().length() + "]";
	}
}
