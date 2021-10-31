package net.anweisen.cloud.modules.bridge.helper;

import net.anweisen.cloud.driver.config.global.objects.CommandObject;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface BridgeProxyMethods {

	void updateCommands(@Nonnull Map<String, Collection<CommandObject>> mapping);

	void registerServer(@Nonnull ServiceInfo service);

	void unregisterServer(@Nonnull String name);

	void checkPlayerDisconnect(@Nonnull CloudPlayer player);

}
