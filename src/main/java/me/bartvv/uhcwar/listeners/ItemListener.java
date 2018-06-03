package me.bartvv.uhcwar.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.manager.GameManager.GameState;
import me.bartvv.uhcwar.manager.GameManager.Teams;
import me.bartvv.uhcwar.manager.User;

@AllArgsConstructor
public class ItemListener implements Listener{

	private UHCWar uhcWar;
	
	@EventHandler
	public void on(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		User user = uhcWar.getGameManager().getUser(player.getName());
		
		if(this.uhcWar.getGameManager().getGameState() != GameState.STARTED && user.getTeam() != null && user.getTeam().getTeamEnum() == Teams.SPECTATOR) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		User user = uhcWar.getGameManager().getUser(player.getName());
		
		if(this.uhcWar.getGameManager().getGameState() != GameState.STARTED && user.getTeam() != null && user.getTeam().getTeamEnum() == Teams.SPECTATOR) {
			e.setCancelled(true);
		}
	}
}
