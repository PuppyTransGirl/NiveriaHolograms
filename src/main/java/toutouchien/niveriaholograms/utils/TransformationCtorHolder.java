package toutouchien.niveriaholograms.utils;

import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Constructor;

@NullMarked
public final class TransformationCtorHolder {
    private final Constructor<?> ctor;
    private final Class<?> vecCls;
    private final Class<?> quatCls;

    public TransformationCtorHolder(Constructor<?> ctor, Class<?> vecCls, Class<?> quatCls) {
        this.ctor = ctor;
        this.vecCls = vecCls;
        this.quatCls = quatCls;
    }

    public Constructor<?> ctor() {
        return ctor;
    }

    public Class<?> vecCls() {
        return vecCls;
    }

    public Class<?> quatCls() {
        return quatCls;
    }
}