package net.anweisen.cloud.modules.perms.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.proxy.Player;
import net.anweisen.cloud.modules.perms.velocity.VelocityCloudPermsHelper;
import net.anweisen.cloud.modules.perms.velocity.VelocityCloudPermsProvider;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class VelocityCloudPermsListener {

	@Subscribe(order = PostOrder.FIRST)
	public void onLogin(@Nonnull LoginEvent event) {
		if (!event.getResult().isAllowed()) return;
		VelocityCloudPermsHelper.injectFunction(event.getPlayer());
	}

	@Subscribe
	public void onSetup(@Nonnull PermissionsSetupEvent event) {
		if (!(event.getSubject() instanceof Player)) return;
		event.setProvider(VelocityCloudPermsProvider.INSTANCE);
	}

}
