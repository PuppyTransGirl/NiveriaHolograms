package toutouchien.niveriaholograms.configuration;

import org.bukkit.Material;

public class BlockHologramConfiguration extends HologramConfiguration {
	private Material material = Material.GRASS_BLOCK;

	public Material material() {
		return material;
	}

	public BlockHologramConfiguration material(Material material) {
		this.material = material;
		return this;
	}
}
