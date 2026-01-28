package toutouchien.niveriaholograms.updater;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
        // This is for 1.21.4 support
        // Don't ask me why this works I don't know
        Vector3f translation = config.translation() != null
                ? new Vector3f(config.translation())
                : null;

        Vector3f scale = config.scale() != null
                ? new Vector3f(config.scale())
                : null;

        Quaternionf leftRotation = new Quaternionf();
        Quaternionf rightRotation = new Quaternionf();

        display.setTransformation(new Transformation(
                translation,
                leftRotation,
                scale,
                rightRotation
        ));
    }

    private void updateShadowAndVisibility() {
        display.setShadowRadius(config.shadowRadius());
        display.setShadowStrength(config.shadowStrength());
        display.setViewRange(config.visibilityDistance());
    }
}