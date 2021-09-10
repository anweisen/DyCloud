package net.anweisen.cloud.modules.proxy.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ProxyMotdEntryConfig {

	private String firstLine;
	private String secondLine;
	private List<String> playerInfo;
	private String protocolText;

	private ProxyMotdEntryConfig() {
	}

	public ProxyMotdEntryConfig(@Nonnull String firstLine, @Nonnull String secondLine, @Nonnull List<String> playerInfo, @Nullable String protocolText) {
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.playerInfo = playerInfo;
		this.protocolText = protocolText;
	}

	@Nonnull
	public String getFirstLine() {
		return firstLine;
	}

	@Nonnull
	public String getSecondLine() {
		return secondLine;
	}

	public List<String> getPlayerInfo() {
		return playerInfo;
	}

	@Nullable
	public String getProtocolText() {
		return protocolText;
	}
}
