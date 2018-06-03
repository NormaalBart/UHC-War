package me.bartvv.uhcwar.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobSpawnListener implements Listener {

	@EventHandler
	public void on(EntitySpawnEvent e) {
		if (e.getEntity() instanceof Player)
			return;
		e.setCancelled(true);
	}

}
