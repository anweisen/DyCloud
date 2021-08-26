package net.anweisen.cloud.driver.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.network.exception.SilentDecoderException;
import net.anweisen.utilities.common.collection.NamedThreadFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public final class NettyUtils {

	private static final ThreadFactory EVENT_LOOP_THREAD_FACTORY = new NamedThreadFactory("EventLoopGroup");
	private static final RejectedExecutionHandler DEFAULT_REJECTED_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

	static {
		// use jdk logger to prevent issues with older slf4j versions
		// like them bundled in spigot 1.8
		InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
		// check if the leak detection level is set before overriding it
		// may be useful for debugging of the network
		if (System.getProperty("io.netty.leakDetection.level") == null) {
			ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
		}

		if (KQueue.isAvailable()) {
			CloudDriver.getInstance().getLogger().info("KQueue is available, utilising it..");
		} else if (Epoll.isAvailable()) {
			CloudDriver.getInstance().getLogger().info("Epoll is available, utilising it..");
		}
	}

	@Nonnull
	@CheckReturnValue
	public static EventLoopGroup newEventLoopGroup() {
		return Epoll.isAvailable() ? new EpollEventLoopGroup(4, getEventLoopThreadFactory())
			: KQueue.isAvailable() ? new KQueueEventLoopGroup(4, getEventLoopThreadFactory())
								   : new NioEventLoopGroup(4, getEventLoopThreadFactory());
	}

	@Nonnull
	@CheckReturnValue
	public static Executor newPacketDispatcher() {
		// a cached pool with a thread idle-lifetime of 30 seconds
		// rejected tasks will be executed on the calling thread (See ThreadPoolExecutor.CallerRunsPolicy)
		return new ThreadPoolExecutor(
			0,
			getThreadAmount(),
			30L,
			TimeUnit.SECONDS,
			new SynchronousQueue<>(true),
			new NamedThreadFactory("PacketDispatcher"),
			DEFAULT_REJECTED_HANDLER
		);
	}

	@Nonnull
	@CheckReturnValue
	public static ChannelFactory<? extends Channel> getClientChannelFactory() {
		return Epoll.isAvailable() ? EpollSocketChannel::new
			: KQueue.isAvailable() ? KQueueSocketChannel::new
								   : NioSocketChannel::new;
	}

	@Nonnull
	@CheckReturnValue
	public static ChannelFactory<? extends ServerChannel> getServerChannelFactory() {
		return Epoll.isAvailable() ? EpollServerSocketChannel::new
			: KQueue.isAvailable() ? KQueueServerSocketChannel::new
			                       : NioServerSocketChannel::new;
	}

	@Nonnull
	@CheckReturnValue
	public static ThreadFactory getEventLoopThreadFactory() {
		return EVENT_LOOP_THREAD_FACTORY;
	}

	@Nonnull
	public static byte[] readByteArray(@Nonnull ByteBuf byteBuf, @Nonnegative int size) {
		byte[] data = new byte[size];
		byteBuf.readBytes(data);
		return data;
	}

	public static int readVarInt(@Nonnull ByteBuf buffer) {
		return (int) readVarVariant(buffer, 5);
	}

	@Nonnull
	public static ByteBuf writeVarInt(@Nonnull ByteBuf buffer, int value) {
		while (true) {
			if ((value & -128) == 0) {
				buffer.writeByte(value);
				return buffer;
			}

			buffer.writeByte(value & 0x7F | 0x80);
			value >>>= 7;
		}
	}

	public static long readVarLong(@Nonnull ByteBuf buffer) {
		return readVarVariant(buffer, 10);
	}

	public static ByteBuf writeVarLong(@Nonnull ByteBuf buffer, long value) {
		while (true) {
			if ((value & -128) == 0) {
				buffer.writeByte((int) value);
				return buffer;
			}

			buffer.writeByte((int) value & 0x7F | 0x80);
			value >>>= 7;
		}
	}

	private static long readVarVariant(@Nonnull ByteBuf byteBuf, int maxReadUpperBound) {
		long i = 0;
		int maxRead = Math.min(maxReadUpperBound, byteBuf.readableBytes());
		for (int j = 0; j < maxRead; j++) {
			int nextByte = byteBuf.readByte();
			i |= (long) (nextByte & 0x7F) << j * 7;
			if ((nextByte & 0x80) != 128) {
				return i;
			}
		}

		throw SilentDecoderException.INVALID_VAR_INT;
	}

	@Nonnull
	public static ByteBuf writeString(@Nonnull ByteBuf buffer, @Nonnull String string) {
		byte[] content = string.getBytes(StandardCharsets.UTF_8);
		writeVarInt(buffer, content.length);
		buffer.writeBytes(content);
		return buffer;
	}

	@Nonnull
	public static String readString(@Nonnull ByteBuf buffer) {
		int size = readVarInt(buffer);
		return new String(readByteArray(buffer, size), StandardCharsets.UTF_8);
	}

	public static int getThreadAmount() {
		return CloudDriver.getInstance().getEnvironment() == DriverEnvironment.WRAPPER ? 8 : Runtime.getRuntime().availableProcessors() * 2;
	}

	private NettyUtils() {}
}