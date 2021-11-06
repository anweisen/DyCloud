package net.anweisen.cloud.driver.network.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.http.HttpMethod;
import net.anweisen.cloud.driver.network.http.handler.RegisteredHandler;
import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpChannelHandler extends SimpleChannelInboundHandler<HttpRequest> implements LoggingApiUser {

	protected final NettyHttpServer server;
	protected final HostAndPort address;

	protected NettyHttpChannel channel;

	public NettyHttpChannelHandler(@Nonnull NettyHttpServer server, @Nonnull HostAndPort address) {
		this.server = server;
		this.address = address;
	}

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		channel = new NettyHttpChannel(address, HostAndPort.fromSocketAddress(context.channel().remoteAddress()));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
		if (!(ex instanceof IOException)) {
			error("Error on channel {}", channel, ex);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, HttpRequest request) throws Exception {
		if (request.decoderResult().isFailure()) {
			context.close();
			return;
		}

		URI uri = URI.create(request.uri());

		String fullPath = uri.getPath();
		trace("Received {} on route '{}'", request.method(), fullPath);

		Map<String, String> pathParameters = new LinkedHashMap<>();
		NettyHttpContext httpContext = new NettyHttpContext(uri, channel, server, request, pathParameters);

		HttpMethod method = httpContext.getRequest().getMethod();
		String[] pathEntries = fullPath.split("/");
		String[] handlerPathEntries;
		for (RegisteredHandler handler : server.getHandlerRegistry().getHandlers()) {
			if (httpContext.cancelNext) break;

			if (!Arrays.asList(handler.getMethods()).contains(method)) continue;
			handlerPathEntries = handler.getPath().split("/");
			if (!checkPath(pathEntries, handlerPathEntries, pathParameters)) continue;

			try {
				handler.execute(httpContext);
			} catch (Exception ex) {
				error("Could not execute http handler for '{}'", fullPath, ex);
			}

		}

	}

	private boolean checkPath(@Nonnull String[] pathEntries, @Nonnull String[] handlerPathEntries, @Nonnull Map<String, String> pathParameters) {

		System.out.println(Arrays.toString(pathEntries));
		System.out.println(Arrays.toString(handlerPathEntries));

		for (int i = 0; i < handlerPathEntries.length; i++) {

			String handlerPathEntry = handlerPathEntries[i];

			if (i >= pathEntries.length)
				return false;

			String providedPathEntry = pathEntries[i];

			if (handlerPathEntry.equals("*"))
				continue;
			if (handlerPathEntry.startsWith("{") && handlerPathEntry.endsWith("}")) {
				String pathArgumentName = handlerPathEntry.substring(1, handlerPathEntry.length() - 1);
				pathParameters.put(pathArgumentName, providedPathEntry);
				continue;
			}

			if (!handlerPathEntry.equalsIgnoreCase(providedPathEntry))
				return false;

		}

		return true;
	}
}
