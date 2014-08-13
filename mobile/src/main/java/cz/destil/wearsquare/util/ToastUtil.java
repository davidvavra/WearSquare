package cz.destil.wearsquare.util;

import android.widget.Toast;

import cz.destil.wearsquare.core.App;

/**
 * Toasty stuff.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ToastUtil {

    public static void show(int resId) {
        Toast.makeText(App.get(), resId, Toast.LENGTH_LONG).show();
    }
}
