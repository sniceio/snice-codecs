package io.snice.codecs.codec.diameter.avp.type;


import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import org.junit.Test;

import static io.snice.codecs.codec.diameter.avp.type.DiameterTypeUtils.create;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DiameterTypeUtilsTest {

    @Test
    public void testCreateFromStrings() {
        ensureStrings(UTF8String.class, "hello", UTF8String.of("hello"));
        ensureStrings(OctetString.class, "hello", OctetString.parse("hello"));
        ensureStrings(Integer32.class, "123", Integer32.of(123));
        ensureStrings(Integer64.class, "123", Integer64.of(123L));
    }

    @Test
    public void testCreateFromBuffers() {
        ensureBuffers(UTF8String.class, Buffers.wrap("hello"), UTF8String.of("hello"));
        ensureBuffers(OctetString.class, Buffers.wrap("hello"), OctetString.parse("hello"));
        ensureBuffers(Integer32.class, Buffers.wrapAsInt(123), Integer32.of(123));
        ensureBuffers(Unsigned32.class, Buffers.wrapAsInt(123), Unsigned32.of(123));
        // TODO: need to figure out why this one is now broken.
        // ensureBuffers(Unsigned32.class, Buffers.wrapAsInt(-123), Unsigned32.of(-123));
        // ensureBuffers(Integer64.class, Buffers.wrapAsLong(123L), Integer64.of(123L));
    }

    @Test
    public void testCreateFromInts() {
        ensureInts(UTF8String.class, 111, UTF8String.of("111"));
        ensureInts(OctetString.class, 222, OctetString.parse("222"));
        ensureInts(Integer32.class, 123, Integer32.of(123));
        ensureInts(Integer32.class, -123, Integer32.of(-123));
        ensureInts(Integer64.class, 898, Integer64.of(898L));
    }

    @Test
    public void testCreateFromLongs() {
        ensureLongs(UTF8String.class, 111L, UTF8String.of("111"));
        ensureLongs(OctetString.class, 222L, OctetString.parse("222"));
        ensureLongs(Integer32.class, 123L, Integer32.of(123));
        ensureLongs(Integer32.class, -123L, Integer32.of(-123));
        ensureLongs(Integer64.class, 898L, Integer64.of(898L));
    }

    private void ensureStrings(final Class<? extends DiameterType> cls, final String value, final Object expected) {
        final var actual = create(cls, value);
        assertThat(actual, is(expected));
    }

    private void ensureBuffers(final Class<? extends DiameterType> cls, final Buffer value, final Object expected) {
        final var actual = create(cls, value);
        assertThat(actual, is(expected));
    }

    private void ensureInts(final Class<? extends DiameterType> cls, final int value, final Object expected) {
        final var actual = create(cls, value);
        assertThat(actual, is(expected));
    }

    private void ensureLongs(final Class<? extends DiameterType> cls, final long value, final Object expected) {
        final var actual = create(cls, value);
        assertThat(actual, is(expected));
    }

}