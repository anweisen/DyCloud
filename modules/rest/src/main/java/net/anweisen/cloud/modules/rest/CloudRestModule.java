package net.anweisen.cloud.modules.rest;

import net.anweisen.cloud.base.module.CloudModule;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.cloud.modules.rest.auth.player.PlayerAuthCommand;
import net.anweisen.cloud.modules.rest.auth.player.PlayerAuthHandler;
import net.anweisen.cloud.modules.rest.v1.router.V1PingRouter;
import net.anweisen.cloud.modules.rest.v1.router.V1PlayerRouter;
import net.anweisen.cloud.modules.rest.v1.router.V1StatusRouter;
import net.anweisen.cloud.modules.rest.v1.router.V1UpgradeRouter;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudRestModule extends CloudModule {

	@Override
	protected void onLoad() {
		CloudMaster.getInstance().getHttpServer().getAuthRegistry().registerAuthMethodHandler("Player", new PlayerAuthHandler());
		CloudMaster.getInstance().getCommandManager().registerCommand(new PlayerAuthCommand());
		CloudMaster.getInstance().getHttpServer().getHandlerRegistry()
			.registerHandlers("v1", new V1PingRouter(), new V1StatusRouter(), new V1UpgradeRouter(), new V1PlayerRouter());
	}

}
