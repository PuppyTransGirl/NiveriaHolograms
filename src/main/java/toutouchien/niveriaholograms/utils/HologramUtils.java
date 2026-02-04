package toutouchien.niveriaholograms.utils;

import com.mojang.math.Transformation;
import net.kyori.adventure.text.format.TextColor;
import toutouchien.niveriaholograms.updater.HologramUpdater;

import java.lang.reflect.Constructor;

public class HologramUtils {
    private static volatile TransformationCtorHolder TRANSFORMATION_CTOR;

    public static final TextColor TRANSPARENT = TextColor.color(0);
    public static final int MAX_LINE_LENGTH = 140305;

    private HologramUtils() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings({"JavaReflectionMemberAccess", "java:S112"})
    public static TransformationCtorHolder transformationCtor() {
        TransformationCtorHolder cached = TRANSFORMATION_CTOR;
        if (cached != null) return cached;
        synchronized (HologramUpdater.class) {
            cached = TRANSFORMATION_CTOR;
            if (cached != null)
                return cached;

            try {
                Class<?> vec3fCls = Class.forName("org.joml.Vector3f");
                Class<?> quatfCls = Class.forName("org.joml.Quaternionf");
                Constructor<?> ctor = Transformation.class.getConstructor(vec3fCls, quatfCls, vec3fCls, quatfCls);

                return TRANSFORMATION_CTOR = new TransformationCtorHolder(ctor, vec3fCls, quatfCls);
            } catch (NoSuchMethodException e) {
                try {
                    Class<?> vec3fcCls = Class.forName("org.joml.Vector3fc");
                    Class<?> quatfcCls = Class.forName("org.joml.Quaternionfc");
                    Constructor<?> ctor = Transformation.class.getConstructor(vec3fcCls, quatfcCls, vec3fcCls, quatfcCls);

                    return TRANSFORMATION_CTOR = new TransformationCtorHolder(ctor, vec3fcCls, quatfcCls);
                } catch (Exception ex) {
                    throw new RuntimeException("Could not resolve Transformation constructor reflectively", ex);
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not resolve Transformation constructor reflectively", e);
            }
        }
    }
}
