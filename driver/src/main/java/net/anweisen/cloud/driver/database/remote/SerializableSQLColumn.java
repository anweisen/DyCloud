package net.anweisen.cloud.driver.database.remote;

import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.utilities.database.SQLColumn;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SerializableSQLColumn implements SerializableObject {

	private SQLColumn column;

	public SerializableSQLColumn(@Nonnull SQLColumn column) {
		this.column = column;
	}

	public SerializableSQLColumn() {
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeString(column.getName());
		buffer.writeString(column.getType());
		buffer.writeOptionalString(column.getParam());
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		column = new SQLColumn(
			buffer.readString(),
			buffer.readString(),
			buffer.readOptionalString()
		);
	}

	public SQLColumn getColumn() {
		return column;
	}
}
