package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket.DatabaseActionType;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.database.action.DatabaseInsertion;
import net.anweisen.utilities.database.exceptions.DatabaseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseInsertion implements DatabaseInsertion {

	private final Document document = Document.create();
	private final String table;

	public RemoteDatabaseInsertion(@Nonnull String table) {
		this.table = table;
	}

	@Nonnull
	@Override
	public DatabaseInsertion set(@Nonnull String field, @Nullable Object value) {
		document.getDocument("set").set(field, value);
		return this;
	}

	// TODO maybe a callback ?

	public Void execute() throws DatabaseException {
		CloudDriver.getInstance().getSocketComponent().getFirstChannel().sendPacket(
			new RemoteDatabaseActionPacket(DatabaseActionType.INSERT, buffer -> buffer.writeString(table).writeDocument(document))
		);
		return null;
	}

	@Nonnull
	@Override
	public Task<Void> executeAsync() {
		return Task.syncRunExceptionally(this::execute);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RemoteDatabaseInsertion that = (RemoteDatabaseInsertion) o;
		return Objects.equals(document, that.document) && Objects.equals(table, that.table);
	}

	@Override
	public int hashCode() {
		return Objects.hash(document, table);
	}
}
