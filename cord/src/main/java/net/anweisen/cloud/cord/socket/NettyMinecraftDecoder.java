package net.anweisen.cloud.cord.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import net.anweisen.cloud.cord.socket.stream.ProxyDownstreamHandler;
import net.anweisen.cloud.cord.socket.stream.ProxyUpstreamHandler;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceProperties;
import net.anweisen.cloud.driver.service.specific.ServiceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
@Sharable
public class NettyMinecraftDecoder extends SimpleChannelInboundHandler<ByteBuf> {

	private final NettyCordSocketServer server;

	public NettyMinecraftDecoder(@Nonnull NettyCordSocketServer server) {
		this.server = server;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf buffer) throws Exception {
		try {
			int packetLength = NettyUtils.readVarInt(buffer);
			int packetId = NettyUtils.readVarInt(buffer);
			if (packetId == 0) {
				int clientVersion = NettyUtils.readVarInt(buffer);
				String hostname = NettyUtils.readString(buffer);
				int port = buffer.readUnsignedShort();
				int state = NettyUtils.readVarInt(buffer);

				connectClient(context.channel(), hostname, buffer.retain());
			}

		} catch (Exception ex) {
			buffer.resetReaderIndex(); // Wait until we receive the full packet
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
		CloudDriver.getInstance().getLogger().error("Exception caught in MinecraftDecoder", ex);
	}

	public void connectClient(@Nonnull Channel channel, @Nonnull String hostname, @Nonnull ByteBuf loginPacket) throws Exception {
		ServiceInfo proxyService = findProxyForHostName(hostname);
		if (proxyService == null) {
			CloudDriver.getInstance().getLogger().warn("Unable to find proxy @ hostname {}", hostname);
			channel.close();
			return;
		}

		connectClient(channel, proxyService, hostname, loginPacket);
	}

	public void connectClient(@Nonnull Channel channel, @Nonnull ServiceInfo proxyService, @Nonnull String hostname, @Nonnull ByteBuf loginPacket) throws Exception {

		ProxyDownstreamHandler downstreamHandler = channel.attr(PacketUtils.DOWNSTREAM_HANDLER) == null ? new ProxyDownstreamHandler(channel) : channel.attr(PacketUtils.DOWNSTREAM_HANDLER).get();
		channel.attr(PacketUtils.DOWNSTREAM_HANDLER).set(downstreamHandler);
		channel.attr(PacketUtils.CONNECTION_STATE).set(ConnectionState.HANDSHAKE);

		Bootstrap bootstrap = new Bootstrap()
			.group(server.getWorkerEventLoopGroup())
			.channelFactory(NettyUtils.getClientChannelFactory())
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(downstreamHandler);
				}
			});

		ChannelFuture connectFuture = bootstrap.connect(proxyService.getAddress().toInetSocketAddress());
		connectFuture.addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				CloudDriver.getInstance().getLogger().info("[{}] successfully connected to '{}' using proxy '{}'", channel.remoteAddress(), hostname, proxyService.getName());

				if (channel.attr(PacketUtils.UPSTREAM_HANDLER).get() == null) {
					ProxyUpstreamHandler upstreamHandler = new ProxyUpstreamHandler(connectFuture.channel(), downstreamHandler);
					channel.pipeline().addLast(upstreamHandler);
					channel.attr(PacketUtils.UPSTREAM_HANDLER).set(upstreamHandler);
				} else {
					channel.attr(PacketUtils.UPSTREAM_HANDLER).get().setChannel(connectFuture.channel());
				}

				if (channel.pipeline().get("minecraft-decoder") != null)
					channel.pipeline().remove("minecraft-decoder");

				loginPacket.resetReaderIndex();
				future.channel().writeAndFlush(loginPacket.retain());
				channel.attr(PacketUtils.CONNECTION_STATE).set(ConnectionState.PROXY);
			} else {
				channel.close();
				connectFuture.channel().close();
			}
		});

	}



	// TODO max players
	// TODO move to helper class

	private static final Comparator<ServiceInfo> comparator = (service1, service2) -> {
		int online1 = service1.getProperties().getInt(ServiceProperties.ONLINE_COUNT);
		int online2 = service2.getProperties().getInt(ServiceProperties.ONLINE_COUNT);
		if (online1 != online2)
			return online1 - online2; // we prefer fewer players
		return 0;
	};

	@Nullable
	private static ServiceInfo findProxyForHostName(@Nonnull String hostname) {
		Collection<ServiceTask> proxyTasks = CloudDriver.getInstance().getServiceConfigManager().getTasks(ServiceType.PROXY);
		proxyTasks.removeIf(task -> !matchesHostName(hostname, task));

		List<ServiceInfo> services = new ArrayList<>();
		proxyTasks.forEach(task -> services.addAll(task.findServices()));
		services.removeIf(service -> !service.isReady());
		services.sort(comparator);

		if (services.isEmpty()) return null;
		return services.get(0);
	}

	private static boolean matchesHostName(@Nonnull String hostname, @Nonnull ServiceTask task) {
		for (String cordHostname : task.getCordHostnames()) {
			if (matchesHostName(hostname, cordHostname))
				return true;
		}
		return false;
	}

	private static boolean matchesHostName(@Nonnull String input, @Nonnull String pattern) {
		if (pattern.equals("*")) return true;
		return createPatternFromHostName(input.toLowerCase().trim()).matcher(input.toLowerCase().trim()).matches();
	}

	@Nonnull
	private static Pattern createPatternFromHostName(String hostname) {
		StringBuilder sb = new StringBuilder();
		for (String part : hostname.split("\\*")) {
			if (part.length() > 0) sb.append(Pattern.quote(part));
			sb.append(".*");
		}
		return Pattern.compile(sb.toString());
	}

}
