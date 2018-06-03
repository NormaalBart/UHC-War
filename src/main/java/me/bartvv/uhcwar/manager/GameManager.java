package me.bartvv.uhcwar.manager;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.scoreboard.BoardManager;
import me.bartvv.uhcwar.scoreboard.PlayerBoard;

@Getter
public class GameManager {

	private transient UHCWar uhcWar;
	

	private transient int taskID = 0;
	@Setter
	private transient GameState gameState;
	private transient Set<Location> blockPlaced;
	private transient Map<String, User> userMap;
	private transient Map<Teams, Team> teams;
	private transient BoardManager boardManager;

	public GameManager(UHCWar uhcWar) {
		this.uhcWar = uhcWar;

		this.boardManager = new BoardManager(uhcWar);

		this.blockPlaced = Sets.newHashSet();

		this.teams = Maps.newHashMap();
		this.userMap = Maps.newHashMap();
		
		this.gameState = GameState.WAITING;

		this.teams.put(Teams.RED, new Team(uhcWar.getConfig().getString("teamName.red"), Teams.RED, uhcWar));
		this.teams.put(Teams.BLUE, new Team(uhcWar.getConfig().getString("teamName.blue"), Teams.BLUE, uhcWar));
		this.teams.put(Teams.SPECTATOR,
				new Team(uhcWar.getConfig().getString("teamName.spectator"), Teams.SPECTATOR, uhcWar));

		addUser("console");
	}

	public void start(final boolean override) {
		if (!override) {
			if (getUserMap().size() - 1 < uhcWar.getConfig().getInt("minPlayers")) {
				return;
			}
		}
		if (this.gameState != GameState.WAITING)
			return;

		this.gameState = GameState.STARTING;
		new BukkitRunnable() {
			int time = uhcWar.getConfig().getInt("countdownFrom", 10);

			@Override
			public void run() {
				if (getUserMap().size() - 1 < uhcWar.getConfig().getInt("minPlayers")) {
					this.cancel();
					gameState = GameState.WAITING;
					return;
				}

				if (time == 0) {
					gameState = GameState.STARTED;
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "autoteam");
					for (PlayerBoard board : getBoardManager().getBoards()) {
						board.updateTeams();
					}

					for (User user : getUserMap().values()) {
						for (String str : Utils.tlList("game.started")) {
							user.sendMessage(str);
						}
						if (!(user.getBase() instanceof Player))
							continue;
						Player player = (Player) user.getBase();
						player.getInventory().clear();
						player.getInventory().setArmorContents(null);
						player.setGameMode(GameMode.SURVIVAL);
						player.setFoodLevel(20);
						player.setHealth(player.getMaxHealth());
						player.setFireTicks(0);
						if (user.getTeam().getSpawnLoc() != null) {
							try {
								player.teleport(user.getTeam().getSpawnLoc());
							} catch (Exception exc) {
								getUhcWar().getLogger().log(Level.WARNING,
										"Failed to teleport " + player.getName() + " to team location!");
							}
						}

						Kit kit = getUhcWar().getKit().get(user.getTeam().getTeamEnum().toString().toLowerCase());
						if (kit != null) {
							kit.giveKit(player);
						} else {
							getUhcWar().getLogger().log(Level.WARNING, "Failed to give kit to " + player.getName() + "("
									+ user.getTeam().getTeamEnum().toString().toLowerCase() + ")");
						}
					}
					taskID = new BukkitRunnable() {
						@Override
						public void run() {
							for (Player player : Bukkit.getOnlinePlayers()) {
								User user = getUser(player.getName());
								Player nearest = null;
								Double distance = Double.MAX_VALUE;
								for (Player toCheck : Bukkit.getOnlinePlayers()) {
									User target = getUser(toCheck.getName());
									if (target.getTeam() != null && user.getTeam() != null
											&& target.getTeam().getTeamEnum() != user.getTeam().getTeamEnum()) {
										Double distanceCheck = toCheck.getLocation()
												.distanceSquared(player.getLocation());
										if (distanceCheck < distance) {
											distance = distanceCheck;
											nearest = toCheck;
										}
									}
								}
								if (nearest != null) {
									player.setCompassTarget(nearest.getLocation());
								}
							}
							new BukkitRunnable() {
								@Override
								public void run() {
									stop();
								}
							}.runTask(uhcWar);
						}
					}.runTaskTimerAsynchronously(uhcWar, 0, 20).getTaskId();
					cancel();
					return;
				}

				for (String str : Utils.tlList("game.starting", time)) {
					for (User user : getUserMap().values()) {
						user.sendMessage(str);
					}
				}
				time--;
			}
		}.runTaskTimer(this.uhcWar, 0, 20);
	}

	public void stop() {
		if (this.teams.get(Teams.RED).getMembers().size() > 0 && this.teams.get(Teams.BLUE).getMembers().size() > 0) {
			return;
		}
		Bukkit.getScheduler().cancelTask(this.taskID);

		if (this.teams.get(Teams.RED).getMembers().size() == 0 && this.teams.get(Teams.BLUE).getMembers().size() == 0) {
			for (User user : getUserMap().values()) {
				for (String str : Utils.tlList("game.finished.draw")) {
					user.sendMessage(str);
				}
			}
		} else {
			gameState = GameState.STOPPING;
			Team team;
			if (this.teams.get(Teams.RED).getMembers().size() == 0) {
				team = this.teams.get(Teams.BLUE);
			} else {
				team = this.teams.get(Teams.RED);
			}
			for (User user : getUserMap().values()) {
				for (String str : Utils.tlList("game.finished.won", team.getTeamName())) {
					user.sendMessage(str);
				}
			}
			new BukkitRunnable() {

				@Override
				public void run() {
					if (uhcWar.getConfig().getString("fallBack-server").equalsIgnoreCase("none")) {
						Bukkit.shutdown();
						return;
					}
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(uhcWar.getConfig().getString("fallBack-server"));
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.sendPluginMessage(uhcWar, "BungeeCord", out.toByteArray());
					}

					new BukkitRunnable() {

						@Override
						public void run() {
							Bukkit.shutdown();
						}
					}.runTaskLater(uhcWar, 20);
				}
			}.runTaskLater(uhcWar, uhcWar.getConfig().getInt("time", 10) * 20);
		}
	}

	public User getUser(String name) {
		return this.userMap.get(name.toLowerCase());
	}

	public void removeUser(String name) {
		User user = getUser(name);

		if (user.getTeam() != null) {
			user.getTeam().removeMember(user.getName());
		}

		this.userMap.remove(name.toLowerCase(), user);
		stop();
	}

	public void addUser(String name) {
		this.userMap.put(name.toLowerCase(), new User(name));
		
		System.out.println(this.gameState.name());
		
		if (this.gameState == GameState.STARTED || this.gameState == GameState.STOPPING) {
			userDeath(getUser(name));
		} else {
			if ((getUserMap().size() - 1) >= uhcWar.getConfig().getInt("minPlayers")) {
				start(true);
			}
		}
	}

	public void userDeath(User user) {
		if (this.gameState == GameState.WAITING)
			return;

		if (user.getTeam() != null) {
			user.getTeam().removeMember(user.getName());
			user.setTeam(null);
		}
		user.setTeam(getTeam(Teams.SPECTATOR));
		teams.get(Teams.SPECTATOR).addMember(user.getName());
		if (user.getBase() instanceof Player) {
			Player player = (Player) user.getBase();
			for (Player players : Bukkit.getOnlinePlayers()) {
				player.showPlayer(players);
				User target = getUser(players.getName());
				if (target.getTeam() != null && target.getTeam().getTeamEnum() != Teams.SPECTATOR) {
					players.hidePlayer(player);
				}
			}
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFireTicks(0);

			getBoardManager().deleteBoard(player);
			getBoardManager().createBoard(player, "deathBoard");

			try {
				Kit kit = getUhcWar().getKit().get(user.getTeam().getTeamEnum().toString().toLowerCase());
				kit.giveKit(player);
			} catch (Exception exc) {
			}
		}
		for (PlayerBoard board : getBoardManager().getBoards()) {
			board.updateTeams();
		}
	}

	public Team getTeam(String team) {
		switch (team.toLowerCase()) {
		case "red":
			return this.teams.get(Teams.RED);
		case "blue":
			return this.teams.get(Teams.BLUE);
		case "spectator":
			return this.teams.get(Teams.SPECTATOR);
		default:
			return null;
		}
	}

	public Team getTeam(Teams team) {
		return this.teams.get(team);
	}

	public enum Teams {
		RED, BLUE, SPECTATOR;
	}

	public enum GameState {
		WAITING, STARTING, STARTED, STOPPING;
	}
}
