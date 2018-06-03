package me.bartvv.uhcwar.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.Team;
import me.bartvv.uhcwar.manager.User;

public class Commandlist extends ICommand {

	@Override
	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		if (args.length != 1) {
			user.sendMessage(Utils.tl("list.notEnoughArgs", commandLabel));
			return;
		}
		Team team = getUhcWar().getGameManager().getTeam(args[1]);
		if (team == null) {

			Player target = Bukkit.getPlayer(args[1]);
			if (target != null) {
				team = getUhcWar().getGameManager().getUser(target.getName()).getTeam();
			}

			if (team == null) {
				user.sendMessage(tl("teams.notFound", args[1]));
				return;
			}
		}
		String teamMembers = String.join(", ", team.getMembers());

		if (teamMembers == null || teamMembers.equalsIgnoreCase("")) {
			teamMembers = "None";
		}

		for (String str : Utils.tlList("teams.team-info", team.getTeamName(), teamMembers, team.getMembers().size())) {
			user.sendMessage(str);
		}
		return;
	}
}
