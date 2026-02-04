package toutouchien.niveriaholograms.updater;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
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

    @SuppressWarnings({"CastCanBeRemovedNarrowingVariableType", "JavaReflectionInvocation", "JavaReflectionMemberAccess"})
    private void updateTransformation() {
        try {
            Class<?> vec3fCls = Class.forName("org.joml.Vector3f");
            Class<?> quatfCls = Class.forName("org.joml.Quaternionf");

            Object t = Transformation.class
                    .getConstructor(vec3fCls, quatfCls, vec3fCls, quatfCls)
                    .newInstance(
                            vec3fCls.cast(config.translation()),
                            quatfCls.cast(new Quaternionf()),
                            vec3fCls.cast(config.scale()),
                            quatfCls.cast(new Quaternionf())
                    );

            display.setTransformation((Transformation) t);
        } catch (NoSuchMethodException e) {
            // fallback to "fc" interfaces
            try {
                Class<?> vec3fcCls = Class.forName("org.joml.Vector3fc");
                Class<?> quatfcCls = Class.forName("org.joml.Quaternionfc");

                Object t = Transformation.class
                        .getConstructor(vec3fcCls, quatfcCls, vec3fcCls, quatfcCls)
                        .newInstance(
                                vec3fcCls.cast(config.translation()),
                                quatfcCls.cast(new Quaternionf()),
                                vec3fcCls.cast(config.scale()),
                                quatfcCls.cast(new Quaternionf())
                        );

                display.setTransformation((Transformation) t);
            } catch (Exception ex) {
                throw new RuntimeException("Could not construct Transformation reflectively", ex);
            }
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