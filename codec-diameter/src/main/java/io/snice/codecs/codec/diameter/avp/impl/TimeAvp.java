package io.snice.codecs.codec.diameter.avp.impl;

import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.type.Time;
import io.snice.codecs.codec.diameter.avp.type.UTF8String;

public class TimeAvp extends ImmutableAvp<Time>{

    public TimeAvp(final FramedAvp raw) {
        super(raw, Time.parse(raw.getData()));
    }
}
