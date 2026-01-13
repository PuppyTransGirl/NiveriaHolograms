package toutouchien.niveriaholograms.configurations;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemHologramConfiguration extends HologramConfiguration {
	private ItemStack itemStack = new ItemStack(Material.APPLE);
	private boolean glowing;
	private TextColor glowingColor;

	public ItemHologramConfiguration() {
		// Needed
	}

	private ItemHologramConfiguration(HologramConfiguration basicConfig) {
		this.scale(basicConfig.scale());
		this.translation(basicConfig.translation());
		this.billboard(basicConfig.billboard());
		this.brightness(basicConfig.brightness());
		this.shadowRadius(basicConfig.shadowRadius());
		this.shadowStrength(basicConfig.shadowStrength());
		this.visibilityDistance(basicConfig.visibilityDistance());
	}

	public ItemStack itemStack() {
		return itemStack;
	}

	public ItemHologramConfiguration itemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		return this;
	}

	public boolean glowing() {
		return glowing;
	}

	public ItemHologramConfiguration glowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}

	public TextColor glowingColor() {
		return glowingColor;
	}

	public ItemHologramConfiguration glowingColor(TextColor glowingColor) {
		this.glowingColor = glowingColor;
		return this;
	}

	@Override
	public ItemHologramConfiguration copy() {
		ItemHologramConfiguration copy = new ItemHologramConfiguration(super.copy());
		copy.itemStack = this.itemStack.clone();
		copy.glowing = this.glowing;
		copy.glowingColor = this.glowingColor;
		return copy;
	}
}
