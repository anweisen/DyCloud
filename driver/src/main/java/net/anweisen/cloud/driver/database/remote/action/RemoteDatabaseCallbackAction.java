package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.utilities.database.action.DatabaseAction;
import net.anweisen.utilities.database.exceptions.DatabaseException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface RemoteDatabaseCallbackAction<R> extends DatabaseAction<R> {

	@Override
	default R execute() throws DatabaseException {
		try {
			return executeAsync().get(15, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			throw new DatabaseException("Operation timed out");
		} catch (InterruptedException ex) {
			throw new DatabaseException("Interrupted while waiting", ex);
		} catch (ExecutionException ex) {
			if (ex.getCause() instanceof DatabaseException)
				throw (DatabaseException) ex.getCause();
			throw new DatabaseException(ex);
		}
	}

}
