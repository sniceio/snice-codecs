package io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel;

import io.snice.codecs.codec.gtp.GtpTestBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateSessionResponseTest extends GtpTestBase {

    @Test
    public void testCreateResponse() {
        final var request = someCSR();
        final var response = request.createResponse().build();
        assertThat(response.isCreateSessionResponse(), is(true));
    }
}
