package cz.destil.wearsquare.core;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import cz.destil.wearsquare.BuildConfig;
import cz.destil.wearsquare.data.Preferences;
import io.fabric.sdk.android.Fabric;

/**
 * App instance.
 *
 * @author David Vávra (david@vavra.me)
 */
public class App extends Application {

    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        sInstance = this;
        Preferences.init();
    }

    public static App get() {
        return sInstance;
    }
}
