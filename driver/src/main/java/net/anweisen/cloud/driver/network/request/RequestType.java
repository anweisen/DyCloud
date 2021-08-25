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

	CREATE_SERVICE(RequestTypeCategory.SERVICE_FACTORY),

	GET_SERVICES(RequestTypeCategory.SERVICE_MANAGEMENT),
	GET_SERVICES_BY_TASK(RequestTypeCategory.SERVICE_MANAGEMENT),
	GET_SERVICES_BY_NODE(RequestTypeCategory.SERVICE_MANAGEMENT),
	GET_SERVICE_BY_NAME(RequestTypeCategory.SERVICE_MANAGEMENT),
	GET_SERVICE_BY_UUID(RequestTypeCategory.SERVICE_MANAGEMENT),

	GET_TASKS(RequestTypeCategory.SERVICE_CONFIG),
	GET_TASK_BY_NAME(RequestTypeCategory.SERVICE_CONFIG),
	GET_TEMPLATE_STORAGES(RequestTypeCategory.SERVICE_CONFIG), // TODO
	GET_TEMPLATE_STORAGE_BY_NAME(RequestTypeCategory.SERVICE_CONFIG), // TODO
	HAS_TEMPLATE_STORAGE(RequestTypeCategory.SERVICE_CONFIG), // TODO ugly

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
