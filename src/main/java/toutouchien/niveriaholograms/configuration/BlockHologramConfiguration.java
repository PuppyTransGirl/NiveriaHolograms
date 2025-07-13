package toutouchien.niveriaholograms.configuration;

import org.bukkit.Material;

public class BlockHologramConfiguration extends HologramConfiguration implements Cloneable {
	private Material material = Material.GRASS_BLOCK;

	public Material material() {
		return material;
	}

	public BlockHologramConfiguration material(Material material) {
		this.material = material;
		return this;
	}

    @Override
    public BlockHologramConfiguration clone() {
		return (BlockHologramConfiguration) super.clone();
    }
}
