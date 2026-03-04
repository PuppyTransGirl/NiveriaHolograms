package toutouchien.niveriaholograms.nms.types;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.BlockHologramConfig;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.types.special.v1_21_11NMSGlowingHologram;

@NullMarked
public class v1_21_11NMSBlockHologram extends v1_21_11NMSGlowingHologram implements NMSBlockHologram {
    @Override
    public void createHologram(Hologram hologram) {
        ServerLevel level = ((CraftWorld) hologram.location().getWorld()).getHandle();

        Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
        display.setTransformationInterpolationDuration(1);
        display.setTransformationInterpolationDelay(0);

        hologram.entity(display);
    }

    @Override
    public void updateHologram(Hologram hologram) {
        super.updateHologram(hologram);

        BlockHologramConfig config = hologram.config(BlockHologramConfig.class);
        Display.BlockDisplay entity = hologram.entity(Display.BlockDisplay.class);

        BlockState blockState = ((CraftBlockState) config.blockState()).getHandle();
        entity.setBlockState(blockState);
    }
}
