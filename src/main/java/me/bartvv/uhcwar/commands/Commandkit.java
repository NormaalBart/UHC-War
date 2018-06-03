package me.bartvv.uhcwar.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.Kit;
import me.bartvv.uhcwar.manager.User;
import me.bartvv.uhcwar.manager.GameManager.GameState;

public class Commandkit extends ICommand {

	@Override
	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		if (args.length < 1 || args.length > 2) {
			user.sendMessage(Utils.tl("kit.notEnoughArgs", commandLabel));
			return;
		}

		if (!user.getBase().hasPermission("uhc-war.kit")) {
			user.sendMessage(Utils.tl("noPermission"));
			return;
		}

		if (getUhcWar().getGameManager().getGameState() != GameState.WAITING
				&& getUhcWar().getGameManager().getGameState() != GameState.STARTING)
			return;

		String kitName = args[0];
		Player player;

		if (args.length == 2) {
			player = Bukkit.getPlayer(args[1]);
		} else {
			if (user.getBase() instanceof Player) {
				player = (Player) user.getBase();
			} else {
				user.sendMessage(Utils.tl("kit.notEnoughArgs", commandLabel));
				return;
			}
		}

		if (player == null) {
			user.sendMessage(Utils.tl("playerNotFound"));
			return;
		}

		Kit kit = getUhcWar().getKit().get(kitName.toLowerCase());

		if (kit == null) {
			user.sendMessage(Utils.tl("kit.kitNotFound"));
			return;
		}

		kit.giveKit(player);

		user.sendMessage(Utils.tl("kit.kitGive", kitName, player.getName()));
		return;
	}

}