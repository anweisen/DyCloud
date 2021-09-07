package net.anweisen.cloud.driver.network.exception;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class SilentDecoderException extends RuntimeException {

	public static SilentDecoderException forInvalidVarInt() {
		return new SilentDecoderException("Invalid var int");
	}

	public static SilentDecoderException forBadPacketLength() {
		return new SilentDecoderException("Bad packet length");
	}

	private SilentDecoderException(@Nonnull String message) {
		super(message);
	}

}
