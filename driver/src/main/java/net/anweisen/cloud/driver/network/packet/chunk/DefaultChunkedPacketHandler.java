package net.anweisen.cloud.driver.network.packet.chunk;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.exception.ChunkInterruptException;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultChunkedPacketHandler {

	public static Consumer<ChunkedPacket> createHandler(@Nonnull Collection<SocketChannel> channels) {
		return packet -> {
			for (SocketChannel channel : channels) {
				if (!channel.isActive()) {
					if (noneActive(channels))
						throw ChunkInterruptException.INSTANCE;
					continue;
				}

				if (!waitWritable(channel))
					continue;

				channel.sendPacketSync(packet.fillBuffer());
			}

			packet.clearData();
		};
	}

	private static boolean noneActive(@Nonnull Collection<SocketChannel> channels) {
		for (SocketChannel channel : channels) {
			if (channel.isActive())
				return false;
		}
		return true;
	}

	private static boolean waitWritable(@Nonnull SocketChannel channel) {
		while (!channel.isWritable()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				throw ChunkInterruptException.INSTANCE;
			}

			if (!channel.isActive())
				return false;
		}

		return true;
	}

}
