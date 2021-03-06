package net.anweisen.cloud.master.network.listener;

import net.anweisen.cloud.base.module.ModuleController;
import net.anweisen.cloud.base.module.config.ModuleCopyType;
import net.anweisen.cloud.driver.DriverEnvironment;
import net.anweisen.cloud.driver.network.SocketChannel;
import net.anweisen.cloud.driver.network.packet.Packet;
import net.anweisen.cloud.driver.network.packet.PacketListener;
import net.anweisen.cloud.driver.network.packet.def.ModuleSystemPacket.ModuleSystemPayload;
import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utility.common.misc.FileUtils;
import net.anweisen.utility.document.Documents;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ModuleSystemListener implements PacketListener {

	@Override
	public void handlePacket(@Nonnull SocketChannel channel, @Nonnull Packet packet) throws Exception {
		PacketBuffer buffer = packet.getBuffer();

		ModuleSystemPayload payload = buffer.readEnum(ModuleSystemPayload.class);
		switch (payload) {
			case GET_MODULES: {
				channel.sendPacket(Packet.createResponseFor(packet, Packet.newBuffer().writeStringArray(
					getModules().stream().map(module -> module.getJarFile().getFileName().toString()).toArray(String[]::new)
				)));
				break;
			}
			case GET_MODULE_JAR: {
				int index = buffer.readInt();
				ModuleController module = getModules().get(index);
				channel.sendChunkedPacketsResponse(packet.getUniqueId(), Documents.emptyDocument(), Files.newInputStream(module.getJarFile()));
				break;
			}
			case GET_MODULE_DATA_FOLDER: {
				int index = buffer.readInt();
				ModuleController module = getModules().get(index);
				channel.sendChunkedPacketsResponse(packet.getUniqueId(), Documents.emptyDocument(), FileUtils.zipToStream(module.getDataFolder()));
				break;
			}
		}

	}

	@Nonnull
	private List<ModuleController> getModules() {
		List<ModuleController> modules = new ArrayList<>(CloudMaster.getInstance().getModuleManager().getModules());
		modules.removeIf(module -> !module.getModuleConfig().getEnvironment().applies(DriverEnvironment.NODE) && module.getModuleConfig().getCopyType() == ModuleCopyType.NONE);
		return modules;
	}

}
