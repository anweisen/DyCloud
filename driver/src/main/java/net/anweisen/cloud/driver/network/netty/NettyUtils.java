package net.anweisen.cloud.driver.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.network.exception.SilentDecoderException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public final class NettyUtils {

	private static final ThreadFactory THREAD_FACTORY = FastThreadLocalThread::new;
	private static final SilentDecoderException INVALID_VAR_INT = new SilentDecoderException("Invalid var int");
	private static final RejectedExecutionHandler DEFAULT_REJECT_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

	static {
		// use jdk logger to prevent issues with older slf4j versions
		// like them bundled in spigot 1.8
		InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
		// check if the leak detection level is set before overriding it
		// may be useful for debugging of the network
		if (System.getProperty("io.netty.leakDetection.level") == null) {
			ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
		}
	}

	private NettyUtils() {}

	// TODO kqueue and epoll
	@Nonnull
	@CheckReturnValue
	public static EventLoopGroup newEventLoopGroup() {
		return new NioEventLoopGroup(4, threadFactory());
	}

	@Nonnull
	@CheckReturnValue
	public static Executor newPacketDispatcher() {
		// a cached pool with a thread idle-lifetime of 30 seconds
		// rejected tasks will be executed on the calling thread (See ThreadPoolExecutor.CallerRunsPolicy)
		return new ThreadPoolExecutor(0, getThreadAmount(),
			30L, TimeUnit.SECONDS, new SynchronousQueue<>(true), DEFAULT_REJECT_HANDLER);
	}

	// TODO kqueue and epoll
	@Nonnull
	@CheckReturnValue
	public static ChannelFactory<? extends Channel> getClientChannelFactory() {
//		return Epoll.isAvailable() ? EpollSocketChannel::new
//			: KQueue.isAvailable() ? KQueueSocketChannel::new : NioSocketChannel::new;
	  return NioSocketChannel::new;
	}

	// TODO kqueue and epoll
	@Nonnull
	@CheckReturnValue
	public static ChannelFactory<? extends ServerChannel> getServerChannelFactory() {
//		return Epoll.isAvailable() ? EpollServerSocketChannel::new
//			: KQueue.isAvailable() ? KQueueServerSocketChannel::new : NioServerSocketChannel::new;
	  return NioServerSocketChannel::new;
	}

	@Nonnull
	@CheckReturnValue
	public static ThreadFactory threadFactory() {
		return THREAD_FACTORY;
	}

	@Nonnull
	public static byte[] readByteArray(@Nonnull ByteBuf byteBuf, @Nonnegative int size) {
		byte[] data = new byte[size];
		byteBuf.readBytes(data);
		return data;
	}

	public static int readVarInt(@Nonnull ByteBuf byteBuf) {
		return (int) readVarVariant(byteBuf, 5);
	}

	@Nonnull
	public static ByteBuf writeVarInt(@Nonnull ByteBuf byteBuf, int value) {
		while (true) {
			if ((value & -128) == 0) {
				byteBuf.writeByte(value);
				return byteBuf;
			}

			byteBuf.writeByte(value & 0x7F | 0x80);
			value >>>= 7;
		}
	}

	public static long readVarLong(ByteBuf byteBuf) {
		return readVarVariant(byteBuf, 10);
	}

	public static ByteBuf writeVarLong(@Nonnull ByteBuf byteBuf, long value) {
		while (true) {
			if ((value & -128) == 0) {
				byteBuf.writeByte((int) value);
				return byteBuf;
			}

			byteBuf.writeByte((int) value & 0x7F | 0x80);
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
		throw INVALID_VAR_INT;
	}

	@Nonnull
	public static ByteBuf writeString(@Nonnull ByteBuf byteBuf, @Nonnull String string) {
		byte[] content = string.getBytes(StandardCharsets.UTF_8);
		writeVarInt(byteBuf, content.length);
		byteBuf.writeBytes(content);
		return byteBuf;
	}

	@Nonnull
	public static String readString(@Nonnull ByteBuf byteBuf) {
		int size = readVarInt(byteBuf);
		return new String(readByteArray(byteBuf, size), StandardCharsets.UTF_8);
	}

	public static int getThreadAmount() {
		return CloudDriver.getInstance().getEnvironment() == DriverEnvironment.WRAPPER ? 8 : Runtime.getRuntime().availableProcessors() * 2;
	}
}