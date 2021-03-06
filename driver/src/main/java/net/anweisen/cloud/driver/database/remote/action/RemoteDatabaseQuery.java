package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket;
import net.anweisen.cloud.driver.network.packet.def.RemoteDatabaseActionPacket.DatabaseActionPayload;
import net.anweisen.utility.common.concurrent.task.Task;
import net.anweisen.utility.database.Order;
import net.anweisen.utility.database.action.DatabaseQuery;
import net.anweisen.utility.database.action.ExecutedQuery;
import net.anweisen.utility.database.exception.DatabaseException;
import net.anweisen.utility.database.internal.abstraction.DefaultExecutedQuery;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseQuery implements DatabaseQuery, RemoteDatabaseCallbackAction<ExecutedQuery> {

	private final Document document = Documents.newJsonDocument();
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
			.sendPacketQueryAsync(new RemoteDatabaseActionPacket(DatabaseActionPayload.QUERY, buffer -> buffer.writeString(table).writeDocument(document)))
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
