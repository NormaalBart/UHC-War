package me.bartvv.uhcwar.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.User;
import me.bartvv.uhcwar.manager.GameManager.GameState;

@AllArgsConstructor
public class PlayerDeathListener implements Listener {

	private UHCWar uhcWar;

	@EventHandler
	public void on(PlayerDeathEvent e) {
		if (this.uhcWar.getGameManager().getGameState() != GameState.STARTED) {
			return;
		}
		if (e.getEntity() instanceof Player) {
			e.setDeathMessage(null);
			String name = e.getEntity().getKiller() instanceof Player ? e.getEntity().getKiller().getName() : "Mob";
			String msg = Utils.tl("deathMessage", name, e.getEntity().getName());
			for (User user : uhcWar.getGameManager().getUserMap().values()) {
				user.sendMessage(msg);
			}

			User death = uhcWar.getGameManager().getUser(e.getEntity().getName());
			Player player = e.getEntity();
			player.setHealth(player.getMaxHealth());
			try {
				Player killer = (Player) player.getKiller();
				User user = uhcWar.getGameManager().getUser(killer.getName());
				user.setKills(user.getKills() + 1);
			} catch (Exception exc) {
			}
			uhcWar.getGameManager().userDeath(death);
			uhcWar.getGameManager().stop();
		}
	}
}
