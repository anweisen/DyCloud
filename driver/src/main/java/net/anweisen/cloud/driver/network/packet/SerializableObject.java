package net.anweisen.cloud.driver.network.packet;

import net.anweisen.cloud.driver.network.packet.protocol.Buffer;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SerializableObject {

	void write(@Nonnull Buffer buffer);

	void read(@Nonnull Buffer buffer);

}
