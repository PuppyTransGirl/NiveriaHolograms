package toutouchien.niveriaholograms.configurations;

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

	@Override
	public BlockHologramConfiguration copy() {
		BlockHologramConfiguration copy = new BlockHologramConfiguration();
		copy.material = this.material;
		return copy;
	}
}
