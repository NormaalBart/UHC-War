package me.bartvv.uhcwar.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.manager.GameManager.GameState;
import me.bartvv.uhcwar.manager.GameManager.Teams;
import me.bartvv.uhcwar.manager.Team;
import me.bartvv.uhcwar.manager.User;

@AllArgsConstructor
public class DamageListener implements Listener {

	private UHCWar uhcWar;

	@EventHandler
	public void on(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			User user = uhcWar.getGameManager().getUser(player.getName());
			if (this.uhcWar.getGameManager().getGameState() == GameState.STARTED)
				return;
			if (user.getTeam() == null)
				return;
			if (user.getTeam().getTeamEnum() != Teams.SPECTATOR)
				return;
			e.setCancelled(true);
			
		}
	}

	@EventHandler
	public void on(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			Team team = uhcWar.getGameManager().getUser(player.getName()).getTeam();
			if (team != null && team.getTeamEnum() == Teams.SPECTATOR) {
				e.setCancelled(true);
				player.setFireTicks(0);
				player.setHealth(player.getMaxHealth());
			}
		}
	}

}
