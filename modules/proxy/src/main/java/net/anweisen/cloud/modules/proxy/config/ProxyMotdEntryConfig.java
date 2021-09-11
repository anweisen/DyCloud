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
	private String protocolText;
	private List<String> playerInfo;

	private ProxyMotdEntryConfig() {
	}

	public ProxyMotdEntryConfig(@Nonnull String firstLine, @Nonnull String secondLine, @Nullable String protocolText, @Nonnull List<String> playerInfo) {
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.protocolText = protocolText;
		this.playerInfo = playerInfo;
	}

	@Nonnull
	public String getFirstLine() {
		return firstLine;
	}

	@Nonnull
	public String getSecondLine() {
		return secondLine;
	}

	@Nullable
	public String getProtocolText() {
		return protocolText;
	}

	public List<String> getPlayerInfo() {
		return playerInfo;
	}
}
