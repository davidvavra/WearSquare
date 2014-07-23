package cz.destil.wearsquare.core;

import android.app.Application;

import com.squareup.otto.Bus;

import cz.destil.wearsquare.util.MainThreadBus;

public class App extends Application {

    private static App sInstance;
    private static MainThreadBus sBus;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sBus = new MainThreadBus();
    }

    public static App get() {
        return sInstance;
    }

    public static Bus bus() {
        return sBus;
    }
}
