package me.bartvv.uhcwar.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.bartvv.uhcwar.Teams;
import me.bartvv.uhcwar.manager.User;
import me.bartvv.uhcwar.scoreboard.scoreboard.PlayerScoreboard;

/**
 * Copyright 2016 Alexander Maxwell Use and or redistribution of compiled JAR
 * file and or source code is permitted only if given explicit permission from
 * original author: Alexander Maxwell
 */
@Getter
public class Glaedr implements Listener {

	private static JavaPlugin plugin;
	private String title;
	private boolean hook, overrideTitle, scoreCountUp;
	private List<String> bottomWrappers, topWrappers;

	public Glaedr(JavaPlugin plugin, String title, boolean hook, boolean overrideTitle, boolean scoreCountUp) {
		Glaedr.plugin = plugin;
		this.title = ChatColor.translateAlternateColorCodes('&', title);
		this.hook = hook;
		this.overrideTitle = overrideTitle;
		this.scoreCountUp = scoreCountUp;

		bottomWrappers = new ArrayList<>();
		topWrappers = new ArrayList<>();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public Glaedr(JavaPlugin plugin, String title) {
		this(plugin, title, false, true, false);
	}

	public void registerPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			User user = ((Teams) plugin).getUser(player.getName());
			if (user.getTeam() != null) {
				new PlayerScoreboard(this, player);
			}
		}
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}
}