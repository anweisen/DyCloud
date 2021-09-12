package net.anweisen.cloud.driver.player.settings;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerSettings {

	@Nonnull
	Locale getLocale();

	byte getRenderDistance();

	boolean hasChatColors();

	@Nonnull
	SkinPartsConfig getSkinParts();

	@Nonnull
	ChatMode getChatMode();

	@Nonnull
	MainHand getMainHand();

}
