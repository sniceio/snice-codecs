package io.snice.codecs.codec.diameter;


import io.snice.codecs.codec.diameter.avp.api.DestinationHost;
import io.snice.codecs.codec.diameter.avp.api.OriginHost;
import io.snice.codecs.codec.diameter.avp.type.DiameterIdentity;

import static io.snice.preconditions.PreConditions.assertNotNull;

/**
 * RFC6733: Section 3, Page 35
 *
 * <p>
 * End-to-End Identifier
 * <p>
 * The End-to-End Identifier is an unsigned 32-bit integer field (in
 * network byte order) that is used to detect duplicate messages.
 * Upon reboot, implementations MAY set the high order 12 bits to
 * contain the low order 12 bits of current time, and the low order
 * 20 bits to a random value.  Senders of request messages MUST
 * insert a unique identifier on each message.  The identifier MUST
 * remain locally unique for a period of at least 4 minutes, even
 * across reboots.  The originator of an answer message MUST ensure
 * that the End-to-End Identifier field contains the same value that
 * was found in the corresponding request.  The End-to-End Identifier
 * MUST NOT be modified by Diameter agents of any kind.  The
 * combination of the Origin-Host AVP (Section 6.3) and this field is
 * used to detect duplicates.  Duplicate requests SHOULD cause the
 * same answer to be transmitted (modulo the Hop-by-Hop Identifier
 * <p>
 * The key in the above paragraph is that the end-to-end identifier AND
 * the Origin-Host AVP are the two fields that together identifies
 * a unique request and is used to detect duplicate messages.
 * <p>
 * Note: perhaps it is wrong to call it a transaction identifier because it is
 * more of a de-duplication key and transaction within Diameter is somewhat loosely
 * defined (unlike e.g. SIP) and the main way of matching request/answers is through
 * the hop-by-hop identifier. However, naming is hard and in some sense, it does match
 * the "end-to-end transaction" so for the time being, it is what it is.
 *
 */
public interface TransactionIdentifier {

    static TransactionIdentifier from(final DiameterRequest req) {
        assertNotNull(req);
        return new DefaultTransactionIdentifier(req.getHeader().getEndToEndId(), req.getOriginHost().getValue());
    }

    static TransactionIdentifier of(final long endToEndId, final OriginHost originHost) {
        assertNotNull(originHost);
        return new DefaultTransactionIdentifier(endToEndId, originHost.getValue());
    }

    class DefaultTransactionIdentifier implements TransactionIdentifier {
        private final long endToEndId;
        private final DiameterIdentity identity;

        private DefaultTransactionIdentifier(final long endToEndId, final DiameterIdentity identity) {
            this.endToEndId = endToEndId;
            this.identity = identity;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final DefaultTransactionIdentifier that = (DefaultTransactionIdentifier) o;

            if (endToEndId != that.endToEndId) return false;
            return identity.equals(that.identity);
        }

        @Override
        public int hashCode() {
            final int result = (int) (endToEndId ^ (endToEndId >>> 32));
            return 31 * result + identity.hashCode();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TransactionIdentifier [");
            sb.append("End-To-End-ID: [").append(endToEndId);
            sb.append("] ");
            sb.append(identity);
            sb.append("]");
            return sb.toString();
        }
    }
}
