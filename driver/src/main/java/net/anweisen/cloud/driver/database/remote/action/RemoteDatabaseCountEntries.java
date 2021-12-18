package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket.DatabaseActionPayload;
import net.anweisen.utility.common.concurrent.task.Task;
import net.anweisen.utility.database.action.DatabaseCountEntries;
import net.anweisen.utility.database.exception.DatabaseException;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseCountEntries implements DatabaseCountEntries, RemoteDatabaseCallbackAction<Long> {

	private final String table;

	public RemoteDatabaseCountEntries(@Nonnull String table) {
		this.table = table;
	}

	@Nonnull
	@Override
	public Task<Long> executeAsync() {
		return CloudDriver.getInstance().getSocketComponent().getFirstChannel()
			.sendPacketQueryAsync(new RemoteDatabaseActionPacket(DatabaseActionPayload.COUNT_ENTRIES, buffer -> buffer.writeString(table)))
			.map(packet -> packet.getBuffer().readLong());
	}

	@Nonnull
	@Override
	public Long execute() throws DatabaseException {
		return RemoteDatabaseCallbackAction.super.execute();
	}

}
