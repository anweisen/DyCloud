package net.anweisen.cloud.base.command.sender.defaults;

import net.anweisen.cloud.base.command.sender.PlayerCommandSender;
import net.anweisen.cloud.driver.player.CloudPlayer;
import net.anweisen.cloud.driver.player.PlayerExecutor;
import net.anweisen.cloud.driver.player.chat.ChatText;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.cloud.driver.translate.Translatable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultPlayerCommandSender implements PlayerCommandSender {

	private final CloudPlayer player;

	public DefaultPlayerCommandSender(@Nonnull CloudPlayer player) {
		this.player = player;
	}

	@Override
	public void sendMessage(@Nonnull String message) {
		getExecutor().sendMessage(message);
	}

	@Override
	public void sendMessage(@Nonnull ChatText... message) {
		getExecutor().sendMessage(message);
	}

	@Override
	public void sendTranslation(@Nonnull String translation, @Nonnull Object... args) {
		sendTranslation(Translatable.of(translation), args);
	}

	@Override
	public void sendTranslation(@Nonnull Translatable translation, @Nonnull Object... args) {
		sendMessage(translation.translate(player).asText(args));
	}

	@Override
	public boolean hasPermission(@Nonnull String permission) {
		return getPermissionPlayer().hasPermission(permission);
	}

	@Nonnull
	@Override
	public CloudPlayer getPlayer() {
		return player;
	}

	@Nonnull
	@Override
	public PermissionPlayer getPermissionPlayer() {
		return player.getPermissionPlayer();
	}

	@Nonnull
	@Override
	public PlayerExecutor getExecutor() {
		return player.getExecutor();
	}

	@Nonnull
	@Override
	public String getName() {
		return player.getName();
	}

	@Nonnull
	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public String toString() {
		return "PlayerCommandSender[name=" + getName() + " uuid=" + getUniqueId() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultPlayerCommandSender that = (DefaultPlayerCommandSender) o;
		return Objects.equals(this.getUniqueId(), that.getUniqueId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUniqueId());
	}
}
