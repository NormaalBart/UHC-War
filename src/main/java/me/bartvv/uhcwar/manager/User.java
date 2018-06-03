package me.bartvv.uhcwar.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

	private String name;
	private Team team;
	private CommandSender base;
	private int kills;

	public User(String name) {
		this.name = name;
		this.kills = 0;
		if (this.name.equalsIgnoreCase("console")) {
			this.base = Bukkit.getConsoleSender();
		} else {
			this.base = Bukkit.getPlayer(name);
		}
	}

	public void sendMessage(String message) {
		getBase().sendMessage(message);
	}
}
