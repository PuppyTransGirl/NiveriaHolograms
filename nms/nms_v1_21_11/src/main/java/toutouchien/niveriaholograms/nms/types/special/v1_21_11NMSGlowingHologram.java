package toutouchien.niveriaholograms.nms.types.special;

import net.minecraft.world.entity.Display;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.special.GlowingHologramConfig;
import toutouchien.niveriaholograms.core.Hologram;

@NullMarked
public class v1_21_11NMSGlowingHologram extends v1_21_11NMSDisplayHologram implements NMSGlowingHologram {
    @Override
    public void updateHologram(Hologram hologram) {
        super.updateHologram(hologram);

        GlowingHologramConfig config = hologram.config(GlowingHologramConfig.class);
        Display entity = hologram.entity(Display.class);

        boolean glowing = config.glowing();
        entity.setGlowingTag(glowing);
        if (glowing)
            entity.setGlowColorOverride(config.glowingColor().value());
    }
}
