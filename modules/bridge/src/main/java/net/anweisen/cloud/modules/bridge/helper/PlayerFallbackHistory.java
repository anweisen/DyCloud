package net.anweisen.cloud.modules.bridge.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PlayerFallbackHistory {

	private final Collection<String> failedConnections = new ArrayList<>();
	private String pendingConnectionAttempt;

	@Nonnull
	public Collection<String> getFailedConnections() {
		return failedConnections;
	}

	@Nullable
	public String getPendingConnectionAttempt() {
		return pendingConnectionAttempt;
	}

	public void setPendingConnectionAttempt(@Nonnull String pendingConnectionAttempt) {
		this.pendingConnectionAttempt = pendingConnectionAttempt;
	}

	public void addFailedConnection(@Nonnull String serverName) {
		failedConnections.add(serverName);
	}

	@Override
	public String toString() {
		return "FallbackHistory[current=" + pendingConnectionAttempt + " tried=" + failedConnections + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerFallbackHistory that = (PlayerFallbackHistory) o;
		return Objects.equals(failedConnections, that.failedConnections) && Objects.equals(pendingConnectionAttempt, that.pendingConnectionAttempt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(failedConnections, pendingConnectionAttempt);
	}
}