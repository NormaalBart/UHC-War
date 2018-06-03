package me.bartvv.uhcwar.commands;

import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.GameManager.Teams;
import me.bartvv.uhcwar.manager.Team;
import me.bartvv.uhcwar.manager.User;

public class Commandautoteam extends ICommand {

	@Override
	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		if (!user.getBase().hasPermission("getUhcWar().autoteam")) {
			user.sendMessage(tl("noPermission"));
			return;
		}

		int size = sortPlayers();

		user.sendMessage(tl("Teams-Sorted", size));
		getUhcWar().setCanSwitch(false);
	}

	public int sortPlayers() {
		Set<User> toSort = Sets.newHashSet();

		for (User user : getUhcWar().getGameManager().getUserMap().values()) {
			if (user.getBase() instanceof Player) {
				if (user.getTeam() == null) {
					toSort.add(user);
				}
			}
		}

		List<Team> teams = Lists.newArrayList(getUhcWar().getGameManager().getTeam(Teams.RED),
				getUhcWar().getGameManager().getTeam(Teams.BLUE));
		for (User userToSort : toSort) {
			if (userToSort.getTeam() == null) {
				Team lowestMembers = null;
				int lowestCount = Integer.MAX_VALUE;
				for (Team team : teams) {
					if (team.getMembers().size() <= lowestCount) {
						lowestCount = team.getMembers().size();
						lowestMembers = team;
					}
				}

				if (lowestMembers != null) {
					Utils.debug("User " + userToSort.getName() + " joined the team "
							+ lowestMembers.getTeamEnum().toString().toLowerCase());
					lowestMembers.addMember(userToSort.getName());
					userToSort.setTeam(lowestMembers);
				}
			}
		}

		for (User user : getUhcWar().getGameManager().getUserMap().values()) {
			if (user.getBase() instanceof Player) {
				Player player = (Player) user.getBase();
				getUhcWar().getGameManager().getBoardManager().createBoard(player, "aliveBoard");
			}
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				List<String> tab = Lists.newArrayList();
				for (String player : getUhcWar().getGameManager().getTeam(Teams.RED).getMembers()) {
					tab.add(player);
				}

				while (!(tab.size() >= 20)) {
					tab.add("   ");
				}

				for (String player : getUhcWar().getGameManager().getTeam(Teams.BLUE).getMembers()) {
					tab.add(player);
				}

				while (!(tab.size() >= 40)) {
					tab.add("   ");
				}

				for (User user : getUhcWar().getGameManager().getUserMap().values()) {
					if (user.getBase() instanceof Player) {
						Player player = (Player) user.getBase();
						User targetUser = getUhcWar().getGameManager().getUser(player.getName());
						if (targetUser.getTeam() != null && targetUser.getTeam().getTeamEnum() == Teams.SPECTATOR) {

							for (String players : getUhcWar().getGameManager().getTeam(Teams.SPECTATOR).getMembers()) {
								tab.add(players);
							}

							while (!(tab.size() >= 60)) {
								tab.add("   ");
							}
						}
						getUhcWar().getTabList().setCustomTablist(player, tab);
						getUhcWar().getTabList().refreshCustomTablist(player, tab);
					}
				}
			}
		}.runTaskTimer(getUhcWar(), 0, 20);
		return toSort.size();
	}
}
