package io.snice.codecs.codec.diameter;

import java.util.Objects;

import static io.snice.preconditions.PreConditions.assertNotNull;

public interface HopByHopIdentifier {

    static HopByHopIdentifier from(final DiameterMessage msg) {
        assertNotNull(msg);
        return new DefaultHopByHopIdentifier(msg.getHeader().getHopByHopId());
    }

    class DefaultHopByHopIdentifier implements HopByHopIdentifier {
        private final long id;

        private DefaultHopByHopIdentifier(final long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultHopByHopIdentifier that = (DefaultHopByHopIdentifier) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
