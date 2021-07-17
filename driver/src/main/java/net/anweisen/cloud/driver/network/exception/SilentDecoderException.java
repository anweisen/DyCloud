package net.anweisen.cloud.driver.network.exception;

import io.netty.handler.codec.DecoderException;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SilentDecoderException extends DecoderException {

	public SilentDecoderException(@Nonnull String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
