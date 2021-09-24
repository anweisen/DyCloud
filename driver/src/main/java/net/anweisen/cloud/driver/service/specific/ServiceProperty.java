package net.anweisen.cloud.driver.service.specific;

import net.anweisen.cloud.driver.service.specific.data.PlayerInfo;
import net.anweisen.cloud.driver.service.specific.data.PluginInfo;
import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see ServiceInfo#get(ServiceProperty)
 * @see ServiceInfo#set(ServiceProperty, Object)
 */
public interface ServiceProperty<T> {

	/**
	 * The current amount of players on the service
	 */
	ServiceProperty<Integer> ONLINE_PLAYERS = newServiceProperty("online", Document::getInt);

	/**
	 * The max allowed amount of players on the service
	 */
	ServiceProperty<Integer> MAX_PLAYERS = newServiceProperty("max", Document::getInt);

	/**
	 * The players currently on the service, represented by a {@link PlayerInfo} object, holding uuid and name
	 */
	ServiceProperty<List<PlayerInfo>> PLAYERS = newServiceListProperty("players", PlayerInfo.class);

	/**
	 * The plugins on the service, represented by a {@link PluginInfo} object, holding name, description, versions, mainClass, authors and website
	 */
	ServiceProperty<List<PluginInfo>> PLUGINS = newServiceListProperty("plugins", PluginInfo.class);

	/**
	 * The registered plugin messaging channels of the proxy messenger
	 */
	ServiceProperty<Collection<String>> MESSAGING_CHANNELS = newServiceProperty("channels", Document::getStringList);

	/**
	 * A string representing the current status of the service like LOBBY, FULL, INGAME, defaults to LOBBY when started
	 */
	ServiceProperty<String> STATUS = newServiceProperty("status", Document::getString);

	/**
	 * A string with no internal use, can be used for the map name example
	 */
	ServiceProperty<String> EXTRA = newServiceProperty("extra", Document::getString);

	/**
	 * The motd of the service
	 */
	ServiceProperty<String> MOTD = newServiceProperty("motd", Document::getString);

	/**
	 * The status of the whitelist on the service
	 */
	ServiceProperty<Boolean> WHITELIST = newServiceProperty("whitelist", Document::getBoolean);

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
	static <T> ServiceProperty<List<T>> newServiceListProperty(@Nonnull String name, @Nonnull Function<Document, T> mapper) {
		return newServiceProperty(name, (document, path) -> document.getDocumentList(path).stream().map(mapper).collect(Collectors.toList()));
	}

	@Nonnull
	static <T> ServiceProperty<List<T>> newServiceListProperty(@Nonnull String name, @Nonnull Class<T> classOfT) {
		return newServiceProperty(name, (document, path) -> document.getInstanceList(path, classOfT));
	}

}
