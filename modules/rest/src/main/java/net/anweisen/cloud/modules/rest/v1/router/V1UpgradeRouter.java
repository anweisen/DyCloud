package net.anweisen.cloud.modules.rest.v1.router;

import net.anweisen.cloud.driver.network.http.HttpCodes;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthHandler;
import net.anweisen.cloud.driver.network.http.auth.HttpAuthUser;
import net.anweisen.cloud.driver.network.http.handler.HttpEndpoint;
import net.anweisen.cloud.driver.network.http.handler.HttpRouter;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketChannel;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketFrameType;
import net.anweisen.utilities.common.collection.pair.Tuple;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@HttpRouter("upgrade")
public class V1UpgradeRouter {

	@HttpEndpoint(method = HttpMethod.GET)
	public void upgrade(@Nonnull HttpContext context) {

		// TODO this auth code is mostly copied
		Tuple<HttpAuthHandler, HttpAuthUser> values = Tuple.empty();
		List<String> auth = context.getRequest().getQueryParameters().get("auth");
		if (auth == null || auth.isEmpty()) {
			context.getResponse().setStatusCode(HttpCodes.UNAUTHORIZED);
			return;
		}
		context.getServer().applyUserAuth(values, auth.get(0));
		if (values.getSecond() == null) {
			context.getResponse().setStatusCode(HttpCodes.UNAUTHORIZED);
			return;
		}
		if (!values.getSecond().hasPermission("web.upgrade")) {
			context.getResponse().setStatusCode(HttpCodes.FORBIDDEN).setBody("Permission " + "web.upgrade" + " required");
			return;
		}

		WebSocketChannel websocket = context.upgrade();
		websocket.sendFrame(WebSocketFrameType.TEXT, "hi");
	}

}
