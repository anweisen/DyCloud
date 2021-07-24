package net.anweisen.cloud.base.network.request;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.request.RequestType;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class RequestPacketListener implements PacketListener {

	protected final Map<RequestType, RequestHandler> handlers = new LinkedHashMap<>();

	public RequestPacketListener(@Nonnull CategorizedRequestHandler... handlers) {
		for (CategorizedRequestHandler handler : handlers)
			registerHandlers(handler);
	}

	public void registerHandler(@Nonnull RequestType type, @Nonnull RequestHandler handler) {
		handlers.put(type, handler);
	}

	public void registerHandlers(@Nonnull CategorizedRequestHandler handler) {
		handler.register();
		handlers.putAll(handler.getHandlers());
	}

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {

		Buffer buffer = packet.getBuffer();

		RequestType request = buffer.readEnumConstant(RequestType.class);
		RequestHandler handler = handlers.get(request);
		Preconditions.checkNotNull(handler, "No handler registered for RequestType." + request);

		Buffer response = handler.handle(channel, packet, buffer);
		if (response != null)
			channel.sendPacket(Packet.createResponseFor(packet, response));

	}

}
