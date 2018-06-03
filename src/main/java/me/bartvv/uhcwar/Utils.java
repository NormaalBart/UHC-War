package me.bartvv.uhcwar;

import java.text.MessageFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

public class Utils {
	
	private static final UHCWar UHC_WAR = JavaPlugin.getPlugin(UHCWar.class);

	public static String tl(String message, Object... obj) {
		message = UHC_WAR.getMessages().getString(message);

		if (!(obj.length == 0)) {
			MessageFormat temp = new MessageFormat(message);
			String formatted = temp.format(obj);
			return formatted;
		}
		return message;
	}

	public static void debug(String string) {
		if (UHC_WAR.isDebug()) {
			System.out.println("[UHC-War] [DEBUG] " + string);
		}
	}

	public static List<String> tlList(String message, Object... obj) {
		List<String> msg = UHC_WAR.getMessages().getStringList(message);
		List<String> toReturn = Lists.newArrayList();

		if (!(obj.length == 0)) {
			for (int i = 0; i < msg.size(); i++) {
				MessageFormat temp = new MessageFormat(msg.get(i));
				String formatted = temp.format(obj);
				toReturn.add(ChatColor.translateAlternateColorCodes('&', formatted));
			}
		} else {
			for (int i = 0; i < msg.size(); i++) {
				toReturn.add(ChatColor.translateAlternateColorCodes('&', msg.get(i)));
			}
		}
		return toReturn;
	}

}
