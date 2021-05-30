package io.snice.codecs.codec.diameter.avp;

import io.snice.codecs.codec.diameter.avp.api.*;
import io.snice.codecs.codec.diameter.avp.type.*;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AvpReflectionTest {

    /**
     * If these tests breaks it means that something change in the code generation of the AVPs.
     * Check out the code that is being generated by the snice-codecs-base plugin.
     */
    @Test
    public void testGetCode() {
        assertThat(AvpReflection.getCode(OriginHost.class), is(OriginHost.CODE));
        assertThat(AvpReflection.getCode(OriginRealm.class), is(OriginRealm.CODE));
        assertThat(AvpReflection.getCode(ExperimentalResult.class), is(ExperimentalResult.CODE));
        assertThat(AvpReflection.getCode(ApnOiReplacement.class), is(ApnOiReplacement.CODE));
    }

    /**
     * If these tests breaks it means that something change in the code generation of the AVPs.
     * Check out the code that is being generated by the snice-codecs-base plugin.
     */
    @Test
    public void testGetType() {
        assertThat(AvpReflection.getDiameterType(OriginHost.class), equalTo(DiameterIdentity.class));
        assertThat(AvpReflection.getDiameterType(OriginRealm.class), equalTo(DiameterIdentity.class));
        assertThat(AvpReflection.getDiameterType(ExperimentalResult.class), equalTo(Grouped.class));
        assertThat(AvpReflection.getDiameterType(ApnOiReplacement.class), equalTo(UTF8String.class));
        assertThat(AvpReflection.getDiameterType(UlrFlags.class), equalTo(Unsigned32.class));
    }

    @Test
    public void testOf() {
        final var o = AvpReflection.getCreator(OriginHost.class).apply(DiameterIdentity.parse("hello.3gppnetwork.org"));
        System.out.println(o);
    }
}
