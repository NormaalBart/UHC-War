package me.bartvv.uhcwar.scoreboard.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import me.bartvv.uhcwar.Teams;
import me.bartvv.uhcwar.manager.Team;
import me.bartvv.uhcwar.manager.User;
import me.bartvv.uhcwar.scoreboard.Glaedr;

/**
 * Copyright 2016 Alexander Maxwell Use and or redistribution of compiled JAR
 * file and or source code is permitted only if given explicit permission from
 * original author: Alexander Maxwell
 */
@Getter
public class PlayerScoreboard {

	private static Teams teams = JavaPlugin.getPlugin(Teams.class);

	private static Map<Player, PlayerScoreboard> scoreboards = Maps.newHashMap();

	public static PlayerScoreboard getScoreboard(Player player) {
		return scoreboards.get(player);
	}

	public static Collection<PlayerScoreboard> getScoreboards() {
		return scoreboards.values();
	}

	private Player player;
	@Setter
	private Objective objective;
	@Setter
	private Scoreboard scoreboard;
	private Map<Entry, String> keys;
	private Map<Entry, Integer> scores;
	private List<Entry> entries;
	private List<Wrapper> wrappers;
	private BukkitTask task;
	private boolean countup = false;

	public PlayerScoreboard(Glaedr main, Player player) {
		this.player = player;

		keys = new HashMap<>();
		scores = new HashMap<>();
		wrappers = new ArrayList<>();
		entries = new ArrayList<>();

		countup = main.isScoreCountUp();

		createScoreboard(main.getTitle(), main.isHook(), main.isOverrideTitle());

		for (int i = 0; i < main.getTopWrappers().size(); i++) {
			String string = main.getTopWrappers().get(i);
			new Wrapper("top_" + i, this, Wrapper.WrapperType.TOP).setText(string).send();
		}

		for (int i = 0; i < main.getBottomWrappers().size(); i++) {
			String string = main.getBottomWrappers().get(i);
			new Wrapper("bottom_" + i, this, Wrapper.WrapperType.BOTTOM).setText(string).send();
		}

		run();

		if (!scoreboards.containsKey(player)) {
			scoreboards.put(player, this);
		}
	}

	public String getAssignedKey(Entry entry) {
		if (keys.containsKey(entry)) {
			return keys.get(entry);
		}
		for (ChatColor color : ChatColor.values()) {

			String colorText = color + "" + ChatColor.WHITE;

			if (entry.getText().length() > 16) {
				String sub = entry.getText().substring(0, 16);
				colorText = colorText + ChatColor.getLastColors(sub);
			}

			if (!keys.values().contains(colorText)) {
				keys.put(entry, colorText);
				return colorText;
			}
		}
		throw new IndexOutOfBoundsException("No more keys available!");
	}

	public int getScore(Entry entry) {
		/*
		 * if (countup) { int start = 1 + getBottomWrappers().size(); int goal = 15 -
		 * getTopWrappers().size();
		 * 
		 * if (entry instanceof Wrapper) { Wrapper wrapper = (Wrapper) entry; if
		 * (wrapper.getType() == Wrapper.WrapperType.TOP) { goal = getEntries().size() +
		 * getBottomWrappers().size() + getTopWrappers().size() + 1; start =
		 * getEntries().size() + getBottomWrappers().size() + 1; } else { goal = start;
		 * start = start - getBottomWrappers().size(); } }
		 * 
		 * for (int i = start; i < goal; i++) { if (!(scores.containsKey(entry))) { if
		 * (!(scores.values().contains(i))) { scores.put(entry, i); return i; } } else {
		 * int score = scores.get(entry); for (int toSub = 0; toSub < start; toSub++) {
		 * if (i - toSub < score && !scores.values().contains(i - toSub)) { //TODO: Make
		 * this better scores.put(entry, i - toSub); return i - toSub; } } if (entry
		 * instanceof Wrapper && ((Wrapper) entry).getType() ==
		 * Wrapper.WrapperType.BOTTOM) { if (score > start) { scores.put(entry, start);
		 * return start; } } return score; } }
		 * 
		 * return 0; }
		 */
		int start = 15 - getTopWrappers().size();
		int goal = 0;

		if (entry instanceof Wrapper) {
			Wrapper wrapper = (Wrapper) entry;
			if (wrapper.getType() == Wrapper.WrapperType.TOP) {
				goal = start;
				start = 15;
			} else {
				start = start - getEntries().size();
				goal = start - getBottomWrappers().size();
			}
		}

		for (int i = start; i > goal; i--) {
			if (!(scores.containsKey(entry))) {
				if (!(scores.values().contains(i))) {
					scores.put(entry, i);
					return i;
				}
			} else {
				int score = scores.get(entry);
				for (int toSub = 0; toSub < start; toSub++) {
					if (i - toSub > score && !scores.values().contains(i - toSub)) { // TODO: Make this better
						scores.put(entry, i - toSub);
						return i - toSub;
					}
				}
				if (entry instanceof Wrapper && ((Wrapper) entry).getType() == Wrapper.WrapperType.BOTTOM) {
					if (score > start) {
						scores.put(entry, start);
						return start;
					}
				}
				return score;
			}
		}
		return 0;
	}

	public Entry getEntry(String id) {
		for (Entry entry : getEntries()) {
			if (entry.getId().equals(id)) {
				return entry;
			}
		}
		return null;
	}

	private List<Wrapper> getTopWrappers() {
		List<Wrapper> toReturn = new ArrayList<>();
		for (Wrapper wrapper : getWrappers()) {
			if (wrapper.getType() == Wrapper.WrapperType.TOP) {
				toReturn.add(wrapper);
			}
		}
		return toReturn;
	}

	private List<Wrapper> getBottomWrappers() {
		List<Wrapper> toReturn = new ArrayList<>();
		for (Wrapper wrapper : getWrappers()) {
			if (wrapper.getType() == Wrapper.WrapperType.BOTTOM) {
				toReturn.add(wrapper);
			}
		}
		return toReturn;
	}

	public void createScoreboard(String title, boolean hook, boolean overrideTitle) {
		scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		try {
			objective = scoreboard.registerNewObjective(player.getName(), "dummy");
		} catch (Exception exc) {
			objective = scoreboard.getObjective(player.getName());
		}
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(title);
		this.player.setScoreboard(scoreboard);
	}

	private void run() {
		task = new BukkitRunnable() {
			@Override
			public void run() {
				User user = teams.getUser(player.getName());
				if (scoreboard.getObjective(player.getName()) != null) {
					for (Entry entry : getEntries()) {
						String originalText = entry.getOriginalText();

						if (originalText.contains("%player_team%")) {
							if (user.getTeam() != null) {
								if (user.getTeam().getTeamName().equalsIgnoreCase("red")) {
									originalText = originalText.replace("%player_team%",
											ChatColor.DARK_RED + user.getTeam().getTeamName());
								} else {
									originalText = originalText.replace("%player_team%",
											ChatColor.BLUE + user.getTeam().getTeamName());
								}
							} else {
								originalText = originalText.replace("%player_team%", "None");
							}
						}

						if (originalText.contains("%player_team_size%")) {
							if (user.getTeam() != null) {
								originalText = originalText.replace("%player_team_size%",
										"" + user.getTeam().getMembers().size());
							} else {
								originalText = originalText.replace("%player_team_size%", "0");
							}
						}

						if (originalText.contains("%player_enemy_size%")) {
							if (user.getTeam() != null) {
								Team team = null;
								if (user.getTeam().getTeamName().equalsIgnoreCase("red")) {
									team = teams.getTeam("blue");
								} else {
									team = teams.getTeam("red");
								}
								originalText = originalText.replace("%player_enemy_size%",
										"" + team.getMembers().size());
							} else {
								originalText = originalText.replace("%player_enemy_size%", "None");
							}
						}
						entry.setText(originalText);
						entry.sendScoreboardUpdate(entry.getText());
					}
				}
			}
		}.runTaskTimer(Glaedr.getPlugin(), 2l, 2l);
	}

	public void stop() {
		if (task != null) {
			Bukkit.getScheduler().cancelTask(task.getTaskId());
		}
	}

	public void clearLines() {
		keys.clear();
		scores.clear();
		entries.clear();
		wrappers.clear();
	}

	public static void unload() {
		for(PlayerScoreboard board : scoreboards.values()) {
			board.stop();
		}
		scoreboards.clear();
	}

}
