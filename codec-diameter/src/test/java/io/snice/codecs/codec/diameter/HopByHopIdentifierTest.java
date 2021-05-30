package io.snice.codecs.codec.diameter;

import io.snice.codecs.codec.diameter.avp.api.ResultCode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class HopByHopIdentifierTest extends DiameterTestBase {

    @Test
    public void testHopByHopIdentifier() {
        final var cer = someCer();
        final var cea = cer.createAnswer(ResultCode.DiameterApplicationUnsupported3007).build();
        final var id1 = HopByHopIdentifier.from(cer);
        final var id2 = HopByHopIdentifier.from(cea);

        assertThat(id1, is(id1)); // self
        assertThat(id1, is(id2));
        assertThat(id2, is(id1));

        final var id3 = HopByHopIdentifier.from(someCer());
        assertThat(id1, not(id3));
        assertThat(id2, not(id3));
    }

}