package toutouchien.niveriaholograms.config;

import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.special.GlowingHologramConfig;

@NullMarked
public class BlockHologramConfig extends GlowingHologramConfig {
    private BlockState blockState = BlockType.GRASS_BLOCK.createBlockData().createBlockState();

    public BlockState blockState() {
        return this.blockState;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockHologramConfig blockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }
}
