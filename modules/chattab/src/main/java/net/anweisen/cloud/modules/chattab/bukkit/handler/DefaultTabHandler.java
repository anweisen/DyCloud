package net.anweisen.cloud.modules.chattab.bukkit.handler;

import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.cloud.modules.chattab.bukkit.BukkitCloudChatTabPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class DefaultTabHandler implements TabHandler {

	@Override
	public void update() {
		PermissionGroup globalHighestGroup = CloudDriver.getInstance().getPermissionManager().getHighestGroup();
		if (globalHighestGroup == null) return;

		int maxSortIdLength = String.valueOf(globalHighestGroup.getSortId()).length();

		for (Player player : Bukkit.getOnlinePlayers()) {
			update(player, maxSortIdLength);
		}
	}

	public void update(@Nonnull Player player, int maxSortIdLength) {
		PermissionPlayer permissionPlayer = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(player.getUniqueId());
		PermissionGroup permissionGroup = permissionPlayer.getHighestGroup();

		for (Player observator : Bukkit.getOnlinePlayers()) {
			Scoreboard scoreboard = observator.getScoreboard();
			if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard())
				observator.setScoreboard(scoreboard = Bukkit.getScoreboardManager().getNewScoreboard());

			String teamName = String.format("%0" + maxSortIdLength + "d", permissionGroup.getSortId()) + permissionGroup.getName();
			if (teamName.length() > 16)
				teamName = teamName.substring(0, 16);

			Team team = scoreboard.getTeam(teamName);
			if (team == null)
				team = scoreboard.registerNewTeam(teamName);

			String displayName = permissionGroup.getColor() + player.getName();
			player.setDisplayName(displayName);
			player.setPlayerListName(BukkitCloudChatTabPlugin.getInstance().getChatTabConfig().getTablist().getPrefix() + displayName);

			team.addEntry(player.getName());

			team.setDisplayName(permissionGroup.getDisplayName());
			try {
				team.setColor(ChatColor.getByChar(permissionGroup.getColor().replace("§", "")));
			} catch (Throwable ex) {
			}

		}
	}
}
