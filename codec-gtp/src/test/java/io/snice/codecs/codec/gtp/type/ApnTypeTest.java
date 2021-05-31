package io.snice.codecs.codec.gtp.type;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApnTypeTest extends GtpTestBase {

    @Test
    public void testCreate() {
        var a = ApnType.ofValue("hello.world.one.two");
        ensureLabels(a.getLabels(), "hello", "world", "one", "two");

        // and make sure it is serialized correctly by re-parsing it from
        // the raw string...
        ensureLabels(ApnType.parse(a.getBuffer()).getLabels(), "hello", "world", "one", "two");

        a = ApnType.ofValue("hello");
        ensureLabels(a.getLabels(), "hello");
        ensureLabels(ApnType.parse(a.getBuffer()).getLabels(), "hello");

        a = ApnType.ofValue("h");
        ensureLabels(a.getLabels(), "h");
        ensureLabels(ApnType.parse(a.getBuffer()).getLabels(), "h");
    }

    private ApnType loadApn(final String resource) throws Exception {
        final var buffer = TypeLengthInstanceValue.frame(loadRaw(resource)).getValue().getBuffer();
        return ApnType.parse(buffer);
    }

    private void ensureLabels(final List<Buffer> labels, final String... expected) {
        assertThat(labels.size(), is(expected.length));
        for (int i = 0; i < expected.length; ++i) {
            assertThat(labels.get(i).toString(), is(expected[i]));
        }
    }

}