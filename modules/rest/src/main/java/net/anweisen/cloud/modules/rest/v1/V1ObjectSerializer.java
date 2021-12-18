package net.anweisen.cloud.modules.rest.v1;

import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.connection.PlayerConnection;
import net.anweisen.cloud.driver.player.settings.PlayerSettings;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class V1ObjectSerializer {

	public static Document forOnlinePlayer(@Nonnull CloudPlayer player) {
		return Documents.newJsonDocument(
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
		return Documents.newJsonDocument(
			"online", connection.getOnlineMode(),
			"legacy", connection.getLegacy(),
			"version", Documents.newJsonDocument(
				"name", connection.getVersion().getName(),
				"id", connection.getRawVersion()
			)
		).applyIf(includeProxy, data -> data.set("proxy", connection.getProxyName()))
		.applyIf(includeAddress, data -> data.set("address", connection.getAddress()));
	}

	public static Document forPlayerSettings(@Nonnull PlayerSettings settings, boolean includeSkinParts) {
		return Documents.newJsonDocument(
			"locale", settings.getLocale(),
			"chatColors", settings.hasChatColors(),
			"chatMode", settings.getChatMode(),
			"mainHand", settings.getMainHand(),
			"renderDistance", settings.getRenderDistance()
		).applyIf(includeSkinParts, data -> data.set("skin", settings.getSkinParts()));
	}

	private V1ObjectSerializer() {}
}
