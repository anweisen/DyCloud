package net.anweisen.cloud.driver.network.request;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum RequestType {

	;

	private final RequestTypeCategory category;

	RequestType(RequestTypeCategory category) {
		this.category = category;
	}

	@Nonnull
	public RequestTypeCategory getCategory() {
		return category;
	}

}
