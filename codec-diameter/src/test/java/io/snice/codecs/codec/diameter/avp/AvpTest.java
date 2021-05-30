package io.snice.codecs.codec.diameter.avp;

import io.snice.buffer.Buffer;
import io.snice.buffer.Buffers;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.diameter.avp.api.AccessRestrictionData;
import io.snice.codecs.codec.diameter.avp.api.DestinationRealm;
import io.snice.codecs.codec.diameter.avp.api.OriginHost;
import io.snice.codecs.codec.diameter.avp.type.*;
import io.snice.generics.Generics;
import net.bytebuddy.pool.TypePool;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AvpTest {

    @Test
    public void testGenericTypeParameter() throws Exception {
        // Class<? extends Avp> c = (Class<Avp>)OriginHost.class;
        // Class<?> c = Generics.getTypeParameter(OriginHost.class, Avp.class);
        // System.out.println(c);
        apa(OriginHost.class);
        apa(DestinationRealm.class);
        apa(AccessRestrictionData.class);
    }

    private static <T extends Avp> void apa(Class<T> cls) throws Exception {

        System.out.println("==========================");
        System.out.println(cls.getName());
        // TypeVariable[] types = cls.getTypeParameters();
        // AnnotatedType[] annotatedTypes = cls.getAnnotatedInterfaces();
        // Arrays.stream(annotatedTypes).forEach(t -> {
            // System.out.println(t);
        // });

        final Field field = cls.getDeclaredField("CODE");
        final int code = (int)field.get(cls);
        System.out.println(code);

        final Field typeField = cls.getDeclaredField("TYPE");
        final Class<? extends DiameterIdentity> type = (Class<? extends DiameterIdentity>)typeField.get(cls);
        System.out.println(type);

        // Class<?> c = Generics.getTypeParameter(cls, DiameterIdentity.class);
        // System.out.println(c);
    }

    @Test
    public void testCreateAvp() {
        ensureAvp("7895", 123);
        ensureAvpBufferConsideredEqualToString(Buffers.wrap("7895"), 123);

        ensureAvpBufferAsRawBytes(432, 888);

        // since the "number" AVPs treat the buffer as a raw byte-array, having "garbage"
        // beyond the first 4 bytes of the buffer doesn't matter.
        final var mixedBuffer = WritableBuffer.of(10);
        mixedBuffer.write(5678);
        mixedBuffer.write(" blah");
        ensureAvpBufferAsRawBytes(mixedBuffer.build(), 5678, 888);

        final var mixedBuffer2 = WritableBuffer.of(20);
        mixedBuffer2.write(5678L);
        mixedBuffer2.write(" blah");
        ensureAvpBufferAsRawBytesLong(mixedBuffer2.build(), 5678, 888);

        ensureAvp(1234, 123);
        ensureAvp(90875L, 123);
    }

    @Test
    public void testNumberAvpsFail() {
        ensureNumberAvpsBlowsUp("hello world");
        ensureNumberAvpsBlowsUp("123 ");
        ensureNumberAvpsBlowsUp(" 123");
        ensureNumberAvpsBlowsUp("123 nope");
    }

    private void ensureNumberAvpsBlowsUp(final String value) {
        try {
            Avp.ofType(Unsigned32.class).withValue(value).withAvpCode(3333).build();
            fail("Expected a " + NumberFormatException.class.getName());
        } catch (final NumberFormatException e) {
            // expected
        }

        try {
            Avp.ofType(Integer32.class).withValue(value).withAvpCode(3333).build();
            fail("Expected a " + NumberFormatException.class.getName());
        } catch (final NumberFormatException e) {
            // expected
        }

        try {
            Avp.ofType(Integer64.class).withValue(value).withAvpCode(3333).build();
            fail("Expected a " + NumberFormatException.class.getName());
        } catch (final NumberFormatException e) {
            // expected
        }
    }

    /**
     * All non-number diameter types assumes that the given buffer should be treated as string.
     */
    private void ensureAvpBufferConsideredEqualToString(Buffer value, long code) {
        final var utf8Avp = Avp.ofType(UTF8String.class).withValue(value).withAvpCode(code).build();
        assertThat(utf8Avp.getValue().getValue(), is(value.toString()));
        assertThat(utf8Avp.getCode(), is(code));

        final var diameterIdentityAvp = Avp.ofType(DiameterIdentity.class).withValue(value).withAvpCode(code).build();
        assertThat(diameterIdentityAvp.getValue().asString(), is(value.toString()));
        assertThat(diameterIdentityAvp.getCode(), is(code));

        final var octetStringAvp = Avp.ofType(OctetString.class).withValue(value).withAvpCode(code).build();
        assertThat(octetStringAvp.getValue().getValue(), is(value.toString()));
        assertThat(octetStringAvp.getCode(), is(code));
    }

    /**
     * All "number" AVPs types assumes that the given buffer should be treated as a raw byte-array, as opposed
     * to a string, and therefore those parse methods will only try and extract out an integer based on the first
     * 4 bytes in the buffer.
     *
     */
    private void ensureAvpBufferAsRawBytes(int expectedValue, long code) {
        final var integer32Avp = Avp.ofType(Integer32.class).withValue(Buffers.wrapAsInt(expectedValue)).withAvpCode(code).build();
        assertThat(integer32Avp.getValue().getValue(), is(expectedValue));
        assertThat(integer32Avp.getCode(), is(code));

        final var unsigned32Avp = Avp.ofType(Unsigned32.class).withValue(Buffers.wrapAsInt(expectedValue)).withAvpCode(code).build();
        assertThat(unsigned32Avp.getValue().getValue(), is((long)expectedValue));
        assertThat(unsigned32Avp.getCode(), is(code));

        final var integer64Avp = Avp.ofType(Integer64.class).withValue(Buffers.wrapAsLong(expectedValue)).withAvpCode(code).build();
        assertThat(integer64Avp.getValue().getValue(), is((long)expectedValue));
        assertThat(integer64Avp.getCode(), is(code));
    }

    private void ensureAvpBufferAsRawBytes(final Buffer value, int expectedValue, long code) {
        final var integer32Avp = Avp.ofType(Integer32.class).withValue(value).withAvpCode(code).build();
        assertThat(integer32Avp.getValue().getValue(), is(expectedValue));
        assertThat(integer32Avp.getCode(), is(code));

        final var unsigned32Avp = Avp.ofType(Unsigned32.class).withValue(value).withAvpCode(code).build();
        assertThat(unsigned32Avp.getValue().getValue(), is((long)expectedValue));
        assertThat(unsigned32Avp.getCode(), is(code));
    }

    private void ensureAvpBufferAsRawBytesLong(final Buffer value, int expectedValue, long code) {
        final var integer64Avp = Avp.ofType(Integer64.class).withValue(Buffers.wrapAsLong(expectedValue)).withAvpCode(code).build();
        assertThat(integer64Avp.getValue().getValue(), is((long)expectedValue));
        assertThat(integer64Avp.getCode(), is(code));
    }

    /**
     * Ensure we can create AVPs from Strings
     */
    private void ensureAvp(String value, long code) {
        final var utf8Avp = Avp.ofType(UTF8String.class).withValue(value).withAvpCode(code).build();
        assertThat(utf8Avp.getValue().getValue(), is(value));
        assertThat(utf8Avp.getCode(), is(code));

        final var diameterIdentityAvp = Avp.ofType(DiameterIdentity.class).withValue(value).withAvpCode(code).build();
        assertThat(diameterIdentityAvp.getValue().asString(), is(value));
        assertThat(diameterIdentityAvp.getCode(), is(code));

        final var octetStringAvp = Avp.ofType(OctetString.class).withValue(value).withAvpCode(code).build();
        assertThat(octetStringAvp.getValue().getValue(), is(value));
        assertThat(octetStringAvp.getCode(), is(code));

        final var unsigned32Avp = Avp.ofType(Unsigned32.class).withValue(value).withAvpCode(code).build();
        assertThat(unsigned32Avp.getValue().getValue(), is(Long.parseLong(value)));
        assertThat(unsigned32Avp.getCode(), is(code));

        final var integer32Avp = Avp.ofType(Integer32.class).withValue(value).withAvpCode(code).build();
        assertThat(integer32Avp.getValue().getValue(), is(Integer.parseInt(value)));
        assertThat(integer32Avp.getCode(), is(code));

        final var integer64Avp = Avp.ofType(Integer64.class).withValue(value).withAvpCode(code).build();
        assertThat(integer64Avp.getValue().getValue(), is(Long.parseLong(value)));
        assertThat(integer64Avp.getCode(), is(code));
    }

    /**
     * Ensure we can create AVPs from integers
     */
    private void ensureAvp(int value, long code) {
        final var utf8Avp = Avp.ofType(UTF8String.class).withValue(value).withAvpCode(code).build();
        assertThat(utf8Avp.getValue().getValue(), is(String.valueOf(value)));
        assertThat(utf8Avp.getCode(), is(code));

        final var diameterIdentityAvp = Avp.ofType(DiameterIdentity.class).withValue(value).withAvpCode(code).build();
        assertThat(diameterIdentityAvp.getValue().asString(), is(String.valueOf(value)));
        assertThat(diameterIdentityAvp.getCode(), is(code));

        final var octetStringAvp = Avp.ofType(OctetString.class).withValue(value).withAvpCode(code).build();
        assertThat(octetStringAvp.getValue().getValue(), is(String.valueOf(value)));
        assertThat(octetStringAvp.getCode(), is(code));

        final var unsigned32Avp = Avp.ofType(Unsigned32.class).withValue(value).withAvpCode(code).build();
        assertThat(unsigned32Avp.getValue().getValue(), is((long)value));
        assertThat(unsigned32Avp.getCode(), is(code));

        final var integer64Avp = Avp.ofType(Integer64.class).withValue(value).withAvpCode(code).build();
        assertThat(integer64Avp.getValue().getValue(), is((long)value));
        assertThat(integer64Avp.getCode(), is(code));
    }

    /**
     * Ensure we can create AVPs from longs
     */
    private void ensureAvp(long value, long code) {
        final var utf8Avp = Avp.ofType(UTF8String.class).withValue(value).withAvpCode(code).build();
        assertThat(utf8Avp.getValue().getValue(), is(String.valueOf(value)));
        assertThat(utf8Avp.getCode(), is(code));

        final var diameterIdentityAvp = Avp.ofType(DiameterIdentity.class).withValue(value).withAvpCode(code).build();
        assertThat(diameterIdentityAvp.getValue().asString(), is(String.valueOf(value)));
        assertThat(diameterIdentityAvp.getCode(), is(code));

        final var octetStringAvp = Avp.ofType(OctetString.class).withValue(value).withAvpCode(code).build();
        assertThat(octetStringAvp.getValue().getValue(), is(String.valueOf(value)));
        assertThat(octetStringAvp.getCode(), is(code));

        final var unsigned32Avp = Avp.ofType(Unsigned32.class).withValue(value).withAvpCode(code).build();
        assertThat(unsigned32Avp.getValue().getValue(), is((long)value));
        assertThat(unsigned32Avp.getCode(), is(code));

        final var integer64Avp = Avp.ofType(Integer64.class).withValue(value).withAvpCode(code).build();
        assertThat(integer64Avp.getValue().getValue(), is((long)value));
        assertThat(integer64Avp.getCode(), is(code));
    }

}