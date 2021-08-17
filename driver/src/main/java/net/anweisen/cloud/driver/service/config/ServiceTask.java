package net.anweisen.cloud.driver.service.config;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.service.specific.ServiceEnvironment;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ServiceTask implements SerializableObject {

	private String name;

	private ServiceEnvironment environment;
	private int javaVersion;

	private boolean fallback;
	private int fallbackPriority;
	private String permission;

	private int maxMemory;

	private int minCount;
	private int maxCount;

	private Collection<String> nodes;
	private Collection<ServiceTemplate> templates;

	private ServiceTask() {
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(name);
		buffer.writeEnumConstant(environment);
		buffer.writeInt(javaVersion);
		buffer.writeBoolean(fallback);
		buffer.writeInt(fallbackPriority);
		buffer.writeOptionalString(permission);
		buffer.writeInt(maxMemory);
		buffer.writeInt(minCount);
		buffer.writeInt(maxCount);
		buffer.writeStringCollection(nodes);
		buffer.writeObjectCollection(templates);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		name = buffer.readString();
		environment = buffer.readEnumConstant(ServiceEnvironment.class);
		javaVersion = buffer.readInt();
		fallback = buffer.readBoolean();
		fallbackPriority = buffer.readInt();
		permission = buffer.readOptionalString();
		maxMemory = buffer.readInt();
		minCount = buffer.readInt();
		maxCount = buffer.readInt();
		nodes = buffer.readStringCollection();
		templates = buffer.readObjectCollection(ServiceTemplate.class);
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

	public boolean isFallback() {
		return fallback;
	}

	public int getFallbackPriority() {
		return fallbackPriority;
	}

	@Nullable
	public String getPermission() {
		return permission;
	}

	@Nonnegative
	public int getMaxCount() {
		return maxCount;
	}

	@Nonnegative
	public int getMinCount() {
		return minCount;
	}

	public int getJavaVersion() {
		return javaVersion;
	}

	public int getMaxMemory() {
		return maxMemory;
	}

	@Nonnull
	public Collection<ServiceInfo> findServices() {
		return CloudDriver.getInstance().getServiceManager().getServiceInfosByTask(name);
	}

	@Override
	public String toString() {
		return "ServiceTask[name=" + name + " environment=" + environment + " javaVersion=" + javaVersion + " memory=" + maxMemory + "MB "
			+ "fallback=" + fallback + (fallback ? "@" + fallbackPriority : "") + " minCount=" + minCount + " maxCount=" + maxCount + " nodes=" + nodes
			+ " templates=" + templates.stream().map(ServiceTemplate::toShortString).collect(Collectors.joining()) + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceTask task = (ServiceTask) o;
		return javaVersion == task.javaVersion
			&& fallback == task.fallback
			&& fallbackPriority == task.fallbackPriority
			&& maxMemory == task.maxMemory
			&& minCount == task.minCount
			&& maxCount == task.maxCount
			&& environment == task.environment
			&& Objects.equals(name, task.name)
			&& Objects.equals(permission, task.permission)
			&& Objects.equals(nodes, task.nodes)
			&& Objects.equals(templates, task.templates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, environment, javaVersion, fallback, fallbackPriority, permission, maxMemory, minCount, maxCount, nodes, templates);
	}
}
