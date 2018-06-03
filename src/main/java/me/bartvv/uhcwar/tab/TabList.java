package me.bartvv.uhcwar.tab;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.manager.User;
import me.bartvv.uhcwar.tab.packets.PacketHandler;

public class TabList {
	UHCWar plugin;
	PacketHandler packet;
	private static TabList instance;
	private ArrayList<String> tablistSlots = Lists.newArrayList();
	private Map<Player, List<String>> playerTablists = Maps.newHashMap();
	private Map<Player, List<String>> playerCustomTablists = Maps.newHashMap();

	public TabList(UHCWar plugin) {
		instance = this;
		this.plugin = plugin;
		this.packet = new PacketHandler(plugin);
	}

	public void setTablist(Player player) {
		if ((!this.playerCustomTablists.containsKey(player)) && (!this.playerTablists.containsKey(player))) {
			List<String> tablist = Lists.newArrayList();
			for (String string : this.tablistSlots) {
				tablist.add(string);
			}
			for (int i = 0; i < tablist.size(); i++) {
				String slot = (String) tablist.get(i);
				slot = userValues(slot, player);
				slot = textValues(slot);
				slot = editText(slot);

				tablist.set(i, slot);
			}
			List<Object> packets = Lists.newArrayList();

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), false));
				}
			}

			for (String s : tablist) {
				packets.add(this.packet.createTablistPacket(s, true));
			}
			this.playerTablists.put(player, tablist);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), true, getPing(p)));
				}
			}
			this.packet.sendPackets(player, packets);
		}
	}

	public void refreshTablist(Player player) {
		if ((!this.playerCustomTablists.containsKey(player)) && (this.playerTablists.containsKey(player))) {
			List<String> tablist = Lists.newArrayList();
			for (String string : this.tablistSlots) {
				tablist.add(string);
			}
			for (int i = 0; i < tablist.size(); i++) {
				String slot = (String) tablist.get(i);
				slot = userValues(slot, player);
				slot = textValues(slot);
				slot = editText(slot);

				tablist.set(i, slot);
			}
			List<Object> packets = Lists.newArrayList();
			for (String s : this.playerTablists.get(player)) {
				packets.add(this.packet.createTablistPacket(s, false));
			}
			this.playerTablists.remove(player);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), false));
				}
			}
			for (String s : tablist) {
				packets.add(this.packet.createTablistPacket(s, true));
			}
			this.playerTablists.put(player, tablist);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), true, getPing(p)));
				}
			}
			this.packet.sendPackets(player, packets);
		}
	}

	public void removeTablist(Player player) {
		if ((!this.playerCustomTablists.containsKey(player)) && (this.playerTablists.containsKey(player))) {
			List<String> tablist = Lists.newArrayList();
			for (String string : this.tablistSlots) {
				tablist.add(string);
			}
			for (int i = 0; i < tablist.size(); i++) {
				String slot = (String) tablist.get(i);
				slot = userValues(slot, player);
				slot = textValues(slot);
				slot = editText(slot);

				tablist.set(i, slot);
			}
			List<Object> packets = Lists.newArrayList();
			for (String slot : this.playerTablists.get(player)) {
				packets.add(this.packet.createTablistPacket(slot, false));
			}
			this.packet.sendPackets(player, packets);
			this.playerTablists.remove(player);
		}
	}

	public void setCustomTablist(Player player, List<String> slots) {
		if (!this.playerCustomTablists.containsKey(player)) {
			List<String> tablist = Lists.newArrayList();
			for (String string : slots) {
				tablist.add(string);
			}
			for (int i = 0; i < tablist.size(); i++) {
				String slot = (String) tablist.get(i);
				slot = userValues(slot, player);
				slot = textValues(slot);
				slot = editText(slot);

				tablist.set(i, slot);
			}
			List<Object> packets = Lists.newArrayList();

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), false));
				}
			}
			for (String s : tablist) {
				packets.add(this.packet.createTablistPacket(s, true));
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), true, getPing(p)));
				}
			}
			this.playerCustomTablists.put(player, tablist);
			this.packet.sendPackets(player, packets);
		}
	}

	public void refreshCustomTablist(Player player, List<String> slots) {
		if (this.playerCustomTablists.containsKey(player)) {
			List<String> tablist = Lists.newArrayList();
			for (String string : slots) {
				tablist.add(string);
			}
			for (int i = 0; i < tablist.size(); i++) {
				String slot = (String) tablist.get(i);
				slot = userValues(slot, player);
				slot = textValues(slot);
				slot = editText(slot);

				tablist.set(i, slot);
			}
			List<Object> packets = Lists.newArrayList();
			for (String s : this.playerCustomTablists.get(player)) {
				packets.add(this.packet.createTablistPacket(s, false));
			}
			this.packet.sendPackets(player, packets);
			this.playerCustomTablists.remove(player);

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), false));
				}
			}
			for (String s : tablist) {
				packets.add(this.packet.createTablistPacket(s, true));
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), true, getPing(p)));
				}
			}
			this.playerCustomTablists.put(player, tablist);
			this.packet.sendPackets(player, packets);
		}
	}

	public void removeCustomTablist(Player player) {
		if (this.playerCustomTablists.containsKey(player)) {
			List<Object> packets = Lists.newArrayList();
			for (String s : this.playerCustomTablists.get(player)) {
				packets.add(this.packet.createTablistPacket(s, false));
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (player.canSee(p)) {
					packets.add(this.packet.createTablistPacket(p.getPlayerListName(), true, getPing(p)));
				}
			}
			this.packet.sendPackets(player, packets);
			this.playerCustomTablists.remove(player);
		}
	}

	public void loadTablist(List<String> tablist) {
		for (String s : tablist) {
			this.tablistSlots.add(s);
		}
	}

	public List<String> getTablist() {
		return this.tablistSlots;
	}

	public Map<Player, List<String>> getPlayerTablists() {
		return this.playerTablists;
	}

	public Map<Player, List<String>> getCustomTablists() {
		return this.playerCustomTablists;
	}

	private String textValues(String line) {
		line = line.replaceAll("%sl1", "? ? ? ? ?");
		line = line.replaceAll("%sl2", "? ? ? ? ?");
		line = line.replaceAll("%sl3", "?? ?? ??");
		line = line.replaceAll("%sl4", "?? ?? ??");
		line = line.replaceAll("%sl5", "? ? ?");
		line = line.replaceAll("%sl6", "? ? ?");
		line = line.replaceAll("%sl7", "? ? ?");
		line = line.replaceAll("%sl8", "? ? ?");
		line = line.replaceAll("%a", "�");
		line = line.replaceAll("%A", "�");
		line = line.replaceAll("%o", "�");
		line = line.replaceAll("%O", "�");
		line = line.replaceAll("%u", "�");
		line = line.replaceAll("%U", "�");
		line = line.replaceAll("%s", "�");
		line = ChatColor.translateAlternateColorCodes('&', line);
		return line;
	}

	@SuppressWarnings("deprecation")
	private String userValues(String line, Player player) {
		Calendar cal = Calendar.getInstance();

		String d = new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime());
		String[] date = d.split("_");

		String ip_adress = player.getAddress().getAddress().toString();
		ip_adress = ip_adress.replaceAll("/", "");

		PluginDescriptionFile descFile = this.plugin.getDescription();
		String version = descFile.getVersion();

		String bukkitVersion = this.plugin.getBukkitVersion();
		String serverVersion = this.plugin.getServerVersion();

		line = line.replaceAll("%username%", player.getName());
		line = line.replaceAll("%displayname%", player.getDisplayName());
		line = line.replaceAll("%numplayers%", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()));
		line = line.replaceAll("%maxplayers%", String.valueOf(this.plugin.getServer().getMaxPlayers()));
		line = line.replaceAll("%ip-adress%", ip_adress);
		line = line.replaceAll("%ping%", String.valueOf(getPing(player)));
		line = line.replaceAll("%day%", fixFormat(String.valueOf(date[0])));
		line = line.replaceAll("%month%", fixFormat(String.valueOf(date[1])));
		line = line.replaceAll("%year%", fixFormat(String.valueOf(date[2])));
		line = line.replaceAll("%hours%", fixFormat(String.valueOf(cal.getTime().getHours())));
		line = line.replaceAll("%minutes%", fixFormat(String.valueOf(cal.getTime().getMinutes())));
		line = line.replaceAll("%seconds%", fixFormat(String.valueOf(cal.getTime().getSeconds())));
		line = line.replaceAll("%version%", version);
		line = line.replaceAll("%mc-version%", bukkitVersion);
		line = line.replaceAll("%serverversion%", serverVersion);
		line = line.replaceAll("%servername%", this.plugin.getServer().getServerName());
		line = line.replaceAll("%serverip%", this.plugin.getServer().getIp());
		line = line.replaceAll("%motd%", this.plugin.getServer().getMotd());
		User user = this.plugin.getGameManager().getUser(player.getName());
		String team;
		if (user.getTeam() == null) {
			team = "None";
		} else {
			team = user.getTeam().getTeamName();
		}
		line = line.replaceAll("%team%", team);
		return line;
	}

	private int getPing(Player player) {
		try {
			Object nms_player = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Field fieldPing = nms_player.getClass().getDeclaredField("ping");
			fieldPing.setAccessible(true);
			return fieldPing.getInt(nms_player);
		} catch (Exception e) {
		}
		return 38;
	}

	private String fixFormat(String input) {
		if (input.length() == 1) {
			input = "0" + input;
		}
		return input;
	}

	private String editText(String text) {
		int length = text.length();
		if (length > 16) {
			text = text.substring(0, 16);
		}
		length = text.length();
		if (length != 16) {
			length = 16 - length;
			for (int i = 0; i < length; i++) {
				text = text + " ";
			}
		}
		return text;
	}

	public static TabList getInstance() {
		return instance;
	}
}
