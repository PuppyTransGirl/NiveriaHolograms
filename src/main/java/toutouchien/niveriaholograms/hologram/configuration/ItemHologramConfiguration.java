package toutouchien.niveriaholograms.hologram.configuration;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemHologramConfiguration extends HologramConfiguration {
	private ItemStack itemStack = new ItemStack(Material.APPLE);

	public ItemStack itemStack() {
		return itemStack;
	}

	public ItemHologramConfiguration itemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		return this;
	}
}
