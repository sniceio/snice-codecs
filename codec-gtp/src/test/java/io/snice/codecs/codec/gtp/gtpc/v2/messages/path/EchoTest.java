package io.snice.codecs.codec.gtp.gtpc.v2.messages.path;

import io.snice.codecs.codec.gtp.GtpMessage;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.Gtp2Message;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Recovery;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EchoTest extends GtpTestBase {

    @Test
    public void testFrameEcho() throws Exception {
        ensureEcho("echo.raw", true, 0);
        ensureEcho("echo_response.raw", false, 0);

        ensureEcho("echo2.raw", true, 62);
        ensureEcho("echo_response2.raw", false, 7);
    }

    @Test
    public void testCreateResponse() throws Exception {
        final var echo = (EchoRequest) Gtp2Message.frame(loadRaw("echo.raw")).toGtp2Request();
        final var resp = echo.createResponse().withTliv(Recovery.ofValue("7")).build();
        ensureEcho(resp, false, 7);
    }

    private static void ensureEcho(final String resource, final boolean isRequest, final int expectedCounter) throws Exception {
        final var raw = loadRaw(resource);
        final var echo = GtpMessage.frame(raw).toGtp2Message();
        ensureEcho(echo, isRequest, expectedCounter);
    }

    private static void ensureEcho(final Gtp2Message echo, final boolean isRequest, final int expectedCounter) throws Exception {
        assertThat(echo.isRequest(), is(isRequest));

        if (isRequest) {
            assertThat(echo.isEchoRequest(), is(true));
            assertThat(echo.isEchoResponse(), is(false));
        } else {
            assertThat(echo.isEchoRequest(), is(false));
            assertThat(echo.isEchoResponse(), is(true));
        }
        assertThat(echo.isGtpVersion2(), is(true));

        assertThat(echo.getInfoElements().size(), is(1));
        final Recovery recovery = (Recovery) echo.getInfoElement(Recovery.TYPE).get();
        assertThat(recovery.getValue().getCounter(), is(expectedCounter));
    }
}
