package io.snice.codecs.codec.gtp.type;

import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.Teid;
import io.snice.codecs.codec.gtp.gtpc.v2.tliv.*;
import io.snice.codecs.codec.tgpp.ReferencePoint;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GroupedTypeTest extends GtpTestBase {

    @Test
    public void testGrouped() throws Exception {
        final var tliv = TypeLengthInstanceValue.frame(loadRaw("bearer_context.raw"));
        final var grouped = GroupedType.parse(tliv.getValue().getBuffer());
        assertThat(grouped.getInfoElements().size(), is(3));
        assertThat(grouped.getInfoElements().get(0).getType(), is((byte) 73));
        assertThat(grouped.getInfoElements().get(1).getType(), is((byte) 87));
        assertThat(grouped.getInfoElements().get(2).getType(), is((byte) 80));
    }

    @Test
    public void testCreateGrouped() throws Exception {
        final var arp = ArpType.ofValue(7, true, true);
        final var qos = QosType.ofQci(2).build();
        final var bqos = BearerQos.ofValue(BearerQosType.ofValue(arp, qos));

        final var ftied = FTeid.ofValue(FTeidType.create()
                .withIPv4Address("192.168.0.100")
                .withReferencePoint(ReferencePoint.S5, true)
                .withTeid(Teid.ZEROS)
                .build());

        final var ebi = Ebi.ofValue(EbiType.ofValue(5));
        final var grouped = GroupedType.ofValue(bqos, ftied, ebi);

        // serialize/deserialize to ensure we get it correct
        final var bearerContext = BearerContext.frame(BearerContext.ofValue(grouped).getRaw());
        assertThat(bearerContext.getValue().getInfoElements().size(), is(3));
    }
}
