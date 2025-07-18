package toutouchien.niveriaholograms.configurations;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public class BlockHologramConfiguration extends HologramConfiguration {
	private Material material = Material.GRASS_BLOCK;
	private boolean glowing;
	private TextColor glowingColor;

	public Material material() {
		return material;
	}

	public BlockHologramConfiguration material(Material material) {
		this.material = material;
		return this;
	}

	public boolean glowing() {
		return glowing;
	}

	public BlockHologramConfiguration glowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}

	public TextColor glowingColor() {
		return glowingColor;
	}

	public BlockHologramConfiguration glowingColor(TextColor glowingColor) {
		this.glowingColor = glowingColor;
		return this;
	}

	@Override
	public BlockHologramConfiguration copy() {
		BlockHologramConfiguration copy = new BlockHologramConfiguration();
		copy.material = this.material;
		copy.glowing = this.glowing;
		copy.glowingColor = this.glowingColor;
		return copy;
	}
}
