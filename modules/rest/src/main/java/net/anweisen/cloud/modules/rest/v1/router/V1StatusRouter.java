package net.anweisen.cloud.modules.rest.v1.router;

import net.anweisen.cloud.driver.CloudDriver;
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
@HttpRouter("status")
public class V1StatusRouter {

	@HttpEndpoint(method = HttpMethod.GET)
	public void getIndex(@Nonnull HttpContext context) {
		CloudDriver driver = CloudDriver.getInstance();

		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(Documents.newJsonDocument(
				"up_time", driver.getUpTime(),
				"startup_time", driver.getStartupTime()
			))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

}
