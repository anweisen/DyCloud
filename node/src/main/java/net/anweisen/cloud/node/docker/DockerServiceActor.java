package net.anweisen.cloud.node.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.SyncDockerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import net.anweisen.cloud.base.module.ModuleController;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.config.ServiceTemplate;
import net.anweisen.cloud.driver.service.config.TemplateStorage;
import net.anweisen.cloud.driver.service.specific.ServiceControlState;
import net.anweisen.cloud.driver.service.specific.ServiceEnvironment;
import net.anweisen.cloud.driver.service.specific.ServiceInfo;
import net.anweisen.cloud.driver.service.specific.ServiceState;
import net.anweisen.cloud.node.CloudNode;
import net.anweisen.utility.common.misc.FileUtils;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DockerServiceActor implements LoggingApiUser {

	private static final String serverDirectory = "/server";
	private static final int containerPort = 25565;

	public void startService(@Nonnull ServiceInfo service) {
		execAction(service, DockerClient::startContainerCmd);
	}

	public void restartService(@Nonnull ServiceInfo service) {
		execAction(service, DockerClient::restartContainerCmd);
	}

	public void stopService(@Nonnull ServiceInfo service) {
		execAction(service, DockerClient::stopContainerCmd);
	}

	public void killService(@Nonnull ServiceInfo service) {
		execAction(service, DockerClient::killContainerCmd);
	}

	public void deleteService(@Nonnull ServiceInfo service) {
		execAction(service, DockerClient::removeContainerCmd);
	}

	private void execAction(@Nonnull ServiceInfo service, @Nonnull BiFunction<DockerClient, String, SyncDockerCmd<?>> commandCreator) {
		commandCreator.apply(CloudNode.getInstance().getDockerClient(), service.getDockerContainerId()).exec();
	}

	// TODO split method up
	// This method does not publish the updated ServiceInfo
	public void createServiceHere(@Nonnull ServiceInfo info, @Nonnull ServiceTask task) throws IOException {
		CloudNode cloud = CloudNode.getInstance();

		debug("Got order to create {} of {}", info, task);

		Path wrapperOrigin = Paths.get("wrapper.jar");
		if (!Files.exists(wrapperOrigin)) throw new IllegalStateException("Missing wrapper.jar");

		Path tempTemplateDirectory = FileUtils.getTempDirectory().resolve(info.getName());
		FileUtils.createDirectory(tempTemplateDirectory);

		// Download, extract and copy templates
		for (ServiceTemplate template : task.getTemplates()) {
			long startMillis = System.currentTimeMillis();
			debug("Downloading template '{}'..", template.toShortString()); // TODO cache this, taking way to long to download a 1gb map every time

			TemplateStorage storage = template.findStorage();
			if (storage == null) {
				warn("TemplateStorage of '{}' cannot be found", template.toShortString());
				continue;
			}
			InputStream stream = storage.zipTemplate(template);
			if (stream == null) {
				warn("{} could not be found / get", template);
				storage.getTemplates().forEach(current -> extended("=> {}", current));
				continue;
			}

			trace("Finished downloading template '{}' in {}ms. Extracting..", template.toShortString(), System.currentTimeMillis() - startMillis);
			startMillis = System.currentTimeMillis();
			FileUtils.extract(stream, tempTemplateDirectory);
			stream.close();
			trace("Finished extracting template '{}' in {}ms", template.toShortString(), System.currentTimeMillis() - startMillis);
		}
		// Copy modules
		for (ModuleController module : cloud.getModuleManager().getModules()) {
			if (module.getModuleConfig().getCopyType().applies(task.getEnvironment().getServiceType())) {
				if (!module.isEnabled()) {
					trace("Skipping module '{}' for '{}', disabled", module.getJarFile().getFileName(), info);
					continue;
				}

				trace("Copying module '{}' to {}..", module.getJarFile().getFileName(), info.getName());
				FileUtils.copy(module.getJarFile(), tempTemplateDirectory.resolve(task.getEnvironment().getPluginsFolder() + "/" + module.getJarFile().getFileName()));
			}
		}
		// Copy config files
		ServiceEnvironment environment = task.getEnvironment();
		for (String config : environment.getConfigs()) {
			trace("Copying config resource '{}' to {}..", config, info.getName());
			InputStream input = getClass().getClassLoader().getResourceAsStream("files/" + config);
			if (input == null) {
				warn("Unable to find config resource '{}'", config);
				continue;
			}

			OutputStream output = new FileOutputStream(tempTemplateDirectory.resolve(config).toFile());
			FileUtils.copy(input, output);
			input.close();
			output.close();
		}
		// Write eula
		if (environment.isServer()) {
			trace("Writing eula file for {}..", info);
			BufferedWriter writer = FileUtils.newBufferedWriter(tempTemplateDirectory.resolve("eula.txt"));
			writer.write("eula=true");
			writer.flush();
			writer.close();
		}

		Path applicationFile = getApplicationFile(tempTemplateDirectory);
		if (applicationFile == null) {
			warn("Unable to locate application file for '{}': Used templates: {}", info.getName(), task.getTemplates());
			FileUtils.list(tempTemplateDirectory).forEach(path -> extended("=> {}", path.toString()));
			FileUtils.delete(tempTemplateDirectory);
			return;
		}

		String image = "openjdk:" + task.getJavaVersion();
		debug("Creating docker container using image '{}' for {}..", image, info);
		DockerClient dockerClient = cloud.getDockerClient();

		// Normally, all required images are pulled on startup
		// But if the image is not installed here, we will just pull it again
		try {
			trace("Searching for image '{}'", image);
			dockerClient.inspectImageCmd(image).exec();
			trace("Found image '{}'", image);
		} catch (NotFoundException exNotFound) {
			try {
				info("Image not found, pulling image '{}'..", image);
				dockerClient.pullImageCmd(image).start().awaitCompletion();
				trace("Finished pulling image '{}'", image);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		List<String> arguments = new ArrayList<>(Collections.singletonList("java"));
		arguments.addAll(Arrays.asList(
			"-DIReallyKnowWhatIAmDoingISwear=true", // skip deprecated build warning
			"-Djline.terminal=jline.UnsupportedTerminal", // docker container dont support these, skip warning
			"-Dfile.encoding=UTF-8", // we want to use utf8 as standard everywhere
			"-Dclient.encoding.override=UTF-8",
			"-XX:+UseStringDeduplication",
			"-XX:-UseAdaptiveSizePolicy",
			"-XX:+UseCompressedOops"
		));
		if (task.getJavaVersion() >= 9) {
			arguments.addAll(Arrays.asList(
				"--add-opens", "java.base/jdk.internal.loader=ALL-UNNAMED" // needed to be able to access the private ucp field of the builtin class loader in java9+
			));
		}
		arguments.addAll(Arrays.asList(
			"-jar", "wrapper.jar", applicationFile.getFileName().toString()
		));

		// Create docker container
		String containerId = dockerClient.createContainerCmd(image)
			.withName(info.getName())
			.withWorkingDir(serverDirectory)
			.withCmd(arguments)
			.withPortSpecs(containerPort + "")
			.withExposedPorts(ExposedPort.tcp(containerPort), ExposedPort.udp(containerPort)) // we need to expose the port in order to get the port binding working
			.withHostName(CloudNode.getInstance().getConfig().getMasterAddress().getHost()) // TODO is this needed?
			.withHostConfig(new HostConfig()
				.withNetworkMode(cloud.getConfig().getDockerNetworkMode())
				.withPortBindings(
					new PortBinding(Binding.bindPort(info.getPort()), ExposedPort.tcp(containerPort)),
					new PortBinding(Binding.bindPort(info.getPort()), ExposedPort.udp(containerPort))
				)
				.withMemory(task.getMemoryLimit() < 1 ? null : 1024L * 1024L * task.getMemoryLimit()) // bytes -> kilobytes -> megabytes
			).exec().getId();
		info.setDockerContainerId(containerId);
		trace("Created docker container {} for {}", containerId, info);
		trace("=> Applied memory limit of {} bytes = {} kilobytes = {} megabytes", 1024L * 1024L * task.getMemoryLimit(), 1024L * task.getMemoryLimit(), task.getMemoryLimit());

		Documents.newJsonDocument(
			"master", cloud.getConfig().getMasterAddress(),
			"identity", cloud.getConfig().getIdentity(),
			"serviceTaskName", task.getName(),
			"serviceUniqueId", info.getUniqueId()
		).saveToFile(tempTemplateDirectory.resolve(".cloud/config.json"));

		// Copy resources to container
		dockerClient.copyArchiveToContainerCmd(containerId)
			.withHostResource(tempTemplateDirectory.toAbsolutePath().toString())
			.withRemotePath(serverDirectory)
			.withDirChildrenOnly(true)
			.withNoOverwriteDirNonDir(false)
			.exec();
		dockerClient.copyArchiveToContainerCmd(containerId)
			.withHostResource(wrapperOrigin.toAbsolutePath().toString())
			.withRemotePath(serverDirectory)
			.withDirChildrenOnly(true)
			.withNoOverwriteDirNonDir(false)
			.exec();
		info.setState(ServiceState.PREPARED);
		info.setControlState(ServiceControlState.NONE);
		trace("Successfully transferred archives to docker container of {}", info);

		FileUtils.delete(tempTemplateDirectory);
	}

	@Nullable
	private Path getApplicationFile(@Nonnull Path directory) throws IOException {
		return Files.list(directory)
			.filter(path -> path.toString().endsWith(".jar"))
			.filter(path -> !path.toString().endsWith("wrapper.jar"))
			.findFirst().orElse(null);
	}

}
