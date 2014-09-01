package org.stphung.openkore;

public class OpenkoreException extends Exception {
    public OpenkoreException() {
        super();
    }

    public OpenkoreException(String message) {
        super(message);
    }

    public OpenkoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenkoreException(Throwable cause) {
        super(cause);
    }
}
