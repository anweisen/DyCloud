package net.anweisen.cloud.modules.rest.v1.router;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.http.HttpCodes;
import net.anweisen.cloud.driver.network.http.HttpContext;
import net.anweisen.cloud.driver.network.http.HttpMethod;
import net.anweisen.cloud.driver.network.http.handler.HttpEndpoint;
import net.anweisen.cloud.driver.network.http.handler.HttpRouter;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.modules.rest.v1.V1ObjectSerializer;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@HttpRouter("player")
public class V1PlayerRouter {

	@HttpEndpoint(method = HttpMethod.GET, path = "online", permission = "web.player.online")
	public void getOnline(@Nonnull HttpContext context) {
		Collection<CloudPlayer> players = CloudDriver.getInstance().getPlayerManager().getOnlinePlayers();

		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(players.stream().map(V1ObjectSerializer::forOnlinePlayer).collect(Collectors.toList()))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

	@HttpEndpoint(method = HttpMethod.GET, path = "online/count", permission = "web.player.online.count")
	public void getOnlineCount(@Nonnull HttpContext context) {
		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(Documents.newJsonDocument("count", CloudDriver.getInstance().getPlayerManager().getOnlinePlayerCount()))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

	@HttpEndpoint(method = HttpMethod.GET, path = "registered/count", permission = "web.player.registered.count")
	public void getRegisteredCount(@Nonnull HttpContext context) {
		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(Documents.newJsonDocument("count", CloudDriver.getInstance().getPlayerManager().getRegisteredPlayerCount()))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

	@HttpEndpoint(method = HttpMethod.POST, path = "{player}/kick", permission = "web.player.kick")
	public void postPlayerKick(@Nonnull HttpContext context) {

	}

}
