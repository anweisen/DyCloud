package net.anweisen.cloud.driver.network.packet.chunk.listener;

import com.google.common.base.Preconditions;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CachedChunkedPacketListener extends ChunkedPacketListener {

	@Nonnull
	@Override
	protected OutputStream createOutputStream(@Nonnull UUID sessionUniqueId, @Nonnull Map<String, Object> properties) throws IOException {
		Path path = FileUtils.getTempDirectory().resolve(sessionUniqueId.toString());
		Files.createDirectories(path.getParent());

		properties.put("path", path);
		return Files.newOutputStream(path);
	}

	@Override
	protected void handleComplete(@Nonnull ChunkedPacketSession session) throws IOException {
		Path path = (Path) session.getProperties().get("path");
		Preconditions.checkArgument(Files.exists(path), "Path of the cache doesn't exist");

		this.handleComplete(session, Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE));
	}

	protected abstract void handleComplete(@Nonnull ChunkedPacketSession session, @Nonnull InputStream inputStream) throws IOException;

}
