package cz.destil.wearsquare.core;

import android.app.Application;

import com.squareup.otto.Bus;

import cz.destil.wearsquare.util.ExceptionHandler;
import cz.destil.wearsquare.util.MainThreadBus;

/**
 * An application object.
 *
 * @author David Vávra (david@vavra.me)
 */
public class App extends Application {

    private static App sInstance;
    private static MainThreadBus sBus;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()));
        sBus = new MainThreadBus();
    }

    public static App get() {
        return sInstance;
    }

    public static Bus bus() {
        return sBus;
    }
}
