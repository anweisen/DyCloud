package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.service.specific.data.MinecraftPlayerInfo;
import net.anweisen.cloud.driver.service.specific.data.PluginInfo;
import net.anweisen.cloud.driver.service.specific.data.ProxyPlayerInfo;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface ServiceProperty<T> {

	ServiceProperty<Integer> ONLINE_PLAYER_COUNT = newServiceProperty("online", Document::getInt);
	ServiceProperty<Integer> MAX_PLAYER_COUNT = newServiceProperty("max", Document::getInt);
	ServiceProperty<List<MinecraftPlayerInfo>> MINECRAFT_PLAYER_LIST = newServiceListProperty("players", MinecraftPlayerInfo.class);
	ServiceProperty<List<ProxyPlayerInfo>> PROXY_PLAYER_LIST = newServiceListProperty("players", ProxyPlayerInfo.class);
	ServiceProperty<List<PluginInfo>> PLUGINS = newServiceListProperty("plugins", PluginInfo.class);
	ServiceProperty<Collection<String>> MESSAGING_CHANNELS = newServiceProperty("channels", Document::getStringList);
	ServiceProperty<String> EXTRA = newServiceProperty("extra", Document::getString);
	ServiceProperty<String> MOTD = newServiceProperty("motd", Document::getString);

	@Nonnull
	String getPropertyName();

	T getProperty(@Nonnull Document properties);

	void setProperty(@Nonnull Document properties, T value);

	@Nonnull
	static <T> ServiceProperty<T> newServiceProperty(@Nonnull String name, @Nonnull BiFunction<Document, String, T> getter) {
		return new ServiceProperty<T>() {

			@Nonnull
			@Override
			public String getPropertyName() {
				return name;
			}

			@Override
			public T getProperty(@Nonnull Document properties) {
				return getter.apply(properties, name);
			}

			@Override
			public void setProperty(@Nonnull Document properties, T value) {
				properties.set(name, value);
			}
		};
	}

	@Nonnull
	static <T> ServiceProperty<List<T>> newServiceListProperty(@Nonnull String name, @Nonnull Class<T> classOfT) {
		return newServiceProperty(name, (document, path) -> document.getInstanceList(path, classOfT));
	}

}
