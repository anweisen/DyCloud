package net.anweisen.cloud.driver.database.remote;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.utility.database.Database;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteDatabaseManager implements DatabaseManager {

	private Database database = new RemoteDatabase();

	@Nonnull
	@Override
	public Database getDatabase() {
		return database;
	}

	@Override
	public void setDatabase(@Nonnull Database database) {
		Preconditions.checkNotNull(database, "Database cannot be null");
		this.database = database;
	}

}
