package net.anweisen.cloud.modules.cloudflare.config;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudflareConfigEntryGroup {

	private String taskName;
	private String subdomainName;
	private int priority;
	private int weight;

	private CloudflareConfigEntryGroup() {
	}

	public CloudflareConfigEntryGroup(@Nonnull String taskName, @Nonnull String subdomainName, int priority, int weight) {
		this.taskName = taskName;
		this.subdomainName = subdomainName;
		this.priority = priority;
		this.weight = weight;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getSubDomainName() {
		return subdomainName;
	}

	public int getPriority() {
		return priority;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "CloudflareConfigEntryGroup[task=" + taskName + " subdomain=" + subdomainName + " priority=" + priority + " weight=" + weight + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CloudflareConfigEntryGroup that = (CloudflareConfigEntryGroup) o;
		return priority == that.priority
			&& weight == that.weight
			&& Objects.equals(taskName, that.taskName)
			&& Objects.equals(subdomainName, that.subdomainName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(taskName, subdomainName, priority, weight);
	}
}
