package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket.DatabaseActionType;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.database.action.DatabaseListTables;
import net.anweisen.utilities.database.exceptions.DatabaseException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseListTables implements DatabaseListTables {

	@Nonnull
	@Override
	public List<String> execute() throws DatabaseException {
		return executeAsync().getDefOrThrow(DatabaseException::new, "Operation timed out");
	}

	@Nonnull
	@Override
	public Task<List<String>> executeAsync() {
		return CloudDriver.getInstance().getSocketComponent().getFirstChannel()
			.sendQueryAsync(new DatabaseActionPacket(DatabaseActionType.LIST_TABLES, buffer -> {}))
			.map(packet -> new ArrayList<>(packet.getBuffer().readStringCollection()));
	}

}
