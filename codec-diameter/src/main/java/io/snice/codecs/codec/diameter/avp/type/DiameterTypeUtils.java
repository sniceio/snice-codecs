package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.preconditions.PreConditions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * Helper class for creating various diameter types.
 */
public class DiameterTypeUtils {

    private DiameterTypeUtils() {
        // to prevent instances being created
    }

    private static final Map<Class, Function<String, DiameterType>> stringMappers = new HashMap<>();
    private static final Map<Class, Function<Buffer, DiameterType>> bufferMappers = new HashMap<>();
    private static final Map<Class, Function<Integer, DiameterType>> intMappers = new HashMap<>();
    private static final Map<Class, Function<Long, DiameterType>> longMappers = new HashMap<>();

    static {
        stringMappers.put(DiameterIdentity.class, DiameterIdentity::parse);
        stringMappers.put(UTF8String.class, UTF8String::parse);
        stringMappers.put(OctetString.class, OctetString::parse);
        stringMappers.put(Integer32.class, Integer32::parse);
        stringMappers.put(Unsigned32.class, Unsigned32::parse);
        stringMappers.put(Integer64.class, Integer64::parse);

        bufferMappers.put(DiameterIdentity.class, DiameterIdentity::parse);
        bufferMappers.put(UTF8String.class, UTF8String::parse);
        bufferMappers.put(OctetString.class, OctetString::parse);
        bufferMappers.put(Integer32.class, Integer32::parse);
        bufferMappers.put(Unsigned32.class, Unsigned32::parse);
        bufferMappers.put(Integer64.class, Integer64::parse);
        bufferMappers.put(Time.class, Time::parse);

        intMappers.put(DiameterIdentity.class, DiameterIdentity::parse);
        intMappers.put(UTF8String.class, UTF8String::parse);
        intMappers.put(OctetString.class, OctetString::parse);
        intMappers.put(Integer32.class, Integer32::of);
        intMappers.put(Unsigned32.class, Unsigned32::of);
        intMappers.put(Integer64.class, Integer64::of);

        longMappers.put(DiameterIdentity.class, DiameterIdentity::parse);
        longMappers.put(UTF8String.class, UTF8String::parse);
        longMappers.put(OctetString.class, OctetString::parse);
        longMappers.put(Integer32.class, Integer32::of);
        longMappers.put(Unsigned32.class, Unsigned32::of);
        longMappers.put(Integer64.class, Integer64::of);
    }


    public static <T extends DiameterType> T create(final Class<T> type, final String value) {
        assertNotNull(type);
        assertNotNull(value);
        return (T)stringMappers.get(type).apply(value);
    }

    public static <T extends DiameterType> T create(final Class<T> type, final Buffer value) {
        assertNotNull(type);
        assertNotNull(value);
        return (T)bufferMappers.get(type).apply(value);
    }

    public static <T extends DiameterType> T create(final Class<T> type, final int value) {
        assertNotNull(type);
        return (T)intMappers.get(type).apply(value);
    }

    public static <T extends DiameterType> T create(final Class<T> type, final long value) {
        assertNotNull(type);
        return (T)longMappers.get(type).apply(value);
    }
}
