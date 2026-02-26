package toutouchien.niveriaholograms.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.special.DisplayHologramConfig;
import toutouchien.niveriaholograms.utils.HologramUtils;

@NullMarked
public class TextHologramConfig extends DisplayHologramConfig {
    private ObjectList<String> text = ObjectLists.synchronize(new ObjectArrayList<>());
    private TextColor background = HologramUtils.DEFAULT_TEXT_BACKGROUND_COLOR;
    private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
    private boolean seeThrough = false;
    private boolean textShadow = false;
    private int updateInterval = -1;

    public ObjectList<String> text() {
        return text;
    }

    public TextColor background() {
        return background;
    }

    public TextDisplay.TextAlignment textAlignment() {
        return textAlignment;
    }

    public boolean seeThrough() {
        return seeThrough;
    }

    public boolean textShadow() {
        return textShadow;
    }

    public int updateInterval() {
        return updateInterval;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig text(ObjectList<String> text) {
        this.text = new ObjectArrayList<>(text);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig background(TextColor background) {
        this.background = background;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig textAlignment(TextDisplay.TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig seeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public TextHologramConfig updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }
}
