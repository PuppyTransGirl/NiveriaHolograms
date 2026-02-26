package toutouchien.niveriaholograms.utils;

import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HologramUtils {
    private HologramUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final TextColor DEFAULT_TEXT_BACKGROUND_COLOR = TextColor.color(1073741824);
}
