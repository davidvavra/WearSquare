package cz.destil.wearsquare.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import cz.destil.wearsquare.core.App;

/**
 * UI-related utils.
 *
 * @author David Vávra (david@vavra.me)
 */
public class UiUtils {
    public static String getScreenDimensions() {
        Display display = ((WindowManager) App.get().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x + "x" + size.y;
    }
}
