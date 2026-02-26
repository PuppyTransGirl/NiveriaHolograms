package toutouchien.niveriaholograms.config.special;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GlowingHologramConfig extends DisplayHologramConfig {
    private boolean glowing = false;
    private TextColor glowingColor = NamedTextColor.WHITE;

    public boolean glowing() {
        return this.glowing;
    }

    public TextColor glowingColor() {
        return this.glowingColor;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public GlowingHologramConfig glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public GlowingHologramConfig glowingColor(TextColor glowingColor) {
        this.glowingColor = glowingColor;
        return this;
    }
}
