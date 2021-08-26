package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.utilities.common.collection.ArrayWalker;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see net.anweisen.cloud.driver.network.listener.PublishConfigListener
 */
public class ConfigInitPacket extends Packet {

	public static ConfigInitPacket create() {
		return new ConfigInitPacket();
	}

	@SuppressWarnings("unchecked")
	private ConfigInitPacket() {
		super(PacketConstants.PUBLISH_CONFIG_CHANNEL, Buffer.create());

		CloudDriver driver = CloudDriver.getInstance();

		append(PropertySection.LOG_LEVEL, buffer -> buffer.writeEnumConstant(driver.getLogger().getMinLevel()));
		append(PropertySection.TASKS, buffer -> buffer.writeObjectCollection(driver.getServiceConfigManager().getTasks()));
		append(PropertySection.TEMPLATE_STORAGES, buffer -> buffer.writeStringCollection(driver.getServiceConfigManager().getTemplateStorageNames()));
		append(PropertySection.SERVICES, buffer -> buffer.writeObjectCollection(driver.getServiceManager().getServiceInfos()));
		append(PropertySection.START_PORTS, buffer -> ArrayWalker.walk(ServiceType.values()).forEach(type -> buffer.writeVarInt(type.getStartPort())));
		append(PropertySection.PERMISSION_GROUPS, driver.hasPermissionManager(), buffer -> buffer.writeObjectCollection((Collection<? extends SerializableObject>) (Collection<?>) driver.getPermissionManager().getGroups()));
		append(PropertySection.ONLINE_PLAYERS, buffer -> buffer.writeObjectCollection((Collection<? extends SerializableObject>) (Collection<?>) driver.getPlayerManager().getOnlinePlayers()));

	}

	private void append(@Nonnull PropertySection section, boolean condition, @Nonnull Consumer<? super Buffer> modifier) {
		if (condition)
			append(section, modifier);
	}

	private void append(@Nonnull PropertySection section, @Nonnull Consumer<? super Buffer> modifier) {
		body.writeEnumConstant(section);
		modifier.accept(body);
	}

	public enum PropertySection {
		LOG_LEVEL,
		TASKS,
		TEMPLATE_STORAGES,
		SERVICES,
		START_PORTS,
		PERMISSION_GROUPS,
		ONLINE_PLAYERS
	}

}
