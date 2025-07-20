package toutouchien.niveriaholograms.updater;

import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.Display;
import toutouchien.niveriaholograms.configurations.LeaderboardHologramConfiguration;
import toutouchien.niveriaholograms.core.Hologram;

public class LeaderboardHologramUpdater extends HologramUpdater {
    private final Display.TextDisplay display;
    private final LeaderboardHologramConfiguration config;

    public LeaderboardHologramUpdater(Display.TextDisplay display, LeaderboardHologramConfiguration config) {
        super(display, config);
        this.display = display;
        this.config = config;
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
        int newBackground = background == null ? Display.TextDisplay.INITIAL_BACKGROUND : background == Hologram.TRANSPARENT ? 0 : background.value() | 0xC8000000;
        display.getEntityData().set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, newBackground);
    }

    private void updateFlags() {
        byte flags = display.getFlags();
        flags = (byte) (config.textShadow() ? flags | Display.TextDisplay.FLAG_SHADOW : (flags & ~Display.TextDisplay.FLAG_SHADOW));
        flags = (byte) (config.seeThrough() ? flags | Display.TextDisplay.FLAG_SEE_THROUGH : (flags & ~Display.TextDisplay.FLAG_SEE_THROUGH));
        display.setFlags(flags);
    }
}
