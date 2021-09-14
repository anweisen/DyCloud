package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseActionPacket extends Packet {

	public RemoteDatabaseActionPacket(@Nonnull DatabaseActionType type) {
		this(type, null);
	}

	public RemoteDatabaseActionPacket(@Nonnull DatabaseActionType type, @Nullable Consumer<? super Buffer> modifier) {
		super(PacketConstants.DATABASE_CHANNEL, Buffer.create().writeEnumConstant(type));
		if (modifier != null)
			modifier.accept(buffer);
	}

	public enum DatabaseActionType {

		QUERY(true),
		UPDATE(true),
		INSERT(true),
		INSERT_OR_UPDATE(true),
		DELETE(true),
		COUNT_ENTRIES(true),
		CREATE_TABLE(true),

		LIST_TABLES(false);

		private final boolean specific;

		DatabaseActionType(boolean specific) {
			this.specific = specific;
		}

		public boolean isSpecific() {
			return specific;
		}
	}

}
