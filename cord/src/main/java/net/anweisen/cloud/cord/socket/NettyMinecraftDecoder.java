package net.anweisen.cloud.cord.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import net.anweisen.cloud.cord.CloudCord;
import net.anweisen.cloud.cord.socket.stream.ProxyDownstreamHandler;
import net.anweisen.cloud.cord.socket.stream.ProxyUpstreamHandler;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.netty.NettyUtils;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceProperty;
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
public class NettyMinecraftDecoder extends SimpleChannelInboundHandler<ByteBuf> implements LoggingApiUser {

	private final NettyCordSocketServer server;

	public NettyMinecraftDecoder(@Nonnull NettyCordSocketServer server) {
		this.server = server;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext context, ByteBuf buffer) throws Exception {
		try {
			int packetLength = NettyUtils.readVarInt(buffer);
			int packetId = NettyUtils.readVarInt(buffer);

			// https://wiki.vg/Protocol#Handshake
			if (packetId == 0x00) {
				int protocolVersion = NettyUtils.readVarInt(buffer);
				String hostname = NettyUtils.readString(buffer);
				int port = buffer.readUnsignedShort();
				int nextState = NettyUtils.readVarInt(buffer);

				connectClient(context.channel(), hostname, buffer.retain());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			buffer.resetReaderIndex(); // Wait until we receive the full packet
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable ex) throws Exception {
		error("Exception caught in MinecraftDecoder", ex);

		ProxyUpstreamHandler upstreamHandler = context.channel().attr(PacketUtils.UPSTREAM_HANDLER).get();
		ProxyDownstreamHandler downstreamHandler = context.channel().attr(PacketUtils.DOWNSTREAM_HANDLER).get();
		if (upstreamHandler != null)
			upstreamHandler.getChannel().close();
		if (downstreamHandler != null)
			downstreamHandler.getChannel().close();
	}

	@Override
	public void channelActive(ChannelHandlerContext context) throws Exception {
		CloudCord.getInstance().getTrafficReporter().reportNewConnection();
	}

	@Override
	public void channelInactive(ChannelHandlerContext context) throws Exception {
		debug("Some channel got disconnected from the MinecraftDecoder");
		context.channel().close();
	}

	public void connectClient(@Nonnull Channel channel, @Nonnull String hostname, @Nonnull ByteBuf handshakePacket) throws Exception {
		ServiceInfo proxyService = findProxyForHostName(hostname);
		if (proxyService == null) {
			warn("Unable to find proxy @ hostname {}", hostname);
			channel.close();
			return;
		}
//		// TODO
//		ServiceInfo proxyService = new ServiceInfo(
//			UUID.randomUUID(), null, "Proxy", 1, ServiceEnvironment.BUNGEECORD,
//			ServiceState.RUNNING, "Node-1", "45.13.227.194", 25565, true, Document.create()
//		);

		connectClient(channel, proxyService, hostname, handshakePacket);
	}

	public void connectClient(@Nonnull Channel clientChannel, @Nonnull ServiceInfo proxyService, @Nonnull String hostname, @Nonnull ByteBuf handshakePacket) throws Exception {

		ProxyDownstreamHandler downstreamHandler = clientChannel.attr(PacketUtils.DOWNSTREAM_HANDLER).get() == null ? new ProxyDownstreamHandler(clientChannel) : clientChannel.attr(PacketUtils.DOWNSTREAM_HANDLER).get();
		clientChannel.attr(PacketUtils.DOWNSTREAM_HANDLER).set(downstreamHandler);
		clientChannel.attr(PacketUtils.CONNECTION_STATE).set(ConnectionState.HANDSHAKE);
		info("[{}] -> Upstream has successfully connected. Connecting Downstream to '{}' | {}..", downstreamHandler.getClientAddress(), proxyService.getName(), proxyService.getAddress());

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
		debug("Connecting for client {} for hostname {} to '{}': {}..", clientChannel.remoteAddress(), hostname, proxyService.getName(), proxyService.getAddress());
		connectFuture.addListener((ChannelFutureListener) future -> {
			Channel serverChannel = future.channel();
			if (future.isSuccess()) {
				debug("Successfully connected for {} to hostname '{}' using proxy '{}'@{} (cord <-> proxy: {})", clientChannel.remoteAddress(), hostname, proxyService.getName(), proxyService.getAddress(), serverChannel.localAddress());

				if (clientChannel.pipeline().get("minecraft-decoder") != null)
					clientChannel.pipeline().remove("minecraft-decoder");

				if (clientChannel.attr(PacketUtils.UPSTREAM_HANDLER).get() == null) {
					ProxyUpstreamHandler upstreamHandler = new ProxyUpstreamHandler(serverChannel, downstreamHandler);
					clientChannel.pipeline().addLast(upstreamHandler);
					clientChannel.attr(PacketUtils.UPSTREAM_HANDLER).set(upstreamHandler);
				} else {
					clientChannel.attr(PacketUtils.UPSTREAM_HANDLER).get().setChannel(serverChannel);
				}
				info("[{}] <- Proxy Downstream has successfully connected to '{}'", downstreamHandler.getClientAddress(), proxyService.getName());

//				CloudDriver.getInstance().getSocketComponent().sendPacketSync(
//					new Packet(PacketConstants.CORD_CHANNEL, Buffer.create()
//						.writeString(proxyService.getName())
//						.writeObject(HostAndPort.fromSocketAddress(clientChannel.remoteAddress()))
//						.writeObject(HostAndPort.fromSocketAddress(serverChannel.localAddress()))
//					)
//				); // TODO wait for callback

				serverChannel.writeAndFlush(Unpooled.copiedBuffer(handshakePacket.resetReaderIndex().retain()));
				trace("Sent handshake packet for {} to proxy..", downstreamHandler.getClientAddress());
				clientChannel.attr(PacketUtils.CONNECTION_STATE).set(ConnectionState.PROXY);
			} else {
				warn("[{}] Proxy Downstream could not be connected successfully", downstreamHandler.getClientAddress());
				clientChannel.close();
				serverChannel.close();
			}
		});

	}

	// TODO max players
	// TODO move to helper class

	private static final Comparator<ServiceInfo> comparator = (service1, service2) -> {
		int online1 = service1.get(ServiceProperty.ONLINE_PLAYER_COUNT);
		int online2 = service2.get(ServiceProperty.ONLINE_PLAYER_COUNT);
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
