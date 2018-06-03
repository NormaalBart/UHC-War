package me.bartvv.uhcwar.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.Team;
import me.bartvv.uhcwar.manager.User;

public class Commandteam extends ICommand {

	@Override
	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		if (args.length == 0) {
			user.sendMessage(tl("teams.notEnoughArgs", commandLabel));
			return;
		}
		if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("info")) {
			if (args.length != 2) {
				user.sendMessage(tl("teams.notEnoughArgs", commandLabel));
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

			for (String str : Utils.tlList("teams.team-info", team.getTeamName(), teamMembers,
					team.getMembers().size())) {
				user.sendMessage(str);
			}
			return;
		} else if (args[0].equalsIgnoreCase("join")) {
			if (args.length != 2) {
				user.sendMessage(tl("teams.notEnoughArgs", commandLabel));
				return;
			}
			if (!(user.getBase() instanceof Player)) {
				user.sendMessage(tl("Only-Player"));
				return;
			}

			if (!getUhcWar().isCanSwitch()) {
				user.sendMessage(tl("cannot-switch"));
				return;
			}

			Team team = getUhcWar().getGameManager().getTeam(args[1]);
			if (team == null) {
				user.sendMessage(tl("teams.notFound", args[1]));
				return;
			}

			if (user.getTeam() != null) {
				if (user.getTeam().getTeamEnum() == team.getTeamEnum()) {
					user.sendMessage(tl("teams.already-joined"));
					return;
				} else {
					user.getTeam().removeMember(user.getName());
				}
			}

			if (team.getMembers().size() >= getUhcWar().getConfig().getInt("maxTeamLimit")) {
				user.sendMessage(tl("teams.max-members"));
				return;
			}

			team.addMember(user.getName());
			user.setTeam(team);
			user.sendMessage(tl("teams.joined", team.getTeamName()));
			return;
		} else if (args[0].equalsIgnoreCase("setspawn")) {
			if (args.length != 2) {
				user.sendMessage(tl("teams.notEnoughArgs", commandLabel));
				return;
			}
			if (!user.getBase().hasPermission("teamssetspawn")) {
				user.sendMessage(tl("noPermission"));
				return;
			}

			if (!(user.getBase() instanceof Player)) {
				user.sendMessage(tl("Only-Player"));
				return;
			}

			Team team = getUhcWar().getGameManager().getTeam(args[1]);
			if (team == null) {
				user.sendMessage(tl("teamsnotFound", args[1]));
				return;
			}
			Player player = (Player) user.getBase();
			team.setSpawnLoc(player.getLocation());
			user.sendMessage(tl("spawn-set", team.getTeamName()));
			getUhcWar().getData().set("team." + team.getTeamEnum().toString().toLowerCase(), player.getLocation(),
					true);
			return;
		} else if (args[0].equalsIgnoreCase("leave")) {
			if (args.length != 2) {
				user.sendMessage(tl("teamsnotEnoughArgs", commandLabel));
				return;
			}
			if (!(user.getBase() instanceof Player)) {
				user.sendMessage(tl("Only-Player"));
				return;
			}

			Player player = (Player) user.getBase();
			Team team = getUhcWar().getGameManager().getTeam(args[1]);
			if (team == null) {
				user.sendMessage(tl("teamsnotFound", args[1]));
				return;
			}

			team.removeMember(player.getName());
			user.setTeam(null);
			user.sendMessage(tl("teamsteam-left"));
			return;
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (args.length != 1) {
				user.sendMessage(tl("teamsnotEnoughArgs", commandLabel));
				return;
			}
			if (!user.getBase().hasPermission("teamsreload")) {
				user.sendMessage(tl("noPermission"));
				return;
			}
			getUhcWar().reloadConfig();
			user.sendMessage(tl("Reloaded"));
			return;
		} else if (args[0].equalsIgnoreCase("debug")) {
			if (!user.getBase().hasPermission("teamsdebug")) {
				user.sendMessage(tl("noPermission"));
				return;
			}
			getUhcWar().setDebug(!getUhcWar().isDebug());
			user.sendMessage("Debug mode " + getUhcWar().isDebug());
			return;

		}
		user.sendMessage(tl("teamsnotEnoughArgs", commandLabel));
		return;
	}

	@Override
	public List<String> onTabComplete(User user, String commandLabel, String[] args) throws Exception {
		List<String> possibilities = Lists.newArrayList();
		if (args.length == 1) {
			possibilities.add("list");
			possibilities.add("join");
			possibilities.add("info");
			if (user.getBase().hasPermission("teamssetspawn")) {
				possibilities.add("setspawn");
			}
			possibilities.add("leave");
			if (user.getBase().hasPermission("teamsreload")) {
				possibilities.add("reload");
			}

		} else if (args.length == 2) {
			if ((args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("reload")))
				return possibilities;
			List<String> teamNames = Arrays.asList("red", "blue", "spectator");
			for (String teamName : teamNames) {
				if (teamName.toLowerCase().startsWith(args[1].toLowerCase())) {
					possibilities.add(teamName);
				}
			}
		}
		return possibilities;
	}

}