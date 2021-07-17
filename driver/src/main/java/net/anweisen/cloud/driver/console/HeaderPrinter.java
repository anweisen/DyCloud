package net.anweisen.cloud.driver.console;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class HeaderPrinter {

	private HeaderPrinter() {}

	public static void printHeader(@Nonnull Console console) {
		InputStream stream = HeaderPrinter.class.getClassLoader().getResourceAsStream("header.txt");
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream), StandardCharsets.UTF_8))) {
			String input;
			while ((input = bufferedReader.readLine()) != null) {
				console.writeLine(input);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
