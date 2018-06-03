package me.bartvv.uhcwar.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;

import lombok.Getter;
import lombok.Setter;
import me.bartvv.uhcwar.UHCWar;
import me.bartvv.uhcwar.Utils;
import me.bartvv.uhcwar.manager.User;

@Getter
@Setter
public class ICommand {

	private UHCWar uhcWar;

	public void onCommand(User user, Command command, String commandLabel, String[] args) throws Exception {
		throw new Exception("Not-Supported");
	}

	public List<String> onTabComplete(User user, String commandLabel, String[] args) throws Exception {
		return Collections.emptyList();
	}

	public String tl(String message, Object... obj) {
		return Utils.tl(message, obj);
	}
}