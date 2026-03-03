package toutouchien.niveriaholograms.nms.types;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.CraftWorld;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.ItemHologramConfig;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.types.special.v1_21_11NMSGlowingHologram;

@NullMarked
public class v1_21_11NMSItemHologram extends v1_21_11NMSGlowingHologram implements NMSItemHologram {
    @Override
    public void createHologram(Hologram hologram) {
        ServerLevel level = ((CraftWorld) hologram.location().getWorld()).getHandle();

        Display.ItemDisplay display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        display.setTransformationInterpolationDuration(1);
        display.setTransformationInterpolationDelay(0);

        hologram.entity(display);
    }

    @Override
    public void updateHologram(Hologram hologram) {
        super.updateHologram(hologram);

        ItemHologramConfig config = hologram.config(ItemHologramConfig.class);
        Display.ItemDisplay entity = hologram.entity(Display.ItemDisplay.class);

        entity.setItemStack(ItemStack.fromBukkitCopy(config.itemStack()));
    }
}
