package io.snice.codecs.codec.diameter.avp;

import io.snice.codecs.codec.diameter.avp.type.DiameterIdentity;
import io.snice.codecs.codec.diameter.avp.type.DiameterType;
import io.snice.codecs.codec.diameter.avp.type.DiameterTypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Auxiliary class for reflection magic on AVPs. Mainly needed when building
 * tools for diameter, such as a Gatling plugin.
 */
public final class AvpReflection {

    private AvpReflection() {
        // just static helper methods so no instance of this one
    }

    public static final <T extends DiameterType, C extends Avp<T>> Function<T, C> of(Class<C> cls) throws AvpReflectionException {
        try {
            final Method method = Arrays.stream(cls.getMethods())
                    .filter(m -> "of".equals(m.getName()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException("of"));
            return v -> {
                try {
                    return (C)method.invoke(null, v);
                } catch (final IllegalAccessException e) {
                    throw new AvpReflectionException("Unable to access the CODE field on AVP \""
                            + cls + "\". Did the auto generated code change?", e);
                } catch (final InvocationTargetException e) {
                    throw new AvpReflectionException("Unable to access the CODE field on AVP \""
                            + cls + "\". Did the auto generated code change?", e);
                }
            };
        } catch (final NoSuchMethodException e) {
            throw new AvpReflectionException("Unable to read the of field on AVP \""
                    + cls + "\". Did the auto generated code change and the field has a new name?", e);
        }
    }

    public static final <T extends DiameterType, C extends Avp<T>> Function<T, C> getCreator(Class<C> cls) throws AvpReflectionException {
        try {
            final Field field = cls.getDeclaredField("CREATOR");
            return (Function<T, C>)field.get(cls);
        } catch (final NoSuchFieldException e) {
            throw new AvpReflectionException("Unable to read the CODE field on AVP \""
                    + cls + "\". Did the auto generated code change and the field has a new name?", e);
        } catch (final IllegalAccessException e) {
            throw new AvpReflectionException("Unable to access the CODE field on AVP \""
                    + cls + "\". Did the auto generated code change?", e);
        }
    }

    public static final <C extends Avp<? extends DiameterType>> Function<DiameterType, C> getCreator2(Class<C> cls) throws AvpReflectionException {
        try {
            final Field field = cls.getDeclaredField("CREATOR");
            return (Function<DiameterType, C>)field.get(cls);
        } catch (final NoSuchFieldException e) {
            throw new AvpReflectionException("Unable to read the CODE field on AVP \""
                    + cls + "\". Did the auto generated code change and the field has a new name?", e);
        } catch (final IllegalAccessException e) {
            throw new AvpReflectionException("Unable to access the CODE field on AVP \""
                    + cls + "\". Did the auto generated code change?", e);
        }
    }

    /**
     * Extracting out the AVP code for a given AVP.
     */
    public static final int getCode(Class<? extends Avp<? extends DiameterType>> cls) throws AvpReflectionException {
        try {
            final Field field = cls.getDeclaredField("CODE");
            return (int)field.get(cls);
        } catch (final NoSuchFieldException e) {
            throw new AvpReflectionException("Unable to read the CODE field on AVP \""
                    + cls + "\". Did the auto generated code change and the field has a new name?", e);
        } catch (final IllegalAccessException e) {
            throw new AvpReflectionException("Unable to access the CODE field on AVP \""
                    + cls + "\". Did the auto generated code change?", e);
        }
    }

    /**
     * Extracting out the type of the given AVP.
     *
     * All {@link Avp}s has a generic type that extends {@link DiameterType} and having the ability to
     * extract out that type is useful when building tools around AVPs. The Gatling plugin uses this in order
     * to provide an easier to use interface.
     */
    public static final Class<? extends DiameterType> getDiameterType(Class<? extends Avp<? extends DiameterType>> cls) throws AvpReflectionException {
        try {
            final Field typeField = cls.getDeclaredField("TYPE");
            return (Class<? extends DiameterType>)typeField.get(cls);
        } catch (final NoSuchFieldException e) {
            throw new AvpReflectionException("Unable to read the TYPE field on AVP \""
                    + cls + "\". Did the auto generated code change and the field has a new name?", e);
        } catch (final IllegalAccessException e) {
            throw new AvpReflectionException("Unable to access the TYPE field on AVP \""
                    + cls + "\". Did the auto generated code change?", e);
        }
    }
}
