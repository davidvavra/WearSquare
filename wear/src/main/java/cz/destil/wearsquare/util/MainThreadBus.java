package cz.destil.wearsquare.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
/**
 * Modification to Otto which allow firing events on any thread.
 *
 * @author David Vávra (david@vavra.me)
 */
public class MainThreadBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }
}