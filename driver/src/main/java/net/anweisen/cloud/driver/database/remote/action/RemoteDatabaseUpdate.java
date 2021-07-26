package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket.DatabaseActionType;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.database.action.DatabaseUpdate;
import net.anweisen.utilities.database.exceptions.DatabaseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseUpdate implements DatabaseUpdate {

	private final Document document = Document.newJsonDocument();
	private final String table;

	public RemoteDatabaseUpdate(@Nonnull String table) {
		this.table = table;
	}

	@Nonnull
	@Override
	public DatabaseUpdate where(@Nonnull String field, @Nullable Object value) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseUpdate where(@Nonnull String field, @Nullable Number value) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseUpdate where(@Nonnull String field, @Nullable String value, boolean ignoreCase) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseUpdate where(@Nonnull String field, @Nullable String value) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseUpdate whereNot(@Nonnull String field, @Nullable Object value) {
		document.getDocument("whereNot").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseUpdate set(@Nonnull String field, @Nullable Object value) {
		document.getDocument("set").set(field, value);
		return this;
	}

	// TODO maybe a callback ?

	public Void execute() throws DatabaseException {
		CloudDriver.getInstance().getSocketComponent().getFirstChannel().sendPacket(
			new DatabaseActionPacket(DatabaseActionType.UPDATE, buffer -> buffer.writeString(table).writeDocument(document))
		);
		return null;
	}

	@Nonnull
	@Override
	public Task<Void> executeAsync() {
		return Task.syncRunExceptionally(this::execute);
	}

	@Override
	public boolean equals(@Nonnull DatabaseUpdate other) {
		return equals((Object) other);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RemoteDatabaseUpdate that = (RemoteDatabaseUpdate) o;
		return Objects.equals(document, that.document) && Objects.equals(table, that.table);
	}

	@Override
	public int hashCode() {
		return Objects.hash(document, table);
	}
}
