package net.anweisen.cloud.driver.network.netty.http;

import net.anweisen.cloud.driver.network.http.HttpChannel;
import net.anweisen.cloud.driver.network.object.HostAndPort;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class NettyHttpChannel implements HttpChannel {

	protected final HostAndPort serverAddress, clientAddress;

	public NettyHttpChannel(@Nonnull HostAndPort serverAddress, @Nonnull HostAndPort clientAddress) {
		this.serverAddress = serverAddress;
		this.clientAddress = clientAddress;
	}

	@Nonnull
	@Override
	public HostAndPort getServerAddress() {
		return serverAddress;
	}

	@Nonnull
	@Override
	public HostAndPort getClientAddress() {
		return clientAddress;
	}

	@Override
	public String toString() {
		return "HttpChannel[client=" + clientAddress + " server=" + serverAddress + "]";
	}
}
