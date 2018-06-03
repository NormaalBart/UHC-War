package me.bartvv.uhcwar.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.manager.Team;
import me.bartvv.uhcwar.manager.GameManager.Teams;

@AllArgsConstructor
public class PlayerHunger implements Listener {

	private UHCWar uhcWar;

	@EventHandler
	public void on(FoodLevelChangeEvent e) {
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
