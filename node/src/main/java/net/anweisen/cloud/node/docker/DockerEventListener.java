package net.anweisen.cloud.node.docker;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Event;
import net.anweisen.cloud.driver.network.packet.def.ServiceInfoPublishPacket.ServicePublishType;
import net.anweisen.cloud.driver.service.specific.ServiceControlState;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceState;
import net.anweisen.cloud.node.CloudNode;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DockerEventListener extends ResultCallbackTemplate<DockerEventListener, Event> {

	@Override
	public void onNext(Event event) {
		CloudNode cloud = CloudNode.getInstance();

		String containerId = event.getId();
		ServiceInfo service = cloud.getServiceManager().getServiceInfoByDockerId(containerId);
		cloud.getLogger().trace("Detected {} of {}", event, service);
		if (service == null) return;
		switch (event.getStatus()) {
			case "die": {
				cloud.getLogger().info("Docker Container of {} died!", service);
				if (service.getState() != ServiceState.STOPPED && service.getState() != ServiceState.DELETED) {
					service.setState(ServiceState.STOPPED);
					service.setControlState(ServiceControlState.NONE);
					cloud.publishUpdate(ServicePublishType.STOPPED, service);
				}
				break;
			}
		}
	}
}
