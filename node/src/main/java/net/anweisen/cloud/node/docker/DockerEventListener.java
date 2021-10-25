package net.anweisen.cloud.node.docker;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Event;
import net.anweisen.cloud.driver.network.packet.def.ServicePublishPacket.ServicePublishPayload;
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
				if (service.getState() != ServiceState.STOPPED && service.getState() != ServiceState.DELETED && service.getControlState() != ServiceControlState.STOPPING) {
					cloud.getLogger().info("Docker Container of {} died!", service);
					service.setState(ServiceState.STOPPED);
					service.setControlState(ServiceControlState.NONE);
					cloud.publishUpdate(ServicePublishPayload.STOPPED, service);
				}
				break;
			}
			case "destroy": {
				if (service.getState() != ServiceState.DELETED && service.getControlState() != ServiceControlState.DELETING) {
					cloud.getLogger().info("Docker container of {} was destroyed!", service);
					service.setState(ServiceState.DELETED);
					service.setControlState(ServiceControlState.NONE);
					cloud.publishUpdate(ServicePublishPayload.UNREGISTER, service);
					cloud.getServiceManager().handleServiceUpdate(ServicePublishPayload.UNREGISTER, service);
				}

				break;
			}
		}
	}
}
