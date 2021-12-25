package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyMotdConfig {

	private List<ProxyMotdEntryConfig> motds;
	private List<ProxyMotdEntryConfig> maintenanceMotds;

	private ProxyMotdConfig() {
	}

	public ProxyMotdConfig(@Nonnull List<ProxyMotdEntryConfig> motds, @Nonnull List<ProxyMotdEntryConfig> maintenanceMotds) {
		this.motds = motds;
		this.maintenanceMotds = maintenanceMotds;
	}

	@Nonnull
	public List<ProxyMotdEntryConfig> getMotds() {
		return motds;
	}

	@Nonnull
	public List<ProxyMotdEntryConfig> getMaintenanceMotds() {
		return maintenanceMotds;
	}

	@Override
	public String toString() {
		return "ProxyMotdConfig[" +
			"motds=" + motds.size() +
			" maintenanceMotds=" + maintenanceMotds.size() +
			']';
	}
}
