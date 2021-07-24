package net.anweisen.cloud.driver.network.exception;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ChunkInterruptException extends RuntimeException {

	public static final ChunkInterruptException INSTANCE = new ChunkInterruptException();

	private ChunkInterruptException() {}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
