package net.anweisen.cloud.driver.network.packet.def;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.listener.AuthenticationResponseListener;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketConstants;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;
import net.anweisen.cloud.driver.service.specific.ServiceType;
import net.anweisen.utilities.common.collection.ArrayWalker;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see AuthenticationResponseListener#readConfigProperties()
 */
public class AuthenticationResponsePacket extends Packet {

	public AuthenticationResponsePacket(boolean access, @Nonnull String message) {
		super(PacketConstants.AUTH_CHANNEL, Document.create().set("access", access).set("message", message));

		if (access) {
			buffer = newBuffer();
			appendConfigProperties();
		}
	}

	@SuppressWarnings("unchecked")
	public void appendConfigProperties() {
		CloudDriver driver = CloudDriver.getInstance();

		append(PropertySection.LOG_LEVEL, buffer -> buffer.writeEnum(driver.getLogger().getMinLevel()));
		append(PropertySection.TASKS, buffer -> buffer.writeObjectCollection(driver.getServiceConfigManager().getTasks()));
		append(PropertySection.TEMPLATE_STORAGES, buffer -> buffer.writeStringCollection(driver.getServiceConfigManager().getTemplateStorageNames()));
		append(PropertySection.SERVICES, buffer -> buffer.writeObjectCollection(driver.getServiceManager().getServiceInfos()));
		append(PropertySection.START_PORTS, buffer -> ArrayWalker.walk(ServiceType.values()).forEach(type -> buffer.writeVarInt(type.getStartPort())));
		append(PropertySection.PERMISSION_GROUPS, driver.hasPermissionManager(), buffer -> buffer.writeObjectCollection((Collection<? extends SerializableObject>) (Collection<?>) driver.getPermissionManager().getGroups()));
		append(PropertySection.ONLINE_PLAYERS, buffer -> buffer.writeObjectCollection((Collection<? extends SerializableObject>) (Collection<?>) driver.getPlayerManager().getOnlinePlayers()));
		append(PropertySection.GLOBAL_CONFIG, buffer -> buffer.writeDocument(driver.getGlobalConfig().getRawData()));
		append(PropertySection.LANGUAGES, buffer -> buffer.writeCollection(driver.getTranslationManager().getAvailableLanguages(), language -> buffer.writeString(language.getId()).writeObject(language.getConfig())));
		append(PropertySection.DEFAULT_LANGUAGE, buffer -> buffer.writeString(driver.getTranslationManager().getDefaultLanguage()));
	}

	private void append(@Nonnull PropertySection section, boolean condition, @Nonnull Consumer<? super PacketBuffer> modifier) {
		if (condition)
			append(section, modifier);
	}

	private void append(@Nonnull PropertySection section, @Nonnull Consumer<? super PacketBuffer> modifier) {
		buffer.writeEnum(section);
		modifier.accept(buffer);
	}

	public enum PropertySection {
		LOG_LEVEL,
		TASKS,
		TEMPLATE_STORAGES,
		SERVICES,
		START_PORTS,
		PERMISSION_GROUPS,
		ONLINE_PLAYERS,
		GLOBAL_CONFIG,
		LANGUAGES,
		DEFAULT_LANGUAGE
	}
}
