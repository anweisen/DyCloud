package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.service.specific.CloudServiceInfo;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceTask implements SerializableObject {

	private String name;

	private Collection<String> nodes;
	private Collection<ServiceTemplate> templates;

	private int minCount;
	private int maxCount;

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(name);
		buffer.writeStringCollection(nodes);
		buffer.writeObjectCollection(templates);
		buffer.writeInt(minCount);
		buffer.writeInt(maxCount);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		name = buffer.readString();
		nodes = buffer.readStringCollection();
		templates = buffer.readObjectCollection(ServiceTemplate.class);
		minCount = buffer.readInt();
		maxCount = buffer.readInt();
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public Collection<String> getNodes() {
		return nodes;
	}

	@Nonnull
	public Collection<ServiceTemplate> getTemplates() {
		return templates;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public int getMinCount() {
		return minCount;
	}

	@Nonnull
	public Collection<CloudServiceInfo> findServices() {
		return CloudDriver.getInstance().getServiceManager().getServiceInfos(this);
	}

	@Override
	public String toString() {
		return "ServiceTask[name=" + name + " minCount=" + minCount + " maxCount=" + maxCount + " nodes=" + nodes + " templates=" + templates + "]";
	}

}
