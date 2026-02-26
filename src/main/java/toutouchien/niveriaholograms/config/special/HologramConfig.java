package toutouchien.niveriaholograms.config.special;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HologramConfig {
    private int visibilityDistance = -1;

    public int visibilityDistance() {
        return this.visibilityDistance;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public HologramConfig visibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
        return this;
    }
}
