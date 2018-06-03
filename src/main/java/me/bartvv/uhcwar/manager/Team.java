package me.bartvv.uhcwar.manager;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Sets;

import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.manager.GameManager.Teams;

public class Team {

	private UHCWar uhcWar;
	private Teams teamEnum;
	private Set<String> members;
	private String teamName;
	private Location spawnLoc;

	public Team(String teamName, final Teams teamEnum, final UHCWar uhcWar) {
		this.teamEnum = teamEnum;
		this.teamName = teamName;
		this.members = Sets.newHashSet();
		this.uhcWar = uhcWar;
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					Location location = uhcWar.getData().getLocation("team." + teamEnum.toString().toLowerCase());
					spawnLoc = location;
				} catch (Exception exc) {
				}
			}
		}.runTaskLater(uhcWar, 2);
	}

	public Teams getTeamEnum() {
		return teamEnum;
	}

	public String getTeamName() {
		return teamName;
	}

	public Set<String> getMembers() {
		return members;
	}

	public void addMember(String name) {
		this.members.add(name);
		Player player = Bukkit.getPlayer(name);

		if (player != null) {
			player.setPlayerListName(
					uhcWar.getConfig().getString("teamName." + this.teamEnum.toString().toLowerCase() + "Prefix")
							+ name);
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
	}

	public void removeMember(String name) {
		this.members.remove(name);
	}

	public Location getSpawnLoc() {
		return spawnLoc;
	}

	public void setSpawnLoc(Location spawnLoc) {
		this.spawnLoc = spawnLoc;
	}

}
