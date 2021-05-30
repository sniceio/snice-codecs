package io.snice.codecs.codec.diameter;

import io.snice.codecs.codec.tgpp.ReferencePoint;

import java.lang.ref.Reference;
import java.util.List;

import static io.snice.codecs.codec.tgpp.ReferencePoint.S6a;
import static io.snice.codecs.codec.tgpp.ReferencePoint.S6d;

public enum CommandCode {

    UpdateLocation(316, S6a, S6d),
    CancelLocation(317, S6a, S6d),
    AuthenticationInformation(318, S6a, S6d),
    InsertSubscriberData(319, S6a, S6d),
    DeleteSubscriberData(320, S6a, S6d),
    PurgeUe(321, S6a, S6d),
    Reset(322, S6a, S6d),
    Notify(323, S6a, S6d);

    private final int commandCode;
    private final List<ReferencePoint> referencePoint;

    CommandCode(final int commandCode, final ReferencePoint... referencePoints) {
        this.commandCode = commandCode;
        this.referencePoint = List.of(referencePoints);
    }

    public int getCode() {
        return commandCode;
    }


}
