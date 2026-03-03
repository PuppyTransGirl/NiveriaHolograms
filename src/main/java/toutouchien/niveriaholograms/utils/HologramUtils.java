package toutouchien.niveriaholograms.utils;

import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HologramUtils {
    private HologramUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final TextColor DEFAULT_TEXT_BACKGROUND_COLOR = TextColor.color(1073741824);
    public static final TextColor TEXT_BACKGROUND_TRANSPARENT = TextColor.color(0);
    public static final int TEXT_BACKGROUND_ALPHA_MASK = 0xC8000000;
    public static final int TEXT_MAX_LINE_LENGTH = 1403;

    public static byte setFlag(byte flags, int flagMask, boolean enabled) {
        return (byte) (enabled ? (flags | flagMask) : (flags & ~flagMask));
    }
}
