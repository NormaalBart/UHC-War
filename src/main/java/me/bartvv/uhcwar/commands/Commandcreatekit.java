package me.bartvv.uhcwar.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.Kit;
import me.bartvv.uhcwar.manager.User;

public class Commandcreatekit extends ICommand {

	@Override
	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		if (!user.getBase().hasPermission("uhc-war.createkit")) {
			user.sendMessage(Utils.tl("noPermission"));
			return;
		}

		if (args.length != 1) {
			user.sendMessage(Utils.tl("createkits.notEnoughArgs", commandLabel));
			return;
		}

		if (!(user.getBase() instanceof Player)) {
			user.sendMessage(Utils.tl("Only-Player"));
			return;
		}

		Player player = (Player) user.getBase();

		String kitName = args[0];
		ItemStack[] armour = player.getInventory().getArmorContents();
		ItemStack[] content = player.getInventory().getContents();

		getUhcWar().getKits().set("kits." + kitName.toLowerCase() + ".armour", armour);
		getUhcWar().getKits().set("kits." + kitName.toLowerCase() + ".content", content);

		getUhcWar().getKit().put(kitName.toLowerCase(), new Kit(armour, content));

		user.sendMessage(Utils.tl("createkits.kitCreated", kitName));
		return;
	}

}
