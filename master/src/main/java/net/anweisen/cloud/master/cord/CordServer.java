package net.anweisen.cloud.master.cord;

import net.anweisen.cloud.driver.cord.CordInfo;
import net.anweisen.cloud.driver.network.SocketChannel;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface CordServer {

	@Nonnull
	CordInfo getInfo();

	@Nonnull
	SocketChannel getChannel();

}
