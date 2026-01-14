package toutouchien.niveriaholograms.updater;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;

public class ItemHologramUpdater extends HologramUpdater<Display.ItemDisplay, ItemHologramConfiguration> {
    public ItemHologramUpdater(Display.ItemDisplay display, ItemHologramConfiguration config) {
        super(display, config);
    }

    @Override
    protected void updateDisplaySpecifics() {
        updateItemStack();
        updateGlowing();
    }

    private void updateItemStack() {
        display.setItemStack(ItemStack.fromBukkitCopy(config.itemStack()));
    }

    private void updateGlowing() {
        display.setGlowingTag(config.glowing());

        if (config.glowingColor() != null)
            display.setGlowColorOverride(config.glowingColor().value());
    }
}