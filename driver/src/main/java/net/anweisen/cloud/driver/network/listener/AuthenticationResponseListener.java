package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class AuthenticationResponseListener implements PacketListener, LoggingApiUser {

	private final Lock lock;
	private final Condition condition;

	private boolean result;
	private String message;

	public AuthenticationResponseListener(@Nonnull Lock lock, @Nonnull Condition condition) {
		this.lock = lock;
		this.condition = condition;
	}

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		Document header = packet.getHeader();
		if (!header.contains("access")) return;

		trace("Received authentication response from master");

		result = header.getBoolean("access");
		message = header.getString("message", "");

		try {
			lock.lock();
			condition.signalAll();
		} finally {
			lock.unlock();
		}

	}

	public boolean getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}
}