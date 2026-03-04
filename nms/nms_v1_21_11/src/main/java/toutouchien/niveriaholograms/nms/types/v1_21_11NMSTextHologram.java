package toutouchien.niveriaholograms.nms.types;

import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaapi.utils.Task;
import toutouchien.niveriaholograms.NiveriaHolograms;
import toutouchien.niveriaholograms.config.TextHologramConfig;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.types.special.v1_21_11NMSDisplayHologram;
import toutouchien.niveriaholograms.utils.HologramUtils;

import java.util.concurrent.TimeUnit;

@NullMarked
public class v1_21_11NMSTextHologram extends v1_21_11NMSDisplayHologram implements NMSTextHologram {
    @Override
    public void createHologram(Hologram hologram) {
        ServerLevel level = ((CraftWorld) hologram.location().getWorld()).getHandle();

        Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        display.setTransformationInterpolationDuration(1);
        display.setTransformationInterpolationDelay(0);

        hologram.entity(display);

        TextHologramConfig config = hologram.config(TextHologramConfig.class);
        if (config.updateInterval() > 0)
            hologram.updateTask(Task.asyncRepeat(
                    ignored -> hologram.updateForAllPlayers(),
                    NiveriaHolograms.instance(),
                    Math.max(40L, config.updateInterval()) * 50L,
                    config.updateInterval() * 50L,
                    TimeUnit.MILLISECONDS
            ));
    }

    @Override
    public void updateHologram(Hologram hologram) {
        super.updateHologram(hologram);

        TextHologramConfig config = hologram.config(TextHologramConfig.class);
        Display.TextDisplay entity = hologram.entity(Display.TextDisplay.class);

        entity.getEntityData().set(Display.TextDisplay.DATA_LINE_WIDTH_ID, HologramUtils.TEXT_MAX_LINE_LENGTH);

        TextColor background = config.background();
        int newBackground;

        if (background == null)
            newBackground = Display.TextDisplay.INITIAL_BACKGROUND;
        else if (background == HologramUtils.TEXT_BACKGROUND_TRANSPARENT)
            newBackground = 0;
        else
            newBackground = background.value() | HologramUtils.TEXT_BACKGROUND_ALPHA_MASK;

        entity.getEntityData().set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, newBackground);

        byte flags = entity.getFlags();
        flags = HologramUtils.setFlag(flags, Display.TextDisplay.FLAG_SHADOW, config.textShadow());
        flags = (byte) (config.textAlignment() == TextDisplay.TextAlignment.LEFT ? (flags | Display.TextDisplay.FLAG_ALIGN_LEFT) : (flags & ~Display.TextDisplay.FLAG_ALIGN_LEFT));
        flags = HologramUtils.setFlag(flags, Display.TextDisplay.FLAG_SEE_THROUGH, config.seeThrough());
        flags = (byte) (config.textAlignment() == TextDisplay.TextAlignment.RIGHT ? (flags | Display.TextDisplay.FLAG_ALIGN_RIGHT) : (flags & ~Display.TextDisplay.FLAG_ALIGN_RIGHT));
        entity.setFlags(flags);
    }

    @Override
    public void updateHologramForPlayer(Hologram hologram, Player player) {

    }
}
