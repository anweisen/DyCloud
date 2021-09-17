package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.TemplateStoragePacket.TemplateStorageRequestType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.service.config.ServiceTemplate;
import net.anweisen.cloud.driver.service.config.TemplateStorage;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.Collections;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class TemplateStorageListener implements PacketListener, LoggingApiUser {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		CloudMaster cloud = CloudMaster.getInstance();
		Buffer buffer = packet.getBuffer();

		TemplateStorageRequestType type = buffer.readEnumConstant(TemplateStorageRequestType.class);
		switch (type) {
			case LOAD_TEMPLATE_STREAM: {
				ServiceTemplate template = buffer.readObject(ServiceTemplate.class);
				TemplateStorage storage = template.findStorage();
				InputStream input = storage == null ? null : storage.zipTemplate(template);

				if (input == null) {
					warn("Could not load requested template stream for {}", template.toShortString());
					channel.sendChunkedPacketsResponse(packet.getUniqueId(), Document.create().set("exists", false), FileUtils.EMPTY_STREAM);
				} else {
					channel.sendChunkedPacketsResponse(packet.getUniqueId(), Document.create().set("exists", true), input);
				}
				break;
			}
			case GET_TEMPLATES: {
				String name = buffer.readString();
				TemplateStorage storage = cloud.getServiceConfigManager().getTemplateStorage(name);
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeObjectCollection(storage == null ? Collections.emptyList() : storage.getTemplates())));
				break;
			}
			case HAS_TEMPLATE: {
				ServiceTemplate template = buffer.readObject(ServiceTemplate.class);
				TemplateStorage storage = template.findStorage();
				boolean has = storage != null && storage.hasTemplate(template);
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeBoolean(has)));
				break;
			}
		}
	}
}
