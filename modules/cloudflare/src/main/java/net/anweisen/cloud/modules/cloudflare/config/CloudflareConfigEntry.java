package net.anweisen.cloud.modules.cloudflare.config;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class CloudflareConfigEntry {

	public enum AuthenticationMethod {
		GLOBAL_KEY,
		BEARER_TOKEN
	}

	private boolean enabled;

	private AuthenticationMethod authenticationMethod = AuthenticationMethod.GLOBAL_KEY;

	private String email;
	private String apiToken;
	private String zoneId;
	private String domainName;

	protected Collection<CloudflareConfigEntryGroup> groups;

	private CloudflareConfigEntry() {
	}

	public CloudflareConfigEntry(boolean enabled, @Nonnull String email, @Nonnull String apiToken, @Nonnull String zoneId,
	                             @Nonnull String domainName, @Nonnull Collection<CloudflareConfigEntryGroup> groups) {
		this.enabled = enabled;
		this.email = email;
		this.apiToken = apiToken;
		this.zoneId = zoneId;
		this.domainName = domainName;
		this.groups = groups;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nonnull
	public AuthenticationMethod getAuthenticationMethod() {
		return authenticationMethod;
	}

	@Nonnull
	public String getEmail() {
		return email;
	}

	@Nonnull
	public String getApiToken() {
		return apiToken;
	}

	@Nonnull
	public String getZoneId() {
		return zoneId;
	}

	@Nonnull
	public String getDomainName() {
		return domainName;
	}

	@Nonnull
	public Collection<CloudflareConfigEntryGroup> getGroups() {
		return groups;
	}

	@Override
	public String toString() {
		return "CloudflareConfigEntry[enabled=" + enabled + " auth=" + authenticationMethod + " domain=" + domainName + " groups=" + groups + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CloudflareConfigEntry that = (CloudflareConfigEntry) o;
		return enabled == that.enabled
			&& authenticationMethod == that.authenticationMethod
			&& Objects.equals(email, that.email)
			&& Objects.equals(apiToken, that.apiToken)
			&& Objects.equals(zoneId, that.zoneId)
			&& Objects.equals(domainName, that.domainName)
			&& Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(enabled, authenticationMethod, email, apiToken, zoneId, domainName, groups);
	}
}
