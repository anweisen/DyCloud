package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket.DatabaseActionType;
import net.anweisen.utilities.common.concurrent.task.Task;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.database.Order;
import net.anweisen.utilities.database.action.DatabaseQuery;
import net.anweisen.utilities.database.action.ExecutedQuery;
import net.anweisen.utilities.database.exceptions.DatabaseException;
import net.anweisen.utilities.database.internal.abstraction.DefaultExecutedQuery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseQuery implements DatabaseQuery, RemoteDatabaseCallbackAction<ExecutedQuery> {

	private final Document document = Document.create();
	private final String table;

	public RemoteDatabaseQuery(@Nonnull String table) {
		this.table = table;
	}

	@Nonnull
	@Override
	public DatabaseQuery where(@Nonnull String field, @Nullable Object value) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseQuery where(@Nonnull String field, @Nullable Number value) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseQuery where(@Nonnull String field, @Nullable String value, boolean ignoreCase) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseQuery where(@Nonnull String field, @Nullable String value) {
		document.getDocument("where").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseQuery whereNot(@Nonnull String field, @Nullable Object value) {
		document.getDocument("whereNot").set(field, value);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseQuery select(@Nonnull String... selection) {
		document.set("select", selection);
		return this;
	}

	@Nonnull
	@Override
	public DatabaseQuery orderBy(@Nonnull String field, @Nonnull Order order) {
		document.getDocument("order").set("field", field).set("rule", order);
		return this;
	}

	@Nonnull
	@Override
	public ExecutedQuery execute() throws DatabaseException {
		return RemoteDatabaseCallbackAction.super.execute();
	}

	@Nonnull
	@Override
	public Task<ExecutedQuery> executeAsync() {
		return CloudDriver.getInstance().getSocketComponent().getFirstChannel()
			.sendQueryAsync(new RemoteDatabaseActionPacket(DatabaseActionType.QUERY, buffer -> buffer.writeString(table).writeDocument(document)))
			.map(packet -> new DefaultExecutedQuery(new ArrayList<>(packet.getBuffer().readDocumentCollection())));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RemoteDatabaseQuery that = (RemoteDatabaseQuery) o;
		return Objects.equals(document, that.document) && Objects.equals(table, that.table);
	}

	@Override
	public int hashCode() {
		return Objects.hash(document, table);
	}
}
