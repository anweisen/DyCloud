package net.anweisen.cloud.driver.network.netty.http;

import com.google.common.base.Preconditions;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.*;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.http.HttpChannel;
import net.anweisen.cloud.driver.network.http.HttpServer;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketChannel;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketFrameType;
import net.anweisen.cloud.driver.network.http.websocket.WebSocketListener;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyWebSocketChannel implements WebSocketChannel, LoggingApiUser {

	protected final Collection<WebSocketListener> listeners = new CopyOnWriteArrayList<>();
	protected final NettyHttpContext context;
	protected final Channel nettyChannel;

	public NettyWebSocketChannel(@Nonnull NettyHttpContext context, @Nonnull Channel nettyChannel) {
		this.context = context;
		this.nettyChannel = nettyChannel;
	}

	@Override
	public void sendFrame(@Nonnull WebSocketFrameType type, @Nonnull String text) {
		sendFrame(type, text.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void sendFrame(@Nonnull WebSocketFrameType type, @Nonnull byte[] data) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(data);

		WebSocketFrame frame;
		trace("Sending {} on {}", type, this);

		switch (type) {
			case PING:
				frame = new PingWebSocketFrame(Unpooled.buffer(data.length).writeBytes(data));
				break;
			case PONG:
				frame = new PongWebSocketFrame(Unpooled.buffer(data.length).writeBytes(data));
				break;
			case TEXT:
				frame = new TextWebSocketFrame(Unpooled.buffer(data.length).writeBytes(data));
				break;
			case BINARY:
				frame = new BinaryWebSocketFrame(Unpooled.buffer(data.length).writeBytes(data));
				break;
			default:
				throw new IllegalArgumentException("Cannot send " + type + " frame");
		}

		nettyChannel.writeAndFlush(frame).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	@Override
	public void close() {
		close(1000, "websocket closed");
	}

	@Override
	public void close(int statusCode, @Nonnull String closeReason) {
		nettyChannel.writeAndFlush(new CloseWebSocketFrame(statusCode, closeReason)).addListener(ChannelFutureListener.CLOSE);
	}

	@Nonnull
	@Override
	public HttpChannel getChannel() {
		return context.getChannel();
	}

	@Nonnull
	@Override
	public HttpServer getServer() {
		return context.getServer();
	}

	@Override
	public void addListener(@Nonnull WebSocketListener listener) {
		listeners.add(listener);
	}

	@Override
	public void clearListeners() {
		listeners.clear();
	}

	@Nonnull
	@Override
	public Collection<WebSocketListener> getListeners() {
		return listeners;
	}

	@Override
	public String toString() {
		return "WebSocketChannel[client=" + getChannel().getClientAddress() + " server=" + getChannel().getServerAddress() + "]";
	}
}
