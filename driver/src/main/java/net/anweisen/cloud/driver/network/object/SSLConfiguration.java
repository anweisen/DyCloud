package net.anweisen.cloud.driver.network.object;

import javax.annotation.Nullable;
import java.nio.file.Path;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SSLConfiguration {

	private final boolean clientAuth;

	private final Path certificatePath;
	private final Path privateKeyPath;

	public SSLConfiguration(boolean clientAuth, @Nullable Path certificatePath, @Nullable Path privateKeyPath) {
		this.clientAuth = clientAuth;
		this.certificatePath = certificatePath;
		this.privateKeyPath = privateKeyPath;
	}

	public boolean getClientAuth() {
		return clientAuth;
	}

	@Nullable
	public Path getCertificatePath() {
		return certificatePath;
	}

	@Nullable
	public Path getPrivateKeyPath() {
		return privateKeyPath;
	}
}
