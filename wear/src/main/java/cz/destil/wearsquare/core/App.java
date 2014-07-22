package cz.destil.wearsquare.core;

import android.app.Application;

public class App extends Application {

    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static App get() {
        return sInstance;
    }
}
