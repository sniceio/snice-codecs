package io.snice.codecs.codec.gtp.gtpc.v1.path;

import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.ie.tv.Recovery;
import io.snice.codecs.codec.gtp.gtpc.v1.messages.path.EchoRequest;
import io.snice.codecs.codec.gtp.gtpc.v1.messages.path.EchoResponse;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EchoTest extends GtpTestBase {

    @Test
    public void testFrameEcho() throws Exception {
        ensureEcho("echo_gtpv1_request.raw", true, 0);
        ensureEcho("echo_gtpv1_response.raw", false, 0);
    }

    @Test
    public void testCreateResponse() throws Exception {
        final var raw = loadRaw("echo_gtpv1_request.raw");
        final var req = GtpMessage.frame(raw).toGtp1Request().toEchoRequest();
        final var resp = req.createResponse()
                .withInfoElement(Recovery.ofValue(8))
                .build();
        ensureEcho(resp, false, 8);
    }

    private static void ensureEcho(final String resource, final boolean isRequest, final int expectedCounter) throws Exception {
        final var raw = loadRaw(resource);
        final var echo = GtpMessage.frame(raw).toGtp1Message();
        ensureEcho(echo, isRequest, expectedCounter);
    }

    private static void ensureEcho(final Gtp1Message echo, final boolean isRequest, final int expectedCounter) throws Exception {
        // TODO: the hierarchy is unfortunately a bit messed up
        assertThat(echo.isRequest(), is(isRequest));
        assertThat(echo.isResponse(), is(!isRequest));

        final var recovery = echo.getInfoElement(Recovery.TYPE_VALUE);
        assertThat(recovery.isPresent(), is(true));
        assertThat(((Recovery) recovery.get().ensure()).getValue().getCounter(), is(expectedCounter));

        if (isRequest) {
            assertThat(echo.isEchoRequest(), is(true));
            assertThat(echo.isEchoResponse(), is(false));
            assertThat(echo instanceof EchoRequest, is(true));
        } else {
            assertThat(echo.isEchoRequest(), is(false));
            assertThat(echo.isEchoResponse(), is(true));
            assertThat(echo instanceof EchoResponse, is(true));
        }
        assertThat(echo.isGtpVersion1(), is(true));
    }
}
