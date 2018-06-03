package me.bartvv.uhcwar.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.AllArgsConstructor;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.Utils;

@AllArgsConstructor
public class PlayerQuitListener implements Listener {

	private UHCWar uhcWar;

	@EventHandler
	public void on(PlayerQuitEvent e) {
		e.setQuitMessage(Utils.tl("leave-Message", e.getPlayer().getName(), Bukkit.getOnlinePlayers().size()));
		this.uhcWar.getGameManager().removeUser(e.getPlayer().getName());
	}
}
