package me.bartvv.uhcwar.tab.packets;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.bartvv.uhcwar.UHCWar;

public class PacketHandler {

	UHCWar plugin;
	private Class<?> packetPlayOutPlayerInfo;
	private Method getPlayerHandle;
	private Field getPlayerConnection;
	private Method sendPacket;

	public PacketHandler(UHCWar plugin) {
		try {
			this.plugin = plugin;
			this.packetPlayOutPlayerInfo = getMCClass("PacketPlayOutPlayerInfo");
			this.getPlayerHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle", new Class[0]);
			this.getPlayerConnection = getMCClass("EntityPlayer").getDeclaredField("playerConnection");
			this.sendPacket = getMCClass("PlayerConnection").getMethod("sendPacket",
					new Class[] { getMCClass("Packet") });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object createTablistPacket(String text, boolean cancel, int ping) {
		try {
			Object packet = this.packetPlayOutPlayerInfo.newInstance();
			Field a = this.packetPlayOutPlayerInfo.getDeclaredField("a");
			a.setAccessible(true);
			a.set(packet, text);
			Field b = this.packetPlayOutPlayerInfo.getDeclaredField("b");
			b.setAccessible(true);
			b.set(packet, Boolean.valueOf(cancel));
			Field c = this.packetPlayOutPlayerInfo.getDeclaredField("c");
			c.setAccessible(true);
			c.set(packet, Integer.valueOf(ping));
			return packet;
		} catch (Exception localException) {
		}
		return null;
	}

	public Object createTablistPacket(String text, boolean cancel) {
		return createTablistPacket(text, cancel, 0);
	}

	public void sendPackets(Player player, List<Object> packets) {
		try {
			for (Object packet : packets) {
				this.sendPacket.invoke(this.getPlayerConnection.get(this.getPlayerHandle.invoke(player, new Object[0])),
						new Object[] { packet });
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE,
					"An error has occurred whilst sending the packets. Is Bukkit up to date?");
			plugin.getLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	public void sendPackets(List<Object> packets) {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				for (Object packet : packets) {
					this.sendPacket.invoke(
							this.getPlayerConnection.get(this.getPlayerHandle.invoke(player, new Object[0])),
							new Object[] { packet });
				}
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE,
					"An error has occurred whilst sending the packets. Is Bukkit up to date?");
			plugin.getLogger().log(Level.SEVERE, e.getMessage());
		}
	}

	private Class<?> getMCClass(String name) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String className = "net.minecraft.server." + version + name;
		return Class.forName(className);
	}

	private Class<?> getCraftClass(String name) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String className = "org.bukkit.craftbukkit." + version + name;
		return Class.forName(className);
	}
}
