package net.anweisen.cloud.modules.rest.v1.router;

import net.anweisen.cloud.driver.network.http.HttpCodes;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;
import net.anweisen.cloud.driver.network.http.handler.HttpEndpoint;
import net.anweisen.cloud.driver.network.http.handler.HttpRouter;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@HttpRouter("ping")
public class V1PingRouter {

	@HttpEndpoint(method = HttpMethod.GET)
	public void getIndex(@Nonnull HttpContext context) {
		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(Documents.newJsonDocument("success", true))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

}
