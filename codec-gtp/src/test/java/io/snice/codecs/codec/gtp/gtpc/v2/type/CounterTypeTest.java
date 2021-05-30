package io.snice.codecs.codec.gtp.gtpc.v2.type;

import org.junit.Test;

import static io.snice.buffer.Buffers.wrap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CounterTypeTest {

    @Test
    public void testType() {
        ensureType((byte) 0x00, 0);
        ensureType((byte) 0x0A, 10);
        ensureType((byte) 0x10, 16);
        ensureType((byte) 0x3E, 62);
        ensureType((byte) 0xE0, 14 * 16);
    }

    private void ensureType(final byte b, final int expected) {
        final var t = CounterType.parse(wrap(b));
        assertThat(t.getCounter(), is(expected));
        assertThat(t.toString(), is(Integer.toString(expected)));
    }


}