package net.anweisen.cloud.modules.chattab.bukkit.handler;

import com.google.common.base.Preconditions;
import net.anweisen.cloud.driver.CloudDriver;
import net.anweisen.cloud.driver.player.permission.PermissionGroup;
import net.anweisen.cloud.driver.player.permission.PermissionPlayer;
import net.anweisen.cloud.modules.chattab.bukkit.BukkitCloudChatTabPlugin;
import net.anweisen.cloud.modules.chattab.bukkit.handler.TabFormatter.Format;
import net.anweisen.cloud.modules.chattab.config.TabConfig;
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

	public static final int SORT_ID_LENGTH = 5;

	private TabFormatter formatter = new DefaultTabFormatter();

	@Nonnull
	@Override
	public TabFormatter getFormatter() {
		return formatter;
	}

	@Override
	public void setFormatter(@Nonnull TabFormatter formatter) {
		Preconditions.checkNotNull(formatter, "Cannot set formatter to null");
		this.formatter = formatter;
	}

	@Override
	public void update() {
		PermissionGroup globalHighestGroup = CloudDriver.getInstance().getPermissionManager().getHighestGroup();
		if (globalHighestGroup == null) return;

		for (Player player : Bukkit.getOnlinePlayers()) {
			update(player);
		}
	}

	public void update(@Nonnull Player player) {
		PermissionPlayer permissionPlayer = CloudDriver.getInstance().getPermissionManager().getPlayerByUniqueId(player.getUniqueId());
		PermissionGroup permissionGroup = permissionPlayer.getHighestGroup();

		TabConfig tablistConfig = BukkitCloudChatTabPlugin.getInstance().getChatTabConfig().getTablist();

		for (Player observator : Bukkit.getOnlinePlayers()) {
			Scoreboard scoreboard = observator.getScoreboard();
			if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard())
				observator.setScoreboard(scoreboard = Bukkit.getScoreboardManager().getNewScoreboard());

			Format format = formatter.format(observator, player, permissionPlayer, permissionGroup);

			String teamName = String.format("%0" + SORT_ID_LENGTH + "d", format.getSortId()) + "-" + player.getUniqueId();
			if (teamName.length() > 16)
				teamName = teamName.substring(0, 16);

			Team team = scoreboard.getTeam(teamName);
			if (team == null)
				team = scoreboard.registerNewTeam(teamName);
			team.addEntry(player.getName());
			team.setDisplayName(player.getName());

			team.setPrefix(format.getNametagPrefix());
			team.setSuffix(format.getNametagSuffix());

			if (format.getTeamColor() != null) {
				team.setColor(format.getTeamColor());
			} else {
				try {
					team.setColor(ChatColor.getByChar(ChatColor.getLastColors(format.getNametagPrefix()).replace("§", "")));
				} catch (Throwable ex) {
				}
			}

			player.setPlayerListName(
				(format.getApplyTablistConfig() ? tablistConfig.getPrefix() : "")
			   + format.getTablistName() +
				(format.getApplyTablistConfig() ? tablistConfig.getSuffix() : "")
			);
			player.setDisplayName(format.getDisplayName());

		}
	}
}
