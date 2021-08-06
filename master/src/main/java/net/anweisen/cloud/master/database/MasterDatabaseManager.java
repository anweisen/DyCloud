package net.anweisen.cloud.master.database;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.database.DatabaseManager;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.database.Database;
import net.anweisen.utilities.database.DatabaseConfig;
import net.anweisen.utilities.database.SimpleDatabaseTypeResolver;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterDatabaseManager implements DatabaseManager {

	private Database database;

	public void setDatabase(@Nonnull Database database) {
		Preconditions.checkNotNull(database, "Database cannot be null");
		this.database = database;
	}

	public void loadDatabase() {
		try {

			if (database != null)
				database.disconnect();

			Document config = CloudMaster.getInstance().getConfig().getDatabaseConfig();
			Class<? extends Database> databaseClass = SimpleDatabaseTypeResolver.findDatabaseType(config.getString("type"));

			if (databaseClass == null)
				throw new IllegalStateException("Unknown database type " + config);

			Constructor<? extends Database> constructor = databaseClass.getConstructor(DatabaseConfig.class);
			database = constructor.newInstance(new DatabaseConfig(config.getDocument("config")));

			CloudDriver.getInstance().getLogger().info("Connecting to database of type '{}'..", config.getString("type"));
			database.connect();
			CloudDriver.getInstance().getLogger().info("Successfully connected to the database");

		} catch (Exception ex) {
			ex.printStackTrace();
			database = Database.empty(); // A database which will do nothing and return empty results
		}
	}

	@Nonnull
	@Override
	public Database getDatabase() {
		return database;
	}

}
