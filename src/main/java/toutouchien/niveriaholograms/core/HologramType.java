package toutouchien.niveriaholograms.core;

import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import toutouchien.niveriaholograms.configurations.BlockHologramConfiguration;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.configurations.ItemHologramConfiguration;
import toutouchien.niveriaholograms.configurations.TextHologramConfiguration;
import toutouchien.niveriaholograms.updater.BlockHologramUpdater;
import toutouchien.niveriaholograms.updater.HologramUpdater;
import toutouchien.niveriaholograms.updater.ItemHologramUpdater;
import toutouchien.niveriaholograms.updater.TextHologramUpdater;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum HologramType {
    BLOCK(
            level -> new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level),
            (display, config) -> new BlockHologramUpdater(
                    (Display.BlockDisplay) display,
                    (BlockHologramConfiguration) config
            )
    ),
    ITEM(
            level -> new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level),
            (display, config) -> new ItemHologramUpdater(
                    (Display.ItemDisplay) display,
                    (ItemHologramConfiguration) config
            )
    ),
    LEADERBOARD(
            level -> new Display.TextDisplay(EntityType.TEXT_DISPLAY, level),
            (display, config) -> new TextHologramUpdater(
                    (Display.TextDisplay) display,
                    (TextHologramConfiguration) config
            )
    ),
    TEXT(
            level -> new Display.TextDisplay(EntityType.TEXT_DISPLAY, level),
            (display, config) -> new TextHologramUpdater(
                    (Display.TextDisplay) display,
                    (TextHologramConfiguration) config
            )
    );

    private final Function<Level, Display> displayFactory;
    private final BiFunction<Display, HologramConfiguration, HologramUpdater> updaterFactory;

    HologramType(Function<Level, Display> displayFactory, BiFunction<Display, HologramConfiguration, HologramUpdater> updaterFactory) {
        this.displayFactory = displayFactory;
        this.updaterFactory = updaterFactory;
    }

    public Display createDisplay(Level level) {
        return displayFactory.apply(level);
    }

    public HologramUpdater createUpdater(Display display, HologramConfiguration config) {
        return updaterFactory.apply(display, config);
    }
}
