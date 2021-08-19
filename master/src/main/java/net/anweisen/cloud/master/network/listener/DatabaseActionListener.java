package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.database.remote.SerializableSQLColumn;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.DatabaseActionPacket.DatabaseActionType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.database.Order;
import net.anweisen.utilities.database.SQLColumn;
import net.anweisen.utilities.database.action.*;
import net.anweisen.utilities.database.action.hierarchy.OrderedAction;
import net.anweisen.utilities.database.action.hierarchy.SetAction;
import net.anweisen.utilities.database.action.hierarchy.WhereAction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DatabaseActionListener implements PacketListener {

	private final DatabaseManager manager;

	public DatabaseActionListener(@Nonnull DatabaseManager manager) {
		this.manager = manager;
	}

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		Buffer buffer = packet.getBuffer();

		DatabaseActionType type = buffer.readEnumConstant(DatabaseActionType.class);

		if (type.isSpecific()) {

			String table = buffer.readString();
			Document document = buffer.readDocument();

			switch (type) {
				case QUERY: {
					DatabaseQuery action = manager.getDatabase().query(table);
					applyWhere(document, action);
					applyOrder(document, action);
					if (document.contains("select"))
						action.select(document.getStringArray("select"));
					channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeDocumentCollection(action.execute().toList())));
					break;
				}
				case INSERT: {
					DatabaseInsertion action = manager.getDatabase().insert(table);
					applySet(document, action);
					action.execute();
					break;
				}
				case INSERT_OR_UPDATE: {
					DatabaseInsertionOrUpdate action = manager.getDatabase().insertOrUpdate(table);
					applySet(document, action);
					applyWhere(document, action);
					action.execute();
					break;
				}
				case UPDATE: {
					DatabaseUpdate action = manager.getDatabase().update(table);
					applySet(document, action);
					applyWhere(document, action);
					action.execute();
					break;
				}
				case DELETE: {
					DatabaseDeletion action = manager.getDatabase().delete(table);
					applyWhere(document, action);
					action.execute();
					break;
				}
			}
		} else {
			switch (type) {
				case LIST_TABLES: {
					Collection<String> tables = manager.getDatabase().listTables();
					channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeStringCollection(tables)));
					break;
				}
				case CREATE_TABLE: {
					String name = buffer.readString();
					Collection<SQLColumn> columns = buffer.readObjectCollection(SerializableSQLColumn.class).stream().map(SerializableSQLColumn::getColumn).collect(Collectors.toList());
					manager.getDatabase().createTable(name, columns.toArray(new SQLColumn[0]));
					break;
				}
			}
		}

	}

	protected void applyWhere(@Nonnull Document document, @Nonnull WhereAction action) {
		if (document.contains("where")) {
			Document where = document.getDocument("where");
			for (String key : where.keys()) {
				action.where(key, where.getObject(key));
			}
		}
		if (document.contains("whereNot")) {
			Document where = document.getDocument("whereNot");
			for (String key : where.keys()) {
				action.whereNot(key, where.getObject(key));
			}
		}
	}

	protected void applyOrder(@Nonnull Document document, @Nonnull OrderedAction action) {
		if (document.contains("order")) {
			Document order = document.getDocument("order");
			action.orderBy(order.getString("field"), order.getEnum("rule", Order.class));
		}
	}

	protected void applySet(@Nonnull Document document, @Nonnull SetAction action) {
		if (document.contains("set")) {
			Document set = document.getDocument("set");
			for (String key : set.keys()) {
				action.set(key, set.getObject(key));
			}
		}
	}

}