package net.anweisen.cloud.driver.config.global;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.network.packet.def.GlobalConfigPacket;
import net.anweisen.cloud.driver.network.packet.def.GlobalConfigPacket.GlobalConfigPayload;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class RemoteGlobalConfig implements GlobalConfig {

	protected Document rawData;

	@Nonnull
	@Override
	public Document getRawData() {
		return rawData;
	}

	@Override
	@Nonnull
	public GlobalConfig setRawData(@Nonnull Document rawData) {
		this.rawData = rawData;
		return this;
	}

	@Override
	public void update() {
		CloudDriver.getInstance().getSocketComponent()
			.sendPacket(new GlobalConfigPacket(GlobalConfigPayload.UPDATE, rawData));
	}

	@Override
	public void fetch() {
		rawData = CloudDriver.getInstance().getSocketComponent().getFirstChannel()
			.sendPacketQuery(new GlobalConfigPacket(GlobalConfigPayload.FETCH))
			.getBuffer().readDocument();
	}
}
