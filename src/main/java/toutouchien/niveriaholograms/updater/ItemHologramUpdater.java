package toutouchien.niveriaholograms.updater;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import toutouchien.niveriaholograms.configuration.ItemHologramConfiguration;

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
        display.setItemStack(ItemStack.fromBukkitCopy(config.itemStack()));
    }
}