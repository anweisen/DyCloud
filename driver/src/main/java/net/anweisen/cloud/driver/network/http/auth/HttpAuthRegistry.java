package net.anweisen.cloud.driver.network.http.auth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class HttpAuthRegistry {

	private final Map<String, HttpAuthHandler> handlers = new LinkedHashMap<>();

	public void registerAuthMethodHandler(@Nonnull String type, @Nonnull HttpAuthHandler handler) {
		handlers.put(type, handler);
	}

	@Nullable
	public HttpAuthHandler getAuthMethodHandler(@Nonnull String type) {
		return handlers.get(type);
	}

	public void unregisterAuthMethodHandler(@Nonnull String type) {
		handlers.remove(type);
	}

}
