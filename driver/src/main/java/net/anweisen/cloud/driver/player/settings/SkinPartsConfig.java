package net.anweisen.cloud.driver.player.settings;

/**
 * Represents a player's skin settings provided by the proxy.
 * These settings can be changed by the player under Skin Configuration in the Options menu.
 *
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 *
 * @see PlayerSettings#getSkinParts()
 */
public interface SkinPartsConfig {

	boolean hasCape();

	boolean hasJacket();

	boolean hasLeftSleeve();

	boolean hasRightSleeve();

	boolean hasLeftPants();

	boolean hasRightPants();

	boolean hasHat();

}
