package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.service.specific.ServiceEnvironment;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ServiceTask implements SerializableObject {

	private String name;

	private String javaVersion;

	private Collection<String> nodes;
	private Collection<ServiceTemplate> templates;

	private ServiceEnvironment environment;

	private int minCount;
	private int maxCount;

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(name);
		buffer.writeString(javaVersion);
		buffer.writeStringCollection(nodes);
		buffer.writeObjectCollection(templates);
		buffer.writeEnumConstant(environment);
		buffer.writeInt(minCount);
		buffer.writeInt(maxCount);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		name = buffer.readString();
		javaVersion = buffer.readString();
		nodes = buffer.readStringCollection();
		templates = buffer.readObjectCollection(ServiceTemplate.class);
		environment = buffer.readEnumConstant(ServiceEnvironment.class);
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

	@Nonnull
	public ServiceEnvironment getEnvironment() {
		return environment;
	}

	@Nonnegative
	public int getMaxCount() {
		return maxCount;
	}

	@Nonnegative
	public int getMinCount() {
		return minCount;
	}

	@Nonnull
	public Collection<ServiceInfo> findServices() {
		return CloudDriver.getInstance().getServiceManager().getServiceInfosByTask(name);
	}

	@Override
	public String toString() {
		return "ServiceTask[name=" + name + " environment=" + environment + " javaVersion=" + javaVersion + " minCount=" + minCount + " maxCount=" + maxCount + " nodes=" + nodes + " templates=" + templates + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceTask that = (ServiceTask) o;
		return minCount == that.minCount && maxCount == that.maxCount && Objects.equals(name, that.name) && Objects.equals(nodes, that.nodes) && Objects.equals(templates, that.templates) && environment == that.environment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, nodes, templates, environment, minCount, maxCount);
	}
}
