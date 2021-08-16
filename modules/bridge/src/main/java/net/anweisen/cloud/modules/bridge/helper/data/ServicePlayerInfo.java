package net.anweisen.cloud.modules.bridge.helper.data;

import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ServicePlayerInfo {

	private final Document rawData;

	public ServicePlayerInfo(@Nonnull Document rawData) {
		this.rawData = rawData;
	}

	@Nonnull
	public Document getRawData() {
		return rawData;
	}

	@Nonnull
	public UUID getUniqueId() {
		return rawData.getUUID("uuid");
	}

	@Nonnull
	public String getName() {
		return rawData.getString("name");
	}

	@Nonnull
	public ProxyPlayerInfo asProxy() {
		return rawData.toInstanceOf(ProxyPlayerInfo.class);
	}

	@Nonnull
	public MinecraftPlayerInfo asMinecraft() {
		return rawData.toInstanceOf(MinecraftPlayerInfo.class);
	}

}
