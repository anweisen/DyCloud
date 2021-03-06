package net.anweisen.cloud.master.config.global;

import com.google.common.collect.ImmutableMap;
import net.anweisen.cloud.driver.config.global.GlobalConfig;
import net.anweisen.cloud.driver.network.packet.def.GlobalConfigPacket;
import net.anweisen.cloud.driver.network.packet.def.GlobalConfigPacket.GlobalConfigPayload;
import net.anweisen.cloud.master.CloudMaster;
import net.anweisen.utility.document.Document;
import net.anweisen.utility.document.Documents;
import net.anweisen.utility.document.wrapped.StorableDocument;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class MasterGlobalConfig implements GlobalConfig {

	private static final Path path = Paths.get("global.json");
	private static final Map<String, Object> defaultValues = ImmutableMap.of(
		"maxPlayers", 50, "maintenance", false);

	private Document rawData;

	@Nonnull
	@Override
	public Document getRawData() {
		return rawData;
	}

	@Nonnull
	@Override
	public GlobalConfig setRawData(@Nonnull Document data) {
		rawData = data;
		update();
		return this;
	}

	@Override
	public void update() {
		// We only save values which keys are contained in defaultValues, because we dont want temp values to be saved
		StorableDocument document = Documents.newStorableDocument(Documents.newJsonDocument(), path);
		rawData.forEach((key, value) -> {
			if (defaultValues.containsKey(key))
				document.set(key, value);
		});
		document.save();

		CloudMaster.getInstance().getSocketComponent().sendPacket(new GlobalConfigPacket(GlobalConfigPayload.UPDATE, rawData));
	}

	@Override
	public void fetch() {
		rawData = Documents.newJsonDocumentUnchecked(path);

		defaultValues.forEach((key, value) -> {
			if (!rawData.contains(key))
				rawData.set(key, value);
		});

		update();
	}
}
