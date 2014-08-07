package cz.destil.wearsquare.util;

import android.content.pm.PackageManager;

import cz.destil.wearsquare.core.App;

/**
 * Package-related utils.
 *
 * @author David Vávra (david@vavra.me)
 */
public class PackageUtils {

    public static boolean isWearLauncherInstalled() {
        PackageManager pm = App.get().getPackageManager();
        try {
            pm.getPackageInfo("com.npi.wearminilauncher", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}
