package me.bartvv.uhcwar.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.manager.GameManager.GameState;

@AllArgsConstructor
public class BlockListener implements Listener {

	private UHCWar uhcWar;

	@EventHandler
	public void on(BlockBreakEvent e) {
		if(this.uhcWar.getGameManager().getGameState() != GameState.STARTED) {
			e.setCancelled(true);
		}
		Block block = e.getBlock();
		Location location = block.getLocation();
		if (this.uhcWar.getGameManager().getBlockPlaced().contains(location)) {
			this.uhcWar.getGameManager().getBlockPlaced().remove(location);
		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void on(BlockPlaceEvent e) {
		if(this.uhcWar.getGameManager().getGameState() != GameState.STARTED) {
			e.setCancelled(true);
		}
		Block block = e.getBlock();
		Location location = block.getLocation();
		this.uhcWar.getGameManager().getBlockPlaced().add(location);
	}
}
