package io.snice.codecs.codec.diameter;

import io.snice.codecs.codec.diameter.avp.api.DestinationHost;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResult;
import io.snice.codecs.codec.diameter.avp.api.ExperimentalResultCode;
import io.snice.codecs.codec.diameter.avp.api.ResultCode;
import io.snice.functional.Either;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DiameterRequestTest extends DiameterTestBase {

    private final DestinationHost dest = DestinationHost.of("hello.epc.mnc001.mcc001.3gppnetwork.org");
    private final String imsi = "9999991234";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testCreateCER() {
        final var cer = DiameterRequest.createCER().withDestinationHost(dest).build();
        assertThat(cer.isCER(), is(true));
    }

    @Test
    public void testExperimentalResultImsiUnknown() throws Exception {
        final var answer = loadDiameterMessage("015_ula_imsi_unknown.raw").toAnswer();
        answer.getResultCode().fold(exp -> {
            final var resultCode = exp.getAvps().stream()
                    .filter(avp -> ExperimentalResultCode.CODE == avp.getCode())
                    .findFirst()
                    .map(avp -> (ExperimentalResultCode)avp.ensure())
                    .orElseThrow(() -> new RuntimeException("Expected to find " + ExperimentalResultCode.class.getName()));
            assertThat(resultCode, is(ExperimentalResultCode.DiameterErrorUserUnknown5001));
            return null;
        }, resultCode -> {
            fail("Did not expect a ResultCode here. Should have been an ExperimentalResult");
            return null;
        });
    }

    @Test
    public void testCreateULR() {
        final var b = DiameterRequest.createULR();
        b.withDestinationHost("hello.world.epc.mnc001.mcc001.3gppnetwork.org");
        b.withDestinationRealm("epc.mnc999.mcc999.3gppnetwork.org");
        b.withUserName(imsi);

        final var ulr = b.build();
        assertThat(ulr.isULR(), is(true));
        final var header = ulr.getHeader();
        assertThat(header.getApplicationId(), is(16777251L));
        assertThat(header.getCommandCode(), is(316));
    }
}
