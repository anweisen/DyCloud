package net.anweisen.cloud.driver.network.http.auth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpAuthHandler {

	@Nullable
	HttpAuthUser getAuthUser(@Nonnull String token);

}
