package net.anweisen.cloud.driver.network.http.auth;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface HttpAuthUser {

	boolean hasPermission(@Nonnull String permission);

}
