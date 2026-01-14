package toutouchien.niveriaholograms.configurations;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import toutouchien.niveriaholograms.configurations.special.GlowingHologramConfiguration;

public class ItemHologramConfiguration extends GlowingHologramConfiguration {
    private ItemStack itemStack = new ItemStack(Material.APPLE);

    public ItemHologramConfiguration() {
        // Needed for HologramType
    }

    private ItemHologramConfiguration(GlowingHologramConfiguration basicConfig) {
        this.glowing(basicConfig.glowing());
        this.glowingColor(basicConfig.glowingColor());
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public ItemHologramConfiguration itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public ItemHologramConfiguration copy() {
        ItemHologramConfiguration copy = new ItemHologramConfiguration(super.copy());

        copy.itemStack = this.itemStack.clone();

        return copy;
    }
}
