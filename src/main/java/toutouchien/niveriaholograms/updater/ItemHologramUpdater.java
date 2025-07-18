package toutouchien.niveriaholograms.updater;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;

public class ItemHologramUpdater extends HologramUpdater {
    private final Display.ItemDisplay display;
    private final ItemHologramConfiguration config;

    public ItemHologramUpdater(Display.ItemDisplay display, ItemHologramConfiguration config) {
        super(display, config);
        this.display = display;
        this.config = config;
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

        if (config.glowingColor() != null) {
            display.setGlowColorOverride(config.glowingColor().value());
        }
    }
}