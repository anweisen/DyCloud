package net.anweisen.cloud.driver.network.listener;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.config.RemoteTemplateStorage;
import net.anweisen.cloud.driver.service.config.ServiceTask;
import net.anweisen.cloud.driver.service.config.TemplateStorage;
import net.anweisen.utilities.common.logging.LogLevel;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class PublishConfigListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudDriver cloud = CloudDriver.getInstance();

		info("Received published configs from master..");

		Buffer buffer = packet.getBuffer();

		getTargetLogger().setMinLevel(buffer.readEnumConstant(LogLevel.class));

		Collection<ServiceTask> tasks = buffer.readObjectCollection(ServiceTask.class);
		cloud.getServiceConfigManager().setServiceTasks(tasks);

		Collection<String> templateStorageNames = buffer.readStringCollection();
		Collection<TemplateStorage> templateStorages = templateStorageNames.stream().map(name -> new RemoteTemplateStorage(name, channel)).collect(Collectors.toList());
		cloud.getServiceConfigManager().setTemplateStorages(templateStorages);

		extended("LogLevel: " + getTargetLogger().getMinLevel());
		extended("ServiceTasks ({}):", tasks.size());
		for (ServiceTask task : tasks)
			extended("- {}", task);
		extended("TemplateStorages ({}):", templateStorageNames.size());
		for (String templateStorage : templateStorageNames)
			extended("- {}", templateStorage);

	}

}
