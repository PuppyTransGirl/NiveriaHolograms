package toutouchien.niveriaholograms.core;

import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import toutouchien.niveriaholograms.configurations.*;
import toutouchien.niveriaholograms.updater.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public enum HologramType {
    BLOCK(
            BlockHologramConfiguration::new,
            level -> new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level),
            (display, config) -> new BlockHologramUpdater(
                    (Display.BlockDisplay) display,
                    (BlockHologramConfiguration) config
            )
    ),
    ITEM(
            ItemHologramConfiguration::new,
            level -> new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level),
            (display, config) -> new ItemHologramUpdater(
                    (Display.ItemDisplay) display,
                    (ItemHologramConfiguration) config
            )
    ),
    LEADERBOARD(
            LeaderboardHologramConfiguration::new,
            level -> new Display.TextDisplay(EntityType.TEXT_DISPLAY, level),
            (display, config) -> new LeaderboardHologramUpdater(
                    (Display.TextDisplay) display,
                    (LeaderboardHologramConfiguration) config
            )
    ),
    TEXT(
            TextHologramConfiguration::new,
            level -> new Display.TextDisplay(EntityType.TEXT_DISPLAY, level),
            (display, config) -> new TextHologramUpdater(
                    (Display.TextDisplay) display,
                    (TextHologramConfiguration) config
            )
    );

    private final Supplier<HologramConfiguration> configurationSupplier;
    private final Function<Level, Display> displayFactory;
    private final BiFunction<Display, HologramConfiguration, HologramUpdater> updaterFactory;

    HologramType(Supplier<HologramConfiguration> configurationSupplier, Function<Level, Display> displayFactory, BiFunction<Display, HologramConfiguration, HologramUpdater> updaterFactory) {
        this.configurationSupplier = configurationSupplier;
        this.displayFactory = displayFactory;
        this.updaterFactory = updaterFactory;
    }

    public HologramConfiguration createConfiguration() {
        return configurationSupplier.get();
    }

    public Display createDisplay(Level level) {
        return displayFactory.apply(level);
    }

    public HologramUpdater createUpdater(Display display, HologramConfiguration config) {
        return updaterFactory.apply(display, config);
    }
}
