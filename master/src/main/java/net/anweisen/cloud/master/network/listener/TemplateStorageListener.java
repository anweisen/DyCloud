package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.driver.console.LoggingApiUser;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.TemplateStoragePacket.TemplateStoragePayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.service.config.ServiceTemplate;
import net.anweisen.cloud.driver.service.config.TemplateStorage;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utility.common.misc.FileUtils;
import net.anweisen.utility.document.Documents;

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
		PacketBuffer buffer = packet.getBuffer();

		TemplateStoragePayload payload = buffer.readEnum(TemplateStoragePayload.class);
		switch (payload) {
			case LOAD_TEMPLATE_STREAM: {
				ServiceTemplate template = buffer.readObject(ServiceTemplate.class);
				TemplateStorage storage = template.findStorage();
				InputStream input = storage == null ? null : storage.zipTemplate(template);

				if (input == null) {
					warn("Could not load requested template stream for {}", template.toShortString());
					channel.sendChunkedPacketsResponse(packet.getUniqueId(), Documents.newJsonDocument("exists", false), FileUtils.EMPTY_STREAM);
				} else {
					channel.sendChunkedPacketsResponse(packet.getUniqueId(), Documents.newJsonDocument("exists", true), input);
				}
				break;
			}
			case GET_TEMPLATES: {
				String name = buffer.readString();
				TemplateStorage storage = cloud.getServiceConfigManager().getTemplateStorage(name);
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeObjectCollection(storage == null ? Collections.emptyList() : storage.getTemplates())));
				break;
			}
			case HAS_TEMPLATE: {
				ServiceTemplate template = buffer.readObject(ServiceTemplate.class);
				TemplateStorage storage = template.findStorage();
				boolean has = storage != null && storage.hasTemplate(template);
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeBoolean(has)));
				break;
			}
		}
	}
}
