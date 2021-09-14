package net.anweisen.cloud.modules.bridge.helper.general;

import net.anweisen.cloud.driver.event.EventListener;
import net.anweisen.cloud.wrapper.event.service.ServiceInfoConfigureEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class BridgeCloudListener {

	@EventListener
	public void onInfoConfigure(@Nonnull ServiceInfoConfigureEvent event) {
		event.getServiceInfo().setReady();
	}

}
