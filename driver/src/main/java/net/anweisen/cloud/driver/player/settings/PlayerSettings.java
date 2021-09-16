package net.anweisen.cloud.driver.player.settings;

import net.anweisen.cloud.driver.player.CloudPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see CloudPlayer#getSettings()
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
