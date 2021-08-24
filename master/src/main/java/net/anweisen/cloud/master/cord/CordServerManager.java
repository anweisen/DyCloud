package net.anweisen.cloud.master.cord;

import net.anweisen.cloud.driver.cord.CordManager;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CordServerManager extends CordManager {

	@Nonnull
	Collection<CordServer> getCordServers();

	@Nullable
	CordServer getCordServer(@Nonnull String name);

	@Nullable
	CordServer getCordServer(@Nonnull SocketChannel channel);

}
