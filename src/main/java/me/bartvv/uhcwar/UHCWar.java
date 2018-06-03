package me.bartvv.uhcwar;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import me.bartvv.uhcwar.commands.ICommand;
import me.bartvv.uhcwar.listeners.BlockListener;
import me.bartvv.uhcwar.listeners.DamageListener;
import me.bartvv.uhcwar.listeners.ItemListener;
import me.bartvv.uhcwar.listeners.MobSpawnListener;
import me.bartvv.uhcwar.listeners.PlayerDeathListener;
import me.bartvv.uhcwar.listeners.PlayerHunger;
import me.bartvv.uhcwar.listeners.PlayerJoinListener;
import me.bartvv.uhcwar.listeners.PlayerQuitListener;
import me.bartvv.uhcwar.manager.FileManager;
import me.bartvv.uhcwar.manager.GameManager;
import me.bartvv.uhcwar.manager.Kit;
import me.bartvv.uhcwar.manager.User;
import me.bartvv.uhcwar.tab.TabList;

@Getter
public class UHCWar extends JavaPlugin {

	private transient GameManager gameManager;

	private transient Map<Command, ICommand> commandCache;
	private transient Map<String, Kit> kit;
	private String serverVersion = "UNKNOWN";
	private String bukkitVersion = "UNKNOWN";
	private TabList tabList;
	private transient FileManager config, scoreboard, messages, data, kits;
	@Setter
	private transient boolean canSwitch;
	private transient boolean debug;

	@Override
	public void onLoad() {
		for (World world : Bukkit.getWorlds()) {
			world.setAutoSave(false);
			world.setSpawnFlags(false, false);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		for (World world : Bukkit.getWorlds()) {
			world.setAutoSave(false);
			world.setSpawnFlags(false, false);
		}
		canSwitch = true;

		this.config = new FileManager(this, "config.yml", 10);
		this.debug = getConfig().getBoolean("debug", true, false);
		this.config.setDebug(debug);
		this.scoreboard = new FileManager(this, "scoreboard.yml", 10, debug);
		this.messages = new FileManager(this, "messages.yml", 10, debug);
		this.data = new FileManager(this, "data.yml", 10, debug);
		this.kits = new FileManager(this, "kits.yml", 4, debug);

		this.tabList = new TabList(this);

		this.kit = Maps.newHashMap();

		this.commandCache = Maps.newHashMap();

		this.gameManager = new GameManager(this);

		this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		this.getServer().getPluginManager().registerEvents(new DamageListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ItemListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerHunger(this), this);
		this.getServer().getPluginManager().registerEvents(new MobSpawnListener(), this);

		this.checkBukkitVersion();
		this.checkServerVersion();

		new BukkitRunnable() {

			@Override
			public void run() {
				Bukkit.broadcastMessage("=======");
				Bukkit.broadcastMessage("");
				Bukkit.broadcastMessage(getName() + " is not yet bought");
				Bukkit.broadcastMessage("Do delete this, buy it :)");
				Bukkit.broadcastMessage("");
				Bukkit.broadcastMessage("=======");
			}
		}.runTaskTimer(this, 10, (15 * 20));

		for (String kits : getKits().getConfigurationSection("kits").getKeys(false)) {
			ItemStack[] armour = (ItemStack[]) ((List<ItemStack>) getKits().get("kits." + kits + ".armour"))
					.toArray(new ItemStack[0]);
			ItemStack[] content = (ItemStack[]) ((List<ItemStack>) getKits().get("kits." + kits + ".content"))
					.toArray(new ItemStack[0]);
			getKit().put(kits, new Kit(armour, content));
		}

	}

	@Override
	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (this.getConfig().getString("fallBack-server").equalsIgnoreCase("none")) {
				player.kickPlayer(getMessages().getString("kickmessage"));
			} else {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(this.getConfig().getString("fallBack-server"));
				player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
			}
		}

		try {
			for (World world : Bukkit.getWorlds()) {
				Bukkit.unloadWorld(world, false);
				File file = new File(world.getName() + File.separator + "playerdata");
				for (File files : file.listFiles()) {
					try {
						files.deleteOnExit();
						files.delete();
					} catch (Exception exc) {
					}

				}
			}
		} catch (Exception exc) {
		}

		data.save();
		kits.save();

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		ICommand iCommand = commandCache.get(command);

		if (iCommand == null) {
			try {
				iCommand = (ICommand) UHCWar.class.getClassLoader()
						.loadClass("me.bartvv.uhcwar.commands.Command" + command.getName()).newInstance();
				iCommand.setUhcWar(this);
				commandCache.put(command, iCommand);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException exc) {
				sender.sendMessage("Something went wrong! " + exc.getMessage());
				exc.printStackTrace();
				return true;
			}

		}

		String name;
		if (sender instanceof Player) {
			name = sender.getName();
		} else {
			name = "console";
		}

		User user = getGameManager().getUser(name);
		user.setBase(sender);

		try {
			iCommand.onCommand(user, command, label, args);
		} catch (Exception e) {
			try {
				String message = getConfig().getString(e.getMessage());
				sender.sendMessage(message);
			} catch (Exception exc) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void setDebug(boolean debug) {
		config.setDebug(debug);
		scoreboard.setDebug(debug);
		data.setDebug(debug);
		messages.setDebug(debug);
		this.debug = debug;
	}

	private void checkBukkitVersion() {
		String version = Bukkit.getVersion();
		version = version.replace("(", "");
		version = version.replace(")", "");
		version = version.split(" ")[2];
		this.bukkitVersion = version;
	}

	private void checkServerVersion() {
		try {
			Package[] arrayOfPackage;
			int j = (arrayOfPackage = Package.getPackages()).length;
			for (int i = 0; i < j; i++) {
				Package pa = arrayOfPackage[i];
				if (pa.getName().startsWith("net.minecraft.server.")) {
					this.serverVersion = pa.getName().split("\\.")[3];
					getLogger().info("This Server is running with CraftBukkit version " + this.bukkitVersion + "!");
					break;
				}
			}
		} catch (Exception e) {
			getLogger().severe("Unknown or unsupported CraftBukkit version! Is the Plugin up to date?");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

}
