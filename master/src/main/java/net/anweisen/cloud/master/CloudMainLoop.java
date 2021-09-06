package net.anweisen.cloud.master;

import net.anweisen.cloud.base.node.NodeCycleData;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.specific.ServiceControlState;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceState;
import net.anweisen.cloud.master.node.NodeServer;
import net.anweisen.cloud.master.service.specific.CloudService;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class CloudMainLoop implements Runnable, LoggingApiUser {

	private final CloudMaster cloud;

	public CloudMainLoop(@Nonnull CloudMaster cloud) {
		this.cloud = cloud;
	}

	@Override
	public synchronized void run() {
		trace("Running cloud main loop iteration");

		checkNodeStatus();
		startRequiredServices();
		checkServiceTimeout();
	}

	private void startRequiredServices() {
		trace("Checking to start required services..");

		List<ServiceTask> tasks = new ArrayList<>(cloud.getServiceConfigManager().getTasks());
		tasks.sort(Comparator.comparingInt(ServiceTask::getStartOrder));
		for (ServiceTask task : tasks) {
			Collection<CloudService> services = cloud.getServiceManager().getServicesByTask(task.getName());
			int createdServiceCount = countWithState(services, Arrays.asList(ServiceState.DEFINED, ServiceState.PREPARED, ServiceState.RUNNING));

			if (createdServiceCount < task.getMinCount()) {
				cloud.getServiceFactory().createServiceAsync(task);
				return;
			}
			for (CloudService service : services) {
				ServiceInfo serviceInfo = service.getInfo();
				if (serviceInfo.getState() == ServiceState.PREPARED && serviceInfo.getControlState() == ServiceControlState.NONE) {
					serviceInfo.getController().start();
					return;
				}
			}
		}
	}

	private void checkServiceTimeout() {
		for (CloudService service : cloud.getServiceManager().getServices()) {
			ServiceInfo serviceInfo = service.getInfo();
			if (!hasTimeouted(serviceInfo)) continue;
			warn("{} has not sent the required info updates", serviceInfo);

			if (serviceInfo.getState() == ServiceState.RUNNING && serviceInfo.getControlState() == ServiceControlState.NONE) {
				// TODO permanent services dont want to get deleted
				warn("=> Probably crashed -> Deleting..");
				serviceInfo.getController().kill();
				serviceInfo.getController().delete();
				return;
			}
			if (serviceInfo.getControlState() == ServiceControlState.STARTING || serviceInfo.getControlState() == ServiceControlState.CREATING) {
				warn("=> Probably crashed during startup or could not be started -> Deleting..");
				serviceInfo.getController().kill();
				serviceInfo.getController().delete();
				return;
			}
		}
	}

	private void checkNodeStatus() {
		trace("Checking node status & timeout..");
		for (NodeServer node : cloud.getNodeManager().getNodeServers()) {
			NodeCycleData cycleData = node.getLastCycleData();
			if (cycleData == null) continue;

			if (cycleData.hasTimeouted()) {
				warn("{} has timeouted!", node);
			}
			if (cycleData.getLatency() > 99) {
				warn("{} has a high ping: {}ms", node, cycleData.getLatency());
			}
		}
	}

	private boolean hasTimeouted(@Nonnull ServiceInfo serviceInfo) {
		long lastCycleDelay = System.currentTimeMillis() - serviceInfo.getTimestamp() - 30;
		int lostCycles = (int) lastCycleDelay / ServiceInfo.PUBLISH_INTERVAL;
		if (lostCycles > 0) trace("Service timeout: lost {} cycles ({}ms)", lostCycles, lastCycleDelay);
		return lostCycles >= ServiceInfo.CYCLE_TIMEOUT;
	}

	private int countWithState(@Nonnull Collection<CloudService> services, @Nonnull Collection<ServiceState> states) {
		int count = 0;
		for (CloudService service : services) {
			if (states.contains(service.getInfo().getState()))
				count++;
		}
		return count;
	}

}
