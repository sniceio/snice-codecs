package io.snice.codecs.codec.gtp.type;

import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.TypeLengthInstanceValue;
import org.junit.Test;

public class BearerQosTypeTest extends GtpTestBase {

    @Test
    public void testBearer() throws Exception {
        var raw = TypeLengthInstanceValue.frame(loadRaw("bqos.raw"));
        var bqos = BearerQosType.parse(raw.getValue().getBuffer());
        ensureArp(bqos.getArp(), 2, false, true);
        ensureQos(bqos.getQos(), 8, 0, 0, 0, 0);

        raw = TypeLengthInstanceValue.frame(loadRaw("bqos2.raw"));
        bqos = BearerQosType.parse(raw.getValue().getBuffer());
        ensureArp(bqos.getArp(), 10, false, true);
        ensureQos(bqos.getQos(), 8, 0, 0, 0, 0);


        final var arp = ArpType.ofValue(7, true, true);

        final var qos = QosType.ofQci(2)
                .withMbrUplink(123)
                .withMbrDownlink(777)
                .withGbrUplink(5555)
                .withGbrDownlink(4444)
                .build();

        bqos = BearerQosType.parse(BearerQosType.ofValue(arp, qos).getBuffer());
        ensureArp(arp, 7, true, true);
        ensureQos(qos, 2, 123, 777, 5555, 4444);
    }

}