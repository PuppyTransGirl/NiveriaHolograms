package toutouchien.niveriaholograms.updater;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import toutouchien.niveriaholograms.configurations.HologramConfiguration;
import toutouchien.niveriaholograms.utils.HologramUtils;
import toutouchien.niveriaholograms.utils.TransformationCtorHolder;

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

    @SuppressWarnings("java:S112")
    private void updateTransformation() {
        try {
            TransformationCtorHolder holder = HologramUtils.transformationCtor();
            Object t = holder.ctor().newInstance(
                    holder.vecCls().cast(config.translation()),
                    holder.quatCls().cast(new Quaternionf()),
                    holder.vecCls().cast(config.scale()),
                    holder.quatCls().cast(new Quaternionf())
            );
            display.setTransformation((Transformation) t);
        } catch (Exception e) {
            throw new RuntimeException("Could not construct Transformation reflectively", e);
        }
    }

    private void updateShadowAndVisibility() {
        display.setShadowRadius(config.shadowRadius());
        display.setShadowStrength(config.shadowStrength());
        display.setViewRange(config.visibilityDistance());
    }
}