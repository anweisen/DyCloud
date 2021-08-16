package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.specific.ServiceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerApiPacket extends Packet {

	public PlayerApiPacket(@Nonnull PlayerActionType action, @Nullable Consumer<? super Buffer> modifier) {
		super(PacketConstants.PLAYER_API_CHANNEL, Buffer.create().writeEnumConstant(action));
		if (modifier != null)
			modifier.accept(body);
	}

	/**
	 * Login Order:
	 * 1. Proxy Login Request
	 * 2. Proxy Login Success
	 * 3. Proxy Server Connect Request
	 * 4. Server Login Request
	 * 5. Proxy Server Switch
	 * 6. Server Login Success
	 */
	public enum PlayerActionType {

		PROXY_LOGIN_REQUEST             (ServiceType.PROXY),
		PROXY_LOGIN_SUCCESS             (ServiceType.PROXY),
		PROXY_SERVER_CONNECT_REQUEST    (ServiceType.PROXY),
		PROXY_SERVER_SWITCH             (ServiceType.PROXY),
		PROXY_DISCONNECT                (ServiceType.PROXY),

		SERVER_LOGIN_REQUEST            (ServiceType.SERVER),
		SERVER_LOGIN_SUCCESS            (ServiceType.SERVER),
		SERVER_DISCONNECT               (ServiceType.SERVER);

		private final ServiceType type;

		PlayerActionType(@Nonnull ServiceType type) {
			this.type = type;
		}

		@Nonnull
		public ServiceType getType() {
			return type;
		}
	}

}
