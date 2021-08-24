package net.anweisen.cloud.driver.database.remote;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.database.remote.action.*;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket.DatabaseActionType;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.database.DatabaseConfig;
import net.anweisen.utilities.database.SQLColumn;
import net.anweisen.utilities.database.action.*;
import net.anweisen.utilities.database.exceptions.DatabaseException;
import net.anweisen.utilities.database.internal.abstraction.AbstractDatabase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabase extends AbstractDatabase {

	public RemoteDatabase() {
		super(null);
	}

	@Override
	public void connect() throws DatabaseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disconnect() throws DatabaseException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void disconnect0() throws Exception {
		// unused
	}

	@Override
	protected void connect0() throws Exception {
		// unused
	}

	@Override
	public boolean isConnected() {
		SocketChannel channel = CloudDriver.getInstance().getSocketComponent().getFirstChannel();
		return channel != null && channel.isActive();
	}

	@Override
	public void createTable(@Nonnull String name, @Nonnull SQLColumn... columns) throws DatabaseException {
		CloudDriver.getInstance().getSocketComponent().getFirstChannel().sendPacket(
			new DatabaseActionPacket(DatabaseActionType.CREATE_TABLE, buffer -> buffer.writeString(name).writeObjectCollection(
				Arrays.stream(columns).map(SerializableSQLColumn::new).collect(Collectors.toList())
			))
		);
	}

	@Nonnull
	@Override
	public DatabaseListTables listTables() {
		return new RemoteDatabaseListTables();
	}

	@Nonnull
	@Override
	public DatabaseCountEntries countEntries(@Nonnull String table) {
		return new RemoteDatabaseCountEntries(table);
	}

	@Nonnull
	@Override
	public DatabaseQuery query(@Nonnull String table) {
		return new RemoteDatabaseQuery(table);
	}

	@Nonnull
	@Override
	public DatabaseUpdate update(@Nonnull String table) {
		return new RemoteDatabaseUpdate(table);
	}

	@Nonnull
	@Override
	public DatabaseInsertion insert(@Nonnull String table) {
		return new RemoteDatabaseInsertion(table);
	}

	@Nonnull
	@Override
	public DatabaseInsertionOrUpdate insertOrUpdate(@Nonnull String table) {
		return new RemoteDatabaseInsertionOrUpdate(table);
	}

	@Nonnull
	@Override
	public DatabaseDeletion delete(@Nonnull String table) {
		return new RemoteDatabaseDeletion(table);
	}

	@Nonnull
	@Override
	public DatabaseConfig getConfig() {
		throw new UnsupportedOperationException();
	}
}
