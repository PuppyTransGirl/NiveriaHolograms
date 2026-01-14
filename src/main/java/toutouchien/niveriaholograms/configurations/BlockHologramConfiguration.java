package toutouchien.niveriaholograms.configurations;

import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import toutouchien.niveriaholograms.configurations.special.GlowingHologramConfiguration;

public class BlockHologramConfiguration extends GlowingHologramConfiguration {
	private BlockState blockState = BlockType.GRASS_BLOCK.createBlockData().createBlockState();

	public BlockHologramConfiguration() {
		// Needed
	}

	private BlockHologramConfiguration(GlowingHologramConfiguration glowingConfig) {
		this.glowing(glowingConfig.glowing());
		this.glowingColor(glowingConfig.glowingColor());
	}

	public BlockState blockState() {
		return blockState;
	}

	public BlockHologramConfiguration blockState(BlockState blockState) {
		this.blockState = blockState;
		return this;
	}

	@Override
	public BlockHologramConfiguration copy() {
		BlockHologramConfiguration copy = new BlockHologramConfiguration(super.copy());

		copy.blockState = this.blockState;

		return copy;
	}
}
