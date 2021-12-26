package net.anweisen.cloud.modules.chattab.bukkit.handler;

import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public interface TabFormatter {

	@Nonnull
	Format format(@Nonnull Player observator, @Nonnull Player player, @Nonnull PermissionPlayer permissionPlayer, @Nonnull PermissionGroup permissionGroup);

	class Format {

		private final String displayName;
		private final String tablistName;
		private final int sortId;
		private final String nametagPrefix;
		private final String nametagSuffix;
		private final ChatColor teamColor;
		private final boolean applyTablistConfig;

		public Format(@Nonnull String displayName, @Nonnull String tablistName,
		              @Nonnull String nametagPrefix, @Nonnull String nametagSuffix,
		              @Nullable ChatColor teamColor, int sortId, boolean applyTablistConfig) {
			this.displayName = displayName;
			this.tablistName = tablistName;
			this.nametagPrefix = nametagPrefix;
			this.nametagSuffix = nametagSuffix;
			this.teamColor = teamColor;
			this.sortId = sortId;
			this.applyTablistConfig = applyTablistConfig;
		}

		@Nonnull
		public String getDisplayName() {
			return displayName;
		}

		@Nonnull
		public String getTablistName() {
			return tablistName;
		}

		@Nonnull
		public String getNametagPrefix() {
			return nametagPrefix;
		}

		@Nonnull
		public String getNametagSuffix() {
			return nametagSuffix;
		}

		@Nullable
		public ChatColor getTeamColor() {
			return teamColor;
		}

		public int getSortId() {
			return sortId;
		}

		public boolean getApplyTablistConfig() {
			return applyTablistConfig;
		}
	}

}
