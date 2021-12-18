package net.anweisen.cloud.modules.rest.v1;

import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class V1ObjectSerializer {

	public static Document forOnlinePlayer(@Nonnull CloudPlayer player) {
		return Document.of(
			"uniqueId", player.getUniqueId(),
			"name", player.getName(),
			"server", player.getServerOptional().map(ServiceInfo::getName).orElse(null),
			"proxy", player.getProxy().getName(),
			"language", player.getLanguage(),
			"connection", forPlayerConnection(player.getConnection(), false, true),
			"settings", forPlayerSettings(player.getSettings(), true)
		);
	}

	public static Document forPlayerConnection(@Nonnull PlayerConnection connection, boolean includeProxy, boolean includeAddress) {
		return Document.of(
			"online", connection.getOnlineMode(),
			"legacy", connection.getLegacy(),
			"version", Document.of(
				"name", connection.getVersion().getName(),
				"id", connection.getRawVersion()
			)
		).<Document>applyIf(includeProxy, data -> data.set("proxy", connection.getProxyName()))
		.<Document>applyIf(includeAddress, data -> data.set("address", connection.getAddress()));
	}

	public static Document forPlayerSettings(@Nonnull PlayerSettings settings, boolean includeSkinParts) {
		return Document.of(
			"locale", settings.getLocale(),
			"chatColors", settings.hasChatColors(),
			"chatMode", settings.getChatMode(),
			"mainHand", settings.getMainHand(),
			"renderDistance", settings.getRenderDistance()
		).<Document>applyIf(includeSkinParts, data -> data.set("skin", settings.getSkinParts()));
	}

	private V1ObjectSerializer() {}
}
