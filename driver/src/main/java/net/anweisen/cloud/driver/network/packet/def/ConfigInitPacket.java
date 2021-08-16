package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.config.TemplateStorage;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ConfigInitPacket extends Packet {

	public static ConfigInitPacket create() {
		CloudDriver driver = CloudDriver.getInstance();
		return new ConfigInitPacket(
			Buffer.create()
				.writeEnumConstant(driver.getLogger().getMinLevel())
				.writeObjectCollection(driver.getServiceConfigManager().getTasks())
				.writeStringCollection(driver.getServiceConfigManager().getTemplateStorages().stream().map(TemplateStorage::getName).collect(Collectors.toList()))
		);
	}

	private ConfigInitPacket(@Nonnull Buffer buffer) {
		super(PacketConstants.PUBLISH_CONFIG_CHANNEL, buffer);
	}

}
