package me.bartvv.uhcwar.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Kit {

	private ItemStack[] armour;
	private ItemStack[] content;

	public void giveKit(Player player) {
		PlayerInventory inventory = player.getInventory();
		inventory.setArmorContents(armour);
		inventory.setContents(content);
	}
}
