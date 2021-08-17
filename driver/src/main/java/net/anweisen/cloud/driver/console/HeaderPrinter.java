package net.anweisen.cloud.driver.console;

import net.anweisen.cloud.driver.CloudDriver;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class HeaderPrinter {

	private HeaderPrinter() {}

	public static void printHeader(@Nonnull Console console, @Nonnull CloudDriver driver) {
		InputStream stream = driver.getClass().getClassLoader().getResourceAsStream("header.txt");
		if (stream == null) return;
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			String input;
			while ((input = bufferedReader.readLine()) != null) {
				console.writeLine(input);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
