package toutouchien.niveriaholograms.updater;

import net.minecraft.world.entity.Display;
import org.bukkit.craftbukkit.block.CraftBlockState;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;

public class BlockHologramUpdater extends HologramUpdater<Display.BlockDisplay, BlockHologramConfiguration> {
    public BlockHologramUpdater(Display.BlockDisplay display, BlockHologramConfiguration config) {
        super(display, config);
    }

    @Override
    protected void updateDisplaySpecifics() {
        updateBlock();
        updateGlowing();
    }

    private void updateBlock() {
        CraftBlockState craftBlockState = (CraftBlockState) config.blockState();
        display.setBlockState(craftBlockState.getHandle());
    }

    private void updateGlowing() {
        display.setGlowingTag(config.glowing());

        if (config.glowingColor() != null)
            display.setGlowColorOverride(config.glowingColor().value());
    }
}