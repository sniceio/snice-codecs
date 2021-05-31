package io.snice.codecs.codec.gtp.gtpc.v1.ie.tlv;

import io.snice.codecs.codec.gtp.GtpRawData;
import io.snice.codecs.codec.gtp.GtpTestBase;
import io.snice.codecs.codec.gtp.type.RawType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TypeLengthValueTest extends GtpTestBase {

    /**
     * If we do not find the given
     */
    @Test
    public void testTlvFraming() {
        final TypeLengthValue<RawType> tlv = (TypeLengthValue<RawType>) TypeLengthValueFramer.frame(GtpRawData.gsnAddressTlv);
        assertThat(tlv.getType(), is((byte) 133));
        assertThat(tlv.getLength(), is(4));
        assertThat(tlv.getValue().getBuffer().toIPv4String(0), is("10.11.12.13"));
    }

}
