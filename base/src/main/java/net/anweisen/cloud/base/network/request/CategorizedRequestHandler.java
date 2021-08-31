package net.anweisen.cloud.base.network.request;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.request.RequestResponseType;
import net.anweisen.cloud.driver.network.request.RequestType;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.function.ExceptionallyFunction;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public abstract class CategorizedRequestHandler implements LoggingApiUser {

	private final Map<RequestType, RequestHandler> handlers = new HashMap<>();

	public abstract void register();

	protected final void registerHandler(@Nonnull RequestType type, @Nonnull RequestHandler handler)  {
		handlers.put(type, handler);
	}

	@Nonnull
	protected RequestHandler handler(@Nonnull ExceptionallyFunction<Buffer, Buffer> action) {
		return (channel, packet, buffer) -> {
			try {
				return action.apply(buffer);
			} catch (ResponseException ex) {
				return Buffer.create().writeEnumConstant(ex.getResponse());
			} catch (Throwable ex) {
				error("An error occurred while handling network api request: {}", packet, ex);
				return Buffer.create().writeEnumConstant(RequestResponseType.EXCEPTION).writeThrowable(ex);
			}
		};
	}

	@Nonnull
	protected RequestHandler chunkedHandler(@Nonnull ExceptionallyFunction<Buffer, InputStream> function) {
		return (channel, packet, input) -> {
			RequestResponseType response = RequestResponseType.SUCCESS;
			InputStream inputStream = FileUtils.EMPTY_STREAM;

			try {
				inputStream = function.apply(input);
				if (inputStream == null) {
					response = RequestResponseType.EXCEPTION; // TODO something else (resource probably not found)
					inputStream = FileUtils.EMPTY_STREAM;
				}

			} catch (ResponseException ex) {
				response = ex.getResponse();
			} catch (Exception ex) {
				ex.printStackTrace();
				response = RequestResponseType.EXCEPTION;
				inputStream = new ByteArrayInputStream(Buffer.create().writeThrowable(ex).toArray());
			}

			try {
				channel.sendChunkedPacketsResponse(packet.getUniqueId(), Document.create().set("response", response), inputStream);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return null;
		};
	}

	@Nonnull
	public final Map<RequestType, RequestHandler> getHandlers() {
		return handlers;
	}
}
