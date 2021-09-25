package net.anweisen.cloud.driver.player.settings;

import net.anweisen.cloud.driver.network.packet.protocol.PacketBuffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPlayerSettings implements PlayerSettings, SerializableObject {

	private Locale locale;
	private byte renderDistance;
	private boolean chatColors;
	private DefaultSkinParts skinParts;
	private ChatMode chatMode;
	private MainHand mainHand;

	private DefaultPlayerSettings() {
	}

	public DefaultPlayerSettings(@Nonnull Locale locale, byte renderDistance, boolean chatColors, @Nonnull DefaultSkinParts skinParts, @Nonnull ChatMode chatMode, @Nonnull MainHand mainHand) {
		this.locale = locale;
		this.renderDistance = renderDistance;
		this.chatColors = chatColors;
		this.skinParts = skinParts;
		this.chatMode = chatMode;
		this.mainHand = mainHand;
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer) {
		buffer.writeByte(renderDistance);
		buffer.writeBoolean(chatColors);
		buffer.writeObject(skinParts);
		buffer.writeEnum(chatMode);
		buffer.writeEnum(mainHand);
		buffer.writeBoolean(locale == null);
		if (locale != null) {
			buffer.writeOptionalString(locale.getLanguage());
			buffer.writeOptionalString(locale.getCountry());
			buffer.writeOptionalString(locale.getVariant());
		}
	}

	@Override
	public void read(@Nonnull PacketBuffer buffer) {
		renderDistance = buffer.readByte();
		chatColors = buffer.readBoolean();
		skinParts = buffer.readObject(DefaultSkinParts.class);
		chatMode = buffer.readEnum(ChatMode.class);
		mainHand = buffer.readEnum(MainHand.class);
		locale = buffer.readBoolean() ? null : new Locale(
			stringOrEmpty(buffer.readOptionalString()), // language
			stringOrEmpty(buffer.readOptionalString()), // country
			stringOrEmpty(buffer.readOptionalString())  // variant
		);
	}

	@Nonnull
	private String stringOrEmpty(@Nullable String value) {
		return value == null ? "" : value;
	}

	@Nonnull
	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public byte getRenderDistance() {
		return renderDistance;
	}

	@Override
	public boolean hasChatColors() {
		return chatColors;
	}

	@Nonnull
	@Override
	public SkinParts getSkinParts() {
		return skinParts;
	}

	@Nonnull
	@Override
	public ChatMode getChatMode() {
		return chatMode;
	}

	@Nonnull
	@Override
	public MainHand getMainHand() {
		return mainHand;
	}

	@Override
	public String toString() {
		return "PlayerSettings[locale=" + locale + " renderDistance=" + renderDistance + " chatColors=" + chatColors + " chatMode=" + chatMode + " mainHand=" + mainHand + " skin=" + skinParts + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultPlayerSettings that = (DefaultPlayerSettings) o;
		return renderDistance == that.renderDistance
			&& chatColors == that.chatColors
			&& chatMode == that.chatMode
			&& mainHand == that.mainHand
			&& Objects.equals(locale, that.locale)
			&& Objects.equals(skinParts, that.skinParts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(locale, renderDistance, chatColors, skinParts, chatMode, mainHand);
	}
}
