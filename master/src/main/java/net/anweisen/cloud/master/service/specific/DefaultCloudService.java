package net.anweisen.cloud.master.service.specific;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultCloudService implements CloudService {

	private ServiceInfo info;
	private SocketChannel channel;

	public DefaultCloudService(@Nonnull ServiceInfo info) {
		this.info = info;
	}

	@Nonnull
	@Override
	public ServiceInfo getInfo() {
		return info;
	}

	@Override
	public void setInfo(@Nonnull ServiceInfo info) {
		Preconditions.checkNotNull(info);
		this.info = info;
	}

	@Nullable
	@Override
	public SocketChannel getChannel() {
		return channel;
	}

	@Override
	public void setChannel(@Nonnull SocketChannel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return info.toString();
	}
}
