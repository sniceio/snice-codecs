package io.snice.codecs.codec.gtp.gtpc.v2.type;

import io.snice.codecs.codec.gtp.GtpTestBase;
import org.junit.Test;

public class QosTypeTest extends GtpTestBase {

    @Test
    public void testQos() {
        var qos = QosType.ofQci(7).build();
        ensureQos(qos, 7, 0, 0, 0, 0);

        qos = QosType.parse(qos.getBuffer());
        ensureQos(qos, 7, 0, 0, 0, 0);

        qos = QosType.ofQci(2)
                .withMbrUplink(123)
                .withMbrDownlink(777)
                .withGbrUplink(5555)
                .withGbrDownlink(4444)
                .build();
        ensureQos(qos, 2, 123, 777, 5555, 4444);

        qos = QosType.parse(qos.getBuffer());
        ensureQos(qos, 2, 123, 777, 5555, 4444);
    }

}