package cz.destil.wearsquare.event;

/**
 * Is fired when uncaught exception occurs.
 */
public class ExceptionEvent {

    private Throwable exception;

    public ExceptionEvent(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
