package net.anweisen.cloud.driver.network.packet;

import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Packet {

	/**
	 * An one length size byte[] for empty packet bodies
	 */
	public static final byte[] EMPTY_PACKET_BYTE_ARRAY = new byte[]{0};
	public static final Packet EMPTY = new Packet(-1, Document.empty(), EMPTY_PACKET_BYTE_ARRAY);

	protected final long creationMillis = System.currentTimeMillis();

	protected int channel;
	protected UUID uniqueId;
	protected Document header;
	protected Buffer body;

	public Packet(int channel, @Nullable Document header) {
		this(channel, header, (Buffer) null);
	}

	public Packet(int channel, @Nullable Document header, @Nullable byte[] body) {
		this(channel, UUID.randomUUID(), header, body);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header, @Nullable byte[] body) {
		this.channel = channel;
		this.uniqueId = uniqueId;
		this.header = header;
		this.body = body == null ? null : Buffer.wrap(body);
	}

	public Packet(int channel, @Nullable Buffer body) {
		this(channel, Document.empty(), body);
	}

	public Packet(int channel, @Nullable Document header, @Nullable Buffer body) {
		this(channel, UUID.randomUUID(), header, body);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header) {
		this(channel, uniqueId, header, (Buffer) null);
	}

	public Packet(int channel, @Nonnull UUID uniqueId, @Nullable Document header, @Nullable Buffer body) {
		this.channel = channel;
		this.uniqueId = uniqueId;
		this.header = header;
		this.body = body;
	}

	public Packet() {
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header, @Nullable Buffer body) {
		return new Packet(-1, packet.getUniqueId(), header, body);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Document header) {
		return new Packet(-1, packet.getUniqueId(), header);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet, @Nullable Buffer body) {
		return new Packet(-1, packet.getUniqueId(), Document.empty(), body);
	}

	@Nonnull
	@CheckReturnValue
	public static Packet createResponseFor(@Nonnull Packet packet) {
		return new Packet(-1, packet.getUniqueId(), Document.empty());
	}

	public int getChannel() {
		return this.channel;
	}

	@Nonnull
	public UUID getUniqueId() {
		if (this.uniqueId == null) {
			this.uniqueId = UUID.randomUUID();
		}
		return this.uniqueId;
	}

	public Document getHeader() {
		return this.header;
	}

	public Buffer getBuffer() {
		return this.body;
	}

	public byte[] getBodyAsArray() {
		return this.body == null ? EMPTY_PACKET_BYTE_ARRAY : this.body.toArray();
	}

	public long getCreationMillis() {
		return this.creationMillis;
	}

	public boolean isShowDebug() {
		return true;
	}

	@Override
	public String toString() {
		return "Packet[channel=" + channel + " uuid=" + uniqueId + " header=" + header.toJson() + " buffer=" + (body != null ? body.readableBytes() : 0) + "]";
	}
}