package cz.destil.wearsquare.event;

/**
 * Otto event which is fired when error from phone is received.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ErrorEvent {

    private String message;

    public ErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
