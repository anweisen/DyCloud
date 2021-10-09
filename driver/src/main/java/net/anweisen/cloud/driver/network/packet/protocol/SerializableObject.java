package net.anweisen.cloud.driver.network.packet.protocol;

import javax.annotation.Nonnull;

/**
 * Serializable objects can be sent in packets using {@link PacketBuffer#writeObject(SerializableObject)} and {@link PacketBuffer#readObject(Class)}.
 * An implementation of SerializableObject must have an empty constructor (can be {@code private})
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface SerializableObject {

	void write(@Nonnull PacketBuffer buffer);

	void read(@Nonnull PacketBuffer buffer);

}
