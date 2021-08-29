package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.base.module.ModuleController;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ModuleSystemPacket.ModuleSystemRequestType;
import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.misc.FileUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ModuleSystemListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		Buffer buffer = packet.getBuffer();

		ModuleSystemRequestType type = buffer.readEnumConstant(ModuleSystemRequestType.class);
		switch (type) {
			case GET_MODULES: {
				channel.sendPacket(Packet.createResponseFor(packet, Buffer.create().writeStringArray(
					CloudMaster.getInstance().getModuleManager().getModules().stream().map(module -> module.getModuleConfig().getJarFile().getFileName().toString()).toArray(String[]::new)
				)));
				break;
			}
			case GET_MODULE_JAR: {
				int index = buffer.readInt();
				ModuleController module = CloudMaster.getInstance().getModuleManager().getModules().get(index);
				channel.sendChunkedPacketsResponse(packet.getUniqueId(), Document.empty(), Files.newInputStream(module.getModuleConfig().getJarFile()));
				break;
			}
			case GET_MODULE_DATA_FOLDER: {
				int index = buffer.readInt();
				ModuleController module = CloudMaster.getInstance().getModuleManager().getModules().get(index);
				channel.sendChunkedPacketsResponse(packet.getUniqueId(), Document.empty(), FileUtils.zipToStream(module.getDataFolder()));
				break;
			}
		}

	}

	private void sendModules(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws IOException {

		for (ModuleController module : CloudMaster.getInstance().getModuleManager().getModules()) {
			InputStream jarInputStream = Files.newInputStream(module.getModuleConfig().getJarFile());
			InputStream dataInputStream = FileUtils.zipToStream(module.getDataFolder());


		}

//		channel.sendChunkedPacketsResponse(packet.getUniqueId(), Document.empty(), inputStream);
	}
}
