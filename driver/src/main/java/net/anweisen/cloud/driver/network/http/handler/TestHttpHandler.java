package net.anweisen.cloud.driver.network.http.handler;

import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@HttpRouter("test")
public class TestHttpHandler {

	@HttpEndpoint(method = HttpMethod.GET)
	public void get(@Nonnull HttpContext context) {
		System.out.println(context.getRequest().getPathParameters());
	}

}
