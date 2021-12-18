package net.anweisen.cloud.base.setup;

import net.anweisen.cloud.base.setup.cnl.CNLInterpreter;
import net.anweisen.cloud.base.setup.cnl.commands.EchoCNLCommand;
import net.anweisen.cloud.base.setup.cnl.commands.VarCNLCommand;
import net.anweisen.utility.common.collection.WrappedException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class SetupRunner {

	private static final Path setupFile = Paths.get("setup.cnl");

	private SetupRunner() {}

	public static void runSetupJob() {
		System.out.println("Running DyCloud setup..");

		try {
			runInterpreter();
		} catch (Exception ex) {
			throw new WrappedException("Could not run interpreter", ex);
		}

		System.out.println("Finished setup! Proceeding to DyCloud startup..");
	}

	private static void runInterpreter() throws Exception {
		CNLInterpreter.registerCommand("var", new VarCNLCommand());
		CNLInterpreter.registerCommand("echo", new EchoCNLCommand());

		try (InputStream input = ClassLoader.getSystemResourceAsStream("setup.cnl")) {
			Objects.requireNonNull(input, "Unable to find the default setup config, is this jar corrupted?");

			if (Files.exists(setupFile)) {
				// adding the default variables first to make sure that all needed variables are existing
				CNLInterpreter.withInputStream(input);
			} else {
				Files.copy(input, setupFile);
			}
		}

		CNLInterpreter.withFile(setupFile);
	}
}
