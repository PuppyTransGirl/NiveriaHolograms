package toutouchien.niveriaholograms.updater;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;

public abstract class HologramUpdater<D extends Display, C extends HologramConfiguration> {
    protected final D display;
    protected final C config;

    protected HologramUpdater(D display, C config) {
        this.display = display;
        this.config = config;
    }

    public void update() {
        updateCommonProperties();
        updateDisplaySpecifics();
    }

    protected abstract void updateDisplaySpecifics();

    private void updateCommonProperties() {
        updateBillboard();
        updateBrightness();
        updateTransformation();
        updateShadowAndVisibility();
    }

    private void updateBillboard() {
        display.setBillboardConstraints(config.billboard());
    }

    private void updateBrightness() {
        Brightness brightness = config.brightness();
        if (brightness != null)
            display.setBrightnessOverride(new Brightness(brightness.block(), brightness.sky()));
    }

    private void updateTransformation() {
        // This FINALLY fixes 1.21.4 support
        Vector3fc jomlTrans = config.translation(); // Trans :3
        Vector3fc jomlScale = config.scale();

        Vector3f translation = new Vector3f(jomlTrans.x(), jomlTrans.y(), jomlTrans.z());
        Vector3f scale = new Vector3f(jomlScale.x(), jomlScale.y(), jomlScale.z());

        display.setTransformation(new Transformation(
                translation,
                new Quaternionf(),
                scale,
                new Quaternionf()
        ));
    }

    private void updateShadowAndVisibility() {
        display.setShadowRadius(config.shadowRadius());
        display.setShadowStrength(config.shadowStrength());
        display.setViewRange(config.visibilityDistance());
    }
}