package net.anweisen.cloud.master.cord;

import net.anweisen.cloud.driver.cord.CordInfo;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCordServer implements CordServer {

	private final CordInfo info;
	private final SocketChannel channel;

	public DefaultCordServer(@Nonnull CordInfo info, @Nonnull SocketChannel channel) {
		this.info = info;
		this.channel = channel;
	}

	@Nonnull
	@Override
	public CordInfo getInfo() {
		return info;
	}

	@Nonnull
	@Override
	public SocketChannel getChannel() {
		return channel;
	}

	@Override
	public String toString() {
		return "CordServer[name=" + info.getName() + " clientAddress=" + info.getClientAddress() + " proxyAddress=" + info.getProxyAddress() + "]";
	}
}
