package net.anweisen.cloud.driver.network.http;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpRequest extends HttpMessage<HttpRequest> {

	@Nonnull
	HttpMethod getMethod();

	@Nonnull
	String getUri();

	@Nonnull
	String getPath();

	@Nonnull
	Map<String, List<String>> getQueryParameters();

	@Nonnull
	Map<String, String> getPathParameters();

}
