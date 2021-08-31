package net.anweisen.cloud.driver.network.request;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public enum RequestType {

	GET_TEMPLATES(RequestTypeCategory.TEMPLATE_STORAGE),
	HAS_TEMPLATE(RequestTypeCategory.TEMPLATE_STORAGE),
	LOAD_TEMPLATE_STREAM(RequestTypeCategory.TEMPLATE_STORAGE),

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
