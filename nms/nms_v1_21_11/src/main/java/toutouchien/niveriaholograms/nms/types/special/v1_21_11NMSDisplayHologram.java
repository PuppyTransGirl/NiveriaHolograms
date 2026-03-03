package toutouchien.niveriaholograms.nms.types.special;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;
import toutouchien.niveriaholograms.config.special.DisplayHologramConfig;
import toutouchien.niveriaholograms.core.Hologram;
import toutouchien.niveriaholograms.nms.v1_21_11NMSAdapter;

@NullMarked
public class v1_21_11NMSDisplayHologram implements NMSDisplayHologram {
    @Override
    public void updateHologram(Hologram hologram) {
        DisplayHologramConfig config = hologram.config(DisplayHologramConfig.class);
        Display entity = hologram.entity(Display.class);

        Display.BillboardConstraints billboard = v1_21_11NMSAdapter.billboardBukkitToNMS(config.billboard());
        entity.setBillboardConstraints(billboard);

        Brightness brightness = v1_21_11NMSAdapter.brightnessBukkitToNMS(config.brightness());
        entity.setBrightnessOverride(brightness);

        Transformation transformation = new Transformation(
                config.translation(),
                new Quaternionf(),
                config.scale(),
                new Quaternionf()
        );

        entity.setTransformation(transformation);

        entity.setShadowRadius(config.shadowRadius());
        entity.setShadowStrength(config.shadowStrength());
    }
}
