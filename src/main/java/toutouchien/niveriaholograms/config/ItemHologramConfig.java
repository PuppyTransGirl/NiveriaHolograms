package toutouchien.niveriaholograms.config;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.special.GlowingHologramConfig;

@NullMarked
public class ItemHologramConfig extends GlowingHologramConfig {
    private ItemStack itemStack = ItemType.APPLE.createItemStack();

    public ItemStack itemStack() {
        return this.itemStack;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public ItemHologramConfig itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }
}
