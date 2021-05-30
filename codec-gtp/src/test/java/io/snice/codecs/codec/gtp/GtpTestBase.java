/**
 *
 */
package io.snice.codecs.codec.gtp;

import io.snice.buffer.Buffer;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Header;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Message;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1MessageType;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Request;
import io.snice.codecs.codec.gtp.gtpc.v1.Gtp1Response;
import io.snice.codecs.codec.gtp.gtpc.v2.messages.tunnel.CreateSessionRequest;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.FTeid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Imsi;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.Msisdn;
import io.snice.codecs.codec.gtp.gtpc.v2.type.ArpType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.EcgiField;
import io.snice.codecs.codec.gtp.gtpc.v2.type.QosType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.RatType;
import io.snice.codecs.codec.gtp.gtpc.v2.type.TaiField;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author jonas@jonasborjesson.com
 */
public class GtpTestBase {

    protected static final Teid teid = Teid.of(10, 20, 30, 40);
    protected static final Teid gtpuTeid = Teid.of(11, 12, 13, 14);

    @Before
    public void setUp() throws Exception {
    }

    public static Gtp1Request loadGtpv1Request(final String resource) throws Exception {
        return GtpMessage.frame(loadRaw(resource)).toGtp1Request();
    }

    public static Gtp1Response loadGtpv1Response(final String resource) throws Exception {
        return GtpMessage.frame(loadRaw(resource)).toGtp1Response();
    }

    public static Gtp1Message loadGtpv1Message(final String resource) throws Exception {
        return GtpMessage.frame(loadRaw(resource)).toGtp1Message();
    }

    public static Buffer loadRaw(final String resource) throws Exception {
        final Path path = Paths.get(GtpTestBase.class.getResource(resource).toURI());
        final byte[] content = Files.readAllBytes(path);
        return Buffer.of(content);
    }

    public static CreateSessionRequest someCSR() {
        return CreateSessionRequest.create()
                .withTeid(Teid.ZEROS)
                .withRat(RatType.EUTRAN)
                .withMsisdn("4155551234")
                .withImsi("001001123456789")
                .withNewSenderControlPlaneFTeid()
                .withTeid(teid)
                .withIPv4Address("10.20.30.40")
                .doneFTeid()
                .withNewBearerContext()
                .withEpsBearerId(5)
                .withNewBearerQualityOfService(9)
                .withPriorityLevel(10)
                .withPci()
                .doneBearerQoS()
                .withNewSgwFTeid()
                .withTeid(gtpuTeid)
                .withIPv4Address("20.30.40.50")
                .doneFTeid()
                .doneBearerContext()
                .build()
                .toCreateSessionRequest();
    }

    public static void ensureMsisdn(final Optional<Msisdn> actualMsisdn, final String expected) {
        if (!actualMsisdn.isPresent()) {
            fail("Expected an MSIDN but got nothing");
            return;
        }

        ensureMsisdn(actualMsisdn.get(), expected);
    }

    public static void ensureMsisdn(final Msisdn actualMsisdn, final String expected) {
        assertThat(actualMsisdn.getValue().toString(), is(expected));
        assertThat(actualMsisdn, is(Msisdn.ofValue(expected)));
    }

    public static void ensureImsi(final Optional<Imsi> actualImsi, final String expected) {
        if (!actualImsi.isPresent()) {
            fail("Expected an IMSI but got nothing");
            return;
        }

        ensureImsi(actualImsi.get(), expected);
    }

    public static void ensureImsi(final Imsi actualImsi, final String expected) {
        assertThat(actualImsi.getValue().toString(), is(expected));
        assertThat(actualImsi, is(Imsi.ofValue(expected)));
    }

    public static void ensureArp(final ArpType arp, final int pl, final boolean pci, final boolean pvi) {
        assertThat(arp.isPreEmptionCapability(), is(pci));
        assertThat(arp.isPreEmptionVulnerability(), is(pvi));
        assertThat(arp.getPriorityLevel(), is(pl));
    }

    public static void ensureFTeid(final FTeid actualFTeid, final String ipv4Address, final Teid teid) {
        final var type = actualFTeid.getValue();
        assertThat(type.getTeid(), is(teid));
        assertThat(type.getIpv4AddressAsString().get(), is(ipv4Address));
    }

    public static void ensureFTeid(final Optional<FTeid> actualFteid, final String ipv4Address, final Teid teid) {
        if (!actualFteid.isPresent()) {
            fail("Expected an FTeid but got nothing");
            return;
        }
        ensureFTeid(actualFteid.get(), ipv4Address, teid);
    }

    public static void ensureQos(final QosType qos, final int qci, final long mbrUplink, final long mbrDownlink, final long gbrUplink, final long gbrDownlink) {
        assertThat(qos.getQci(), is(qci));

        assertThat(qos.getMbrUplink(), is(mbrUplink));
        assertThat(qos.getMbrDownlink(), is(mbrDownlink));

        assertThat(qos.getGbrUplink(), is(gbrUplink));
        assertThat(qos.getGbrDownlink(), is(gbrDownlink));
    }

    public static void assertTai(final TaiField tai, final String mcc, final String mnc, final Buffer tac) {
        assertThat(tai.getMccMnc().getMcc(), is(mcc));
        assertThat(tai.getMccMnc().getMnc(), is(mnc));
        assertThat(tai.getTac(), is(tac));
    }

    public static void assertTeid(final Teid actualTeid, final int a, final int b, final int c, final int d) {
        final var expectedTeid = Teid.of(a, b, c, d);
        assertThat(actualTeid, is(expectedTeid));
    }

    public static void assertImsi(final io.snice.codecs.codec.Imsi actualTeid, final int a, final int b, final int c, final int d) {
        final var expectedTeid = Teid.of(a, b, c, d);
        assertThat(actualTeid, is(expectedTeid));
    }

    public static void assertEcgi(final EcgiField ecgi, final String mcc, final String mnc, final Buffer eci) {
        assertThat(ecgi.getMccMnc().getMcc(), is(mcc));
        assertThat(ecgi.getMccMnc().getMnc(), is(mnc));
        assertThat(ecgi.getEci(), is(eci));
    }

    public static void assertGtpGPduHeader(final String resource, final int expectedLength, final int a, final int b, final int c, final int d) throws Exception {
        final Buffer expectedTeid = Buffer.of((byte) a, (byte) b, (byte) c, (byte) d);
        final Gtp1Header header = GtpHeader.frame(loadRaw(resource)).toGtp1Header();
        assertGtpGPduHeader(header, expectedLength, expectedTeid);
    }

    public static void assertGtpGPduHeader(final Gtp1Header header, final int expectedLength, final Buffer expectedTeid) {
        assertGtpGPduHeader(header, expectedLength, expectedLength, expectedTeid, null);
    }

    public static void assertGtpGPduHeader(final Gtp1Header header, final int expectedLength, final int expectedBodyLength,
                                           final Buffer expectedTeid, final Buffer seqNo) {
        final var seqNoMaybe = Optional.ofNullable(seqNo);
        assertThat(header.getVersion(), is(1));
        assertThat(header.getType(), is(Gtp1MessageType.G_PDU));
        assertThat(header.getLength(), is(expectedLength));
        assertThat(header.getTotalLength(), is(expectedLength + 8));
        assertThat(header.getBodyLength(), is(expectedBodyLength));
        assertThat(header.getSequenceNo(), is(seqNoMaybe));
        assertThat(header.getSequenceNoAsDecimal(), is(seqNoMaybe.map(b -> b.getUnsignedShort(0))));
        assertThat(header.getTeid(), is(Teid.of(expectedTeid)));
        assertThat(header.getTeid().getBuffer(), is(expectedTeid));
    }
}
