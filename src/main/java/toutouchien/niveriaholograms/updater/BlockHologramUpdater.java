package toutouchien.niveriaholograms.updater;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Block;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;

import java.util.Optional;

public class BlockHologramUpdater extends HologramUpdater {
    private final Display.BlockDisplay display;
    private final BlockHologramConfiguration config;

    public BlockHologramUpdater(Display.BlockDisplay display, BlockHologramConfiguration config) {
        super(display, config);
        this.display = display;
        this.config = config;
    }


    @Override
    protected void updateDisplaySpecifics() {
        updateBlock();
        updateGlowing();
    }

    private void updateBlock() {
        ResourceLocation blockResource = ResourceLocation.parse(config.material().key().asString());
        Optional<Holder.Reference<Block>> blockHolder = BuiltInRegistries.BLOCK.get(blockResource);
        if (blockHolder.isEmpty())
            throw new IllegalArgumentException("Invalid block material: " + blockResource);

        Block block = blockHolder.get().value();
        display.setBlockState(block.defaultBlockState());
    }

    private void updateGlowing() {
        display.setGlowingTag(config.glowing());

        if (config.glowingColor() != null) {
            display.setGlowColorOverride(config.glowingColor().value());
        }
    }
}