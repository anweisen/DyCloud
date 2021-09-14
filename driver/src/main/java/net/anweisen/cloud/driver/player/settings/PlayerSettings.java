package net.anweisen.cloud.driver.player.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface PlayerSettings {

	@Nullable
	Locale getLocale();

	byte getRenderDistance();

	boolean hasChatColors();

	@Nonnull
	SkinParts getSkinParts();

	@Nonnull
	ChatMode getChatMode();

	@Nonnull
	MainHand getMainHand();

}
