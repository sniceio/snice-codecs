package io.snice.codecs.codec.gtp.gtpc.v1.ie.tv;

import io.snice.buffer.Buffers;
import io.snice.codecs.codec.gtp.GtpTestBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecoveryTest extends GtpTestBase {

    @Test
    public void testFrameRecovery() {
        final var recovery = Recovery.frame(Buffers.wrap((byte) 0x0e, (byte) 0x04));

        // we really want to make sure it is the very same instance here since
        // creating a Recovery, or any IE, directly on the interface itself for that type
        // then you should get the "final" value and since it is immutable, when you do ensure
        // you really should be getting back the exact same object.
        assertThat(recovery.ensure() == recovery, is(true));

        assertThat(recovery.getValue().getCounter(), is(4));
    }

    @Test
    public void testCreateRecovery() {
        final var recovery = Recovery.ofValue(8);
        assertThat(recovery.getValue().getCounter(), is(8));

        assertThat(recovery.getRaw(), is(Buffers.wrap((byte) 0x0e, (byte) 0x08)));
    }

}