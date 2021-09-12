package net.anweisen.cloud.driver.player.settings;

import net.anweisen.cloud.driver.network.packet.protocol.Buffer;
import net.anweisen.cloud.driver.network.packet.protocol.SerializableObject;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPlayerSettings implements PlayerSettings, SerializableObject {

	private Locale locale;
	private byte renderDistance;
	private boolean chatColors;
	private DefaultSkinPartsConfig skinParts;
	private ChatMode chatMode;
	private MainHand mainHand;

	private DefaultPlayerSettings() {
	}

	public DefaultPlayerSettings(@Nonnull Locale locale, byte renderDistance, boolean chatColors, @Nonnull DefaultSkinPartsConfig skinParts, @Nonnull ChatMode chatMode, @Nonnull MainHand mainHand) {
		this.locale = locale;
		this.renderDistance = renderDistance;
		this.chatColors = chatColors;
		this.skinParts = skinParts;
		this.chatMode = chatMode;
		this.mainHand = mainHand;
	}

	@Override
	public void write(@Nonnull Buffer buffer) {
		buffer.writeString(locale.getLanguage());
		buffer.writeString(locale.getCountry());
		buffer.writeString(locale.getVariant());
		buffer.writeByte(renderDistance);
		buffer.writeObject(skinParts);
		buffer.writeEnumConstant(chatMode);
		buffer.writeEnumConstant(mainHand);
	}

	@Override
	public void read(@Nonnull Buffer buffer) {
		locale = new Locale(
			buffer.readString(), // language
			buffer.readString(), // country
			buffer.readString()  // variant
		);
		renderDistance = buffer.readByte();
		chatColors = buffer.readBoolean();
		skinParts = buffer.readObject(DefaultSkinPartsConfig.class);
		chatMode = buffer.readEnumConstant(ChatMode.class);
		mainHand = buffer.readEnumConstant(MainHand.class);
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
	public SkinPartsConfig getSkinParts() {
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
		return "PlayerSetting[locale=" + locale.toString() + " renderDistance=" + renderDistance + " chatColors=" + chatColors + " chatMode=" + chatMode + " mainHand=" + mainHand + " skin=" + skinParts + "]";
	}
}
