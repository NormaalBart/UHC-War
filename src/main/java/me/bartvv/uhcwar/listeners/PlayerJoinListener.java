package me.bartvv.uhcwar.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.GameManager.GameState;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

	private UHCWar uhcWar;

	@EventHandler
	public void on(PlayerJoinEvent e) {
		final Player player = e.getPlayer();

		e.setJoinMessage(Utils.tl("join-Message", player.getName(), Bukkit.getOnlinePlayers().size()));

		if (this.uhcWar.getGameManager().getGameState() == GameState.STOPPING) {
			if (uhcWar.getConfig().getString("fallBack-server").equalsIgnoreCase("none")) {
				player.kickPlayer("Server is restarting");
				return;
			} else {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(uhcWar.getConfig().getString("fallBack-server"));
				player.sendPluginMessage(uhcWar, "BungeeCord", out.toByteArray());
				return;
			}
		}
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		uhcWar.getGameManager().addUser(player.getName());

		try {
			player.teleport(uhcWar.getData().getLocation("spawn"));
		} catch (Exception exc) {
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					player.teleport(uhcWar.getData().getLocation("spawn"));
				} catch (Exception exc) {
				}
			}
		}.runTaskLater(uhcWar, 2);

		uhcWar.getTabList().setTablist(player);

		for (Player players : Bukkit.getOnlinePlayers()) {
			uhcWar.getTabList().refreshTablist(players);
		}
	}

}
