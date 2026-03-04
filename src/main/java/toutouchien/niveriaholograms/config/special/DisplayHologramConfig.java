package toutouchien.niveriaholograms.config.special;

import org.bukkit.entity.Display;
import org.jetbrains.annotations.Contract;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class DisplayHologramConfig extends HologramConfig {
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Vector3f translation = new Vector3f(0, 0, 0);
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Display.@Nullable Brightness brightness;
    private float shadowRadius = 0F;
    private float shadowStrength = 1F;

    public Vector3f scale() {
        return scale;
    }

    public Vector3f translation() {
        return translation;
    }

    public Display.Billboard billboard() {
        return billboard;
    }

    public Display.@Nullable Brightness brightness() {
        return brightness;
    }

    public float shadowRadius() {
        return shadowRadius;
    }

    public float shadowStrength() {
        return shadowStrength;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public DisplayHologramConfig scale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public DisplayHologramConfig translation(Vector3f translation) {
        this.translation = translation;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public DisplayHologramConfig billboard(Display.Billboard billboard) {
        this.billboard = billboard;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public DisplayHologramConfig brightness(Display.@Nullable Brightness brightness) {
        this.brightness = brightness;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public DisplayHologramConfig shadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public DisplayHologramConfig shadowStrength(float shadowStrength) {
        this.shadowStrength = shadowStrength;
        return this;
    }
}
