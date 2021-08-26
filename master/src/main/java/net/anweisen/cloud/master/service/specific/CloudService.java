package net.anweisen.cloud.master.service.specific;

import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CloudService {

	@Nonnull
	ServiceInfo getInfo();

	void setInfo(@Nonnull ServiceInfo info);

	@Nullable
	SocketChannel getChannel();

	void setChannel(@Nonnull SocketChannel channel);

}
