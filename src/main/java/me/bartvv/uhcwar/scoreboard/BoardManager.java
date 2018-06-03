package me.bartvv.uhcwar.scoreboard;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.interfaces.IPlaceHolder;
import me.bartvv.uhcwar.manager.GameManager.Teams;
import me.bartvv.uhcwar.manager.User;

public class BoardManager {

	private Set<IPlaceHolder> placeholders;
	private Map<Player, PlayerBoard> boards;
	private UHCWar uhcWar;

	public BoardManager(final UHCWar uhcWar) {
		this.uhcWar = uhcWar;
		this.boards = Maps.newHashMap();

		this.placeholders = Sets.newHashSet();

		this.placeholders.add(new me.bartvv.uhcwar.interfaces.IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				String team;
				if (user.getTeam() != null) {
					team = user.getTeam().getTeamName();
				} else {
					team = "None";
				}
				return message.replace(getPlaceholder(), team);
			}

			@Override
			public String getPlaceholder() {
				return "%player_team%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				int size;
				if (user.getTeam() != null) {
					size = user.getTeam().getMembers().size();
				} else {
					size = 0;
				}
				return message.replace(getPlaceholder(), "" + size);
			}

			@Override
			public String getPlaceholder() {
				return "%player_team_size%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				if (user.getTeam() == null) {
					return message.replace(getPlaceholder(), "Error");
				}

				if (user.getTeam().getTeamEnum() == Teams.RED) {
					return message.replace(getPlaceholder(), uhcWar.getGameManager().getTeam(Teams.BLUE).getTeamName());
				} else if (user.getTeam().getTeamEnum() == Teams.BLUE) {
					return message.replace(getPlaceholder(), uhcWar.getGameManager().getTeam(Teams.RED).getTeamName());
				} else if (user.getTeam().getTeamEnum() == Teams.SPECTATOR) {
					return message.replace(getPlaceholder(),
							uhcWar.getGameManager().getTeam(Teams.SPECTATOR).getTeamName());
				} else {
					return message.replace(getPlaceholder(), "Error");
				}
			}

			@Override
			public String getPlaceholder() {
				return "%player_enemy%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				if (user.getTeam() == null || user.getTeam().getTeamEnum() == Teams.SPECTATOR) {
					return message.replace(getPlaceholder(), "None");
				}

				if (user.getTeam().getTeamEnum() == Teams.RED) {
					return message.replace(getPlaceholder(),
							"" + uhcWar.getGameManager().getTeam(Teams.BLUE).getMembers().size());
				} else if (user.getTeam().getTeamEnum() == Teams.BLUE) {
					return message.replace(getPlaceholder(),
							"" + uhcWar.getGameManager().getTeam(Teams.RED).getMembers().size());
				} else {
					return message.replace(getPlaceholder(), "Error");
				}
			}

			@Override
			public String getPlaceholder() {
				return "%player_enemy_size%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(), "" + uhcWar.getGameManager().getTeam(Teams.RED).getTeamName());
			}

			@Override
			public String getPlaceholder() {
				return "%team_red%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(),
						"" + uhcWar.getGameManager().getTeam(Teams.BLUE).getTeamName());
			}

			@Override
			public String getPlaceholder() {
				return "%team_blue%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(),
						"" + uhcWar.getGameManager().getTeam(Teams.SPECTATOR).getTeamName());
			}

			@Override
			public String getPlaceholder() {
				return "%team_spectator%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(),
						"" + uhcWar.getGameManager().getTeam(Teams.RED).getMembers().size());
			}

			@Override
			public String getPlaceholder() {
				return "%team_red_size%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(),
						"" + uhcWar.getGameManager().getTeam(Teams.BLUE).getMembers().size());
			}

			@Override
			public String getPlaceholder() {
				return "%team_blue_size%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(),
						"" + uhcWar.getGameManager().getTeam(Teams.SPECTATOR).getMembers().size());
			}

			@Override
			public String getPlaceholder() {
				return "%team_spectator_size%";
			}
		});

		this.placeholders.add(new IPlaceHolder() {

			@Override
			public String replacePlaceHolder(User user, String message) {
				return message.replace(getPlaceholder(), "" + user.getKills());
			}

			@Override
			public String getPlaceholder() {
				return "%player_kills%";
			}
		});

	}

	public PlayerBoard getBoard(Player player) {
		return this.boards.get(player);
	}

	public PlayerBoard createBoard(Player player, String string) {
		this.boards.put(player, new PlayerBoard(player, this.uhcWar, this.placeholders, string));
		return getBoard(player);
	}

	public void deleteBoard(Player player) {
		PlayerBoard board = getBoard(player);
		if (board != null) {
			Bukkit.getScheduler().cancelTask(board.getTaskID());
		}
		this.boards.remove(player, board);
	}

	public Collection<PlayerBoard> getBoards() {
		return boards.values();
	}
}
