package net.anweisen.cloud.driver.console;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class SpacePadder {

	private SpacePadder() {}

	/** @see #pad(StringBuilder, int) */
	private final static String[] fastSpaces = { " ", "  ", "    ", "        ", "                ", "                                " };

	public static void padLeft(@Nonnull StringBuilder buffer, @Nullable String content, int desiredLength) {
		int actualLength = 0;
		if (content != null) {
			actualLength = content.length();
		}
		if (actualLength < desiredLength) {
			pad(buffer, desiredLength - actualLength);
		}
		if (content != null) {
			buffer.append(content);
		}
	}

	public static void padRight(@Nonnull StringBuilder buffer, @Nullable String content, int desiredLength) {
		int actualLength = 0;
		if (content != null) {
			actualLength = content.length();
		}
		if (content != null) {
			buffer.append(content);
		}
		if (actualLength < desiredLength) {
			pad(buffer, desiredLength - actualLength);
		}
	}

	/**
	 * Fast space padding method.
	 */
	public static void pad(@Nonnull StringBuilder buffer, int length) {
		while (length >= 32) {
			buffer.append(fastSpaces[5]);
			length -= 32;
		}

		for (int i = 4; i >= 0; i--) {
			if ((length & (1 << i)) != 0) {
				buffer.append(fastSpaces[i]);
			}
		}
	}

}
