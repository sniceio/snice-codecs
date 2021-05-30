package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.codecs.codec.gtp.GtpTestBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeleteSessionRequestTest extends GtpTestBase {

    @Test
    public void testDeleteSessionRequest() {
        final var dsr = DeleteSessionRequest.create().build();
        assertThat(dsr.isDeleteSessionRequest(), is(true));
    }
}
