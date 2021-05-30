package io.snice.codecs.codec.gtp.gtpc.v2;

import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.BearerContext;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Ebi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BearerContextTest extends GtpTestBase {

    @Test
    public void testBearerContext() throws Exception {
        final var bc = BearerContext.frame(loadRaw("bearer_context.raw"));

        final var ebi = (Ebi) bc.getValue().getInfoElements().get(0).ensure();
        assertThat(ebi.getValue().getId(), is(5));

        final var fteid = (FTeid) bc.getValue().getInfoElements().get(1).ensure();
        assertThat(fteid.getValue().getIpv4AddressAsString().get(), is("13.52.50.32"));
    }
}
