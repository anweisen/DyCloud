package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketChannels;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseActionPacket extends Packet {

	public RemoteDatabaseActionPacket(@Nonnull DatabaseActionPayload payload) {
		this(payload, null);
	}

	public RemoteDatabaseActionPacket(@Nonnull DatabaseActionPayload payload, @Nullable Consumer<? super PacketBuffer> modifier) {
		super(PacketChannels.DATABASE_CHANNEL, newBuffer().writeEnum(payload));
		apply(modifier);
	}

	public enum DatabaseActionPayload {

		QUERY(true),
		UPDATE(true),
		INSERT(true),
		INSERT_OR_UPDATE(true),
		DELETE(true),
		COUNT_ENTRIES(true),
		CREATE_TABLE(true),

		LIST_TABLES(false);

		private final boolean specific;

		DatabaseActionPayload(boolean specific) {
			this.specific = specific;
		}

		public boolean isSpecific() {
			return specific;
		}
	}

}
