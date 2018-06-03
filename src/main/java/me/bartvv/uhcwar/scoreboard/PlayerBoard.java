package me.bartvv.uhcwar.scoreboard;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;

import lombok.Getter;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.interfaces.IPlaceHolder;
import me.bartvv.uhcwar.manager.GameManager.Teams;
import me.bartvv.uhcwar.manager.User;

public class PlayerBoard {

	private transient UHCWar uhcWar;
	private transient FileConfiguration config;
	private transient Scoreboard scoreboard;
	private transient Objective objective;
	private transient Set<IPlaceHolder> placeholders;
	private transient int c;
	@Getter
	private transient Player player;
	private transient List<String> cache = Lists.newArrayList();
	private transient List<Line> lines = Lists.newArrayList();
	@Getter
	private transient int taskID;

	public PlayerBoard(final Player player, final UHCWar uhcWar, Set<IPlaceHolder> placeholders, String path) {
		this.uhcWar = uhcWar;
		this.config = uhcWar.getScoreboard();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		this.player = player;
		this.placeholders = placeholders;

		c = (config.getConfigurationSection(path).getKeys(false).size() - 1);

		objective = scoreboard.registerNewObjective(player.getName(), "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("");

		int score = c;
		int count = 0;
		for (String key : config.getConfigurationSection(path).getKeys(false)) {
			int keyInt = 1;
			try {
				keyInt = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if (keyInt != 1) {
				Team t;
				if (scoreboard.getTeam(count + "") == null) {
					t = scoreboard.registerNewTeam(count + "");
				} else {
					t = scoreboard.getTeam(count + "");
					Iterator<String> it = t.getEntries().iterator();
					while (it.hasNext()) {
						t.removeEntry(it.next());
					}
				}

				t.addEntry(String.valueOf(org.bukkit.ChatColor.values()[count]));

				objective.getScore(String.valueOf(org.bukkit.ChatColor.values()[count])).setScore(score);
				count++;
				score--;
			}
			Line l = new Line(this.uhcWar.getGameManager().getUser(player.getName()), key, this.placeholders,
					this.uhcWar, path);
			lines.add(l);
		}

		Team red = scoreboard.registerNewTeam("red");
		red.setPrefix(uhcWar.getConfig().getString("teamName.redPrefix"));
		Team blue = scoreboard.registerNewTeam("blue");
		blue.setPrefix(uhcWar.getConfig().getString("teamName.bluePrefix"));
		Team spectator = scoreboard.registerNewTeam("spectator");
		spectator.setPrefix(uhcWar.getConfig().getString("teamName.spectatorPrefix"));

		this.player.setScoreboard(scoreboard);

		update();
	}

	private void setTitle(String s) {
		this.objective.setDisplayName(s);
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	public void updateTeams() {
		Team red = scoreboard.getTeam("red");
		Team blue = scoreboard.getTeam("blue");
		Team spectator = scoreboard.getTeam("spectator");
		for (User user : uhcWar.getGameManager().getUserMap().values()) {
			if (user.getBase() instanceof Player) {
				Player player = (Player) user.getBase();
				if (user.getTeam() != null) {
					if (user.getTeam().getTeamEnum() == Teams.RED) {
						if (!red.hasPlayer(player)) {
							red.addPlayer(player);
						}
						blue.removePlayer(player);
						spectator.removePlayer(player);
					} else if (user.getTeam().getTeamEnum() == Teams.BLUE) {
						if (!blue.hasPlayer(player)) {
							blue.addPlayer(player);
						}
						red.removePlayer(player);
						spectator.removePlayer(player);
					} else {
						red.removePlayer(player);
						blue.removePlayer(player);
						if (!spectator.hasPlayer(player)) {
							spectator.addPlayer(player);
						}
					}
				} else {
					red.removePlayer(player);
					blue.removePlayer(player);
					if (!spectator.hasPlayer(player)) {
						spectator.addPlayer(player);
					}
				}
			}
		}
	}

	private void update() {
		taskID = new BukkitRunnable() {

			@Override
			public void run() {
				List<String> newCache = Lists.newArrayList();

				int count = 0;
				boolean first = true;
				for (Line line : lines) {
					String string = line.next();
					if (first) {
						setTitle(ChatColor.translateAlternateColorCodes('&', string));
						first = false;
					} else {
						newCache.add(string);
						if (cache.contains(string)) {
							count++;
							continue;
						} else {
							final String prefix, suffix;
							final int finalCount = count;
							if (string.length() > 16) {
								prefix = string.substring(0, 16);
								String lastColors = "";
								for (int i = 0; i < 16; i++) {
									char c = string.charAt(i);
									if (c == '&') {
										lastColors = lastColors + "&" + string.charAt(i + 1);
									}
								}
								String preSuffix = lastColors
										+ string.substring(16, string.length() > 32 ? 32 : string.length());
								suffix = preSuffix.length() > 16 ? preSuffix.substring(0, 16) : preSuffix;
							} else {
								prefix = string;
								suffix = "";
							}
							Team t = scoreboard.getTeam(finalCount + "");
							t.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
							t.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));
							count++;
						}
					}
				}
				cache = newCache;
			}
		}.runTaskTimerAsynchronously(uhcWar, 0, 1).getTaskId();
	}
}