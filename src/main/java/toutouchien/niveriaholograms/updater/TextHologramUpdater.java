package toutouchien.niveriaholograms.updater;

import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.Display;
import org.bukkit.entity.TextDisplay;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;

public class TextHologramUpdater extends HologramUpdater<Display.TextDisplay, TextHologramConfiguration> {
    private static final int BACKGROUND_ALPHA_MASK = 0xC8000000; // preserves alpha/flags used by Display

    public TextHologramUpdater(Display.TextDisplay display, TextHologramConfiguration config) {
        super(display, config);
    }

    @Override
    protected void updateDisplaySpecifics() {
        updateMaxLineLength();
        updateBackgroundColor();
        updateFlags();
    }

    private void updateMaxLineLength() {
        display.getEntityData().set(Display.TextDisplay.DATA_LINE_WIDTH_ID, Hologram.MAX_LINE_LENGTH);
    }

    private void updateBackgroundColor() {
        TextColor background = config.background();
        int newBackground;

        if (background == null)
            newBackground = Display.TextDisplay.INITIAL_BACKGROUND;
        else if (background == Hologram.TRANSPARENT)
            newBackground = 0;
        else
            newBackground = background.value() | BACKGROUND_ALPHA_MASK;

        display.getEntityData().set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, newBackground);
    }

    private void updateFlags() {
        byte flags = display.getFlags();
        flags = setFlag(flags, Display.TextDisplay.FLAG_SHADOW, config.textShadow());
        flags = (byte) (config.textAlignment() == TextDisplay.TextAlignment.LEFT ? (flags | Display.TextDisplay.FLAG_ALIGN_LEFT) : (flags & ~Display.TextDisplay.FLAG_ALIGN_LEFT));
        flags = setFlag(flags, Display.TextDisplay.FLAG_SEE_THROUGH, config.seeThrough());
        flags = (byte) (config.textAlignment() == TextDisplay.TextAlignment.RIGHT ? (flags | Display.TextDisplay.FLAG_ALIGN_RIGHT) : (flags & ~Display.TextDisplay.FLAG_ALIGN_RIGHT));
        display.setFlags(flags);
    }

    private static byte setFlag(byte flags, int flagMask, boolean enabled) {
        return (byte) (enabled ? (flags | flagMask) : (flags & ~flagMask));
    }
}
