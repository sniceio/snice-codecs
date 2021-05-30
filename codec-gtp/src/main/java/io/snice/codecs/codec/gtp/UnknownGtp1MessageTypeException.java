/**
 *
 */
package io.snice.codecs.codec.gtp;


/**
 * @author jonas@jonasborjesson.com
 */
public class UnknownGtp1MessageTypeException extends GtpException {

    private final int type;

    public UnknownGtp1MessageTypeException(final int type) {
        super("Unknown GTPv1 Message Type " + type);
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
