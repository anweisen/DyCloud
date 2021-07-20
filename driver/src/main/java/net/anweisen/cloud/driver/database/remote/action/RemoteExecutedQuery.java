package net.anweisen.cloud.driver.database.remote.action;

import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.database.internal.abstraction.AbstractExecutedQuery;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteExecutedQuery extends AbstractExecutedQuery {

	public RemoteExecutedQuery(@Nonnull List<Document> results) {
		super(results);
	}

	public RemoteExecutedQuery(@Nonnull Collection<Document> results) {
		super(results instanceof List ? (List<Document>) results : new ArrayList<>(results));
	}

}
