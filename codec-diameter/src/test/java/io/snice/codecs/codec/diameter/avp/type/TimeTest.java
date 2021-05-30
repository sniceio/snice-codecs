package io.snice.codecs.codec.diameter.avp.type;

import io.snice.buffer.Buffer;
import io.snice.buffer.WritableBuffer;
import io.snice.codecs.codec.diameter.DiameterTestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TimeTest extends DiameterTestBase {

    @Test
    public void testTime() {
        final var t = Time.parse(Buffer.of((byte)65, (byte)66, (byte)67, (byte)68));
        assertThat(t, notNullValue());
        final var w = WritableBuffer.of(10);
        t.writeValue(w);
        assertThat(w.getWritableBytes(), is(10 - 4));
    }

    @Test
    public void testTimeBadInput() {
        ensureCreate(Time::parse, null, "Expected to blow up on null");
        ensureCreate(Time::parse, Buffer.of(), "Expected to blow up on too small buffer");
        ensureCreate(Time::parse, Buffer.of((byte)1), "Expected to blow up on too small buffer");
        ensureCreate(Time::parse, Buffer.of((byte)1, (byte)2), "Expected to blow up on too small buffer");
        ensureCreate(Time::parse, Buffer.of((byte)1, (byte)2, (byte)3), "Expected to blow up on too small buffer");
        // 4 bytes is what we want, so skipping this one
        ensureCreate(Time::parse, Buffer.of((byte)1, (byte)2, (byte)3, (byte)4, (byte)5), "Expected to blow up on too big buffer");
        ensureCreate(Time::parse, Buffer.of((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6), "Expected to blow up on too big buffer");
    }

    private void ensureCreate(Function<Buffer, Time> f, final Buffer b, final String msg) {
        try {
            f.apply(b);
            fail(msg);
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }
}
