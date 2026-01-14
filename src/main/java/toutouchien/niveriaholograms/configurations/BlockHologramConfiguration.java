package toutouchien.niveriaholograms.configurations;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;

public class BlockHologramConfiguration extends HologramConfiguration {
	private BlockState blockState = BlockType.GRASS_BLOCK.createBlockData().createBlockState();
	private boolean glowing;
	private TextColor glowingColor;

	public BlockHologramConfiguration() {
		// Needed
	}

	private BlockHologramConfiguration(HologramConfiguration basicConfig) {
		this.scale(basicConfig.scale());
		this.translation(basicConfig.translation());
		this.billboard(basicConfig.billboard());
		this.brightness(basicConfig.brightness());
		this.shadowRadius(basicConfig.shadowRadius());
		this.shadowStrength(basicConfig.shadowStrength());
		this.visibilityDistance(basicConfig.visibilityDistance());
	}

	public BlockState blockState() {
		return blockState;
	}

	public boolean glowing() {
		return glowing;
	}

	public TextColor glowingColor() {
		return glowingColor;
	}

	public BlockHologramConfiguration blockState(BlockState blockState) {
		this.blockState = blockState;
		return this;
	}

	public BlockHologramConfiguration glowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}

	public BlockHologramConfiguration glowingColor(TextColor glowingColor) {
		this.glowingColor = glowingColor;
		return this;
	}

	@Override
	public BlockHologramConfiguration copy() {
		BlockHologramConfiguration copy = new BlockHologramConfiguration(super.copy());

		copy.blockState = this.blockState;
		copy.glowing = this.glowing;
		copy.glowingColor = this.glowingColor;

		return copy;
	}
}
