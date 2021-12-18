package net.anweisen.cloud.master.database;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.driver.player.PlayerConstants;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utility.database.Database;
import net.anweisen.utility.database.DatabaseConfig;
import net.anweisen.utility.database.SQLColumn;
import net.anweisen.utility.database.SQLColumn.Type;
import net.anweisen.utility.database.SimpleDatabaseTypeResolver;
import net.anweisen.utility.document.Document;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterDatabaseManager implements DatabaseManager, LoggingApiUser {

	private Database database;

	@Override
	public void setDatabase(@Nonnull Database database) {
		Preconditions.checkNotNull(database, "Database cannot be null");
		this.database = database;
	}

	public void loadDatabase() {
		try {

			if (database != null)
				database.disconnect();

			Document config = CloudMaster.getInstance().getConfig().getDatabaseConfig();
			Class<? extends Database> databaseClass = SimpleDatabaseTypeResolver.findDatabaseType(config.getString("type", "null"));

			if (databaseClass == null)
				throw new IllegalStateException("Unknown database type " + config);

			Constructor<? extends Database> constructor = databaseClass.getConstructor(DatabaseConfig.class);
			database = constructor.newInstance(new DatabaseConfig(config.getDocument("config")));

			info("Connecting to database of type '{}'..", config.getString("type"));
			database.connect();
			info("Successfully connected to the database");

		} catch (Exception ex) {
			error("Unable to load database", ex);
			database = Database.empty(); // A database which will do nothing and return empty results
		}
	}

	@Nonnull
	@Override
	public Database getDatabase() {
		return database;
	}

}
