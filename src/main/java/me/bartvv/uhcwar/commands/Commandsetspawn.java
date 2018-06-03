package me.bartvv.uhcwar.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.User;

public class Commandsetspawn extends ICommand {

	@Override
	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		if (!user.getBase().hasPermission("uhc-war.setspawn")) {
			user.sendMessage(Utils.tl("noPermission"));
			return;
		}
		
		if(!(user.getBase() instanceof Player)) {
			user.sendMessage(Utils.tl("Only-Player"));
			return;
		}
		
		Player player = (Player)user.getBase();
		Location loc = player.getLocation();
		
		getUhcWar().getData().set("spawn", loc);
		
		user.sendMessage(Utils.tl("spawn-location-set"));
		return;
	}

}
