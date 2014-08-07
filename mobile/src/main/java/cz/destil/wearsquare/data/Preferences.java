package cz.destil.wearsquare.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cz.destil.wearsquare.core.App;
/**
 * Simplifies access to SharedPreferences.
 *
 * @author David Vávra (david@vavra.me)
 */
public class Preferences {

    private static final String TOKEN = "foursquare_token";

    public static boolean hasFoursquareToken() {
        return preferences().contains(TOKEN);
    }

    public static String getFoursquareToken() {
        return preferences().getString(TOKEN, null);
    }

    public static void setFoursquareToken(String token) {
        preferences().edit().putString(TOKEN, token).commit();
    }

    private static SharedPreferences preferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.get());
    }

    public static void clearFoursquareToken() {
        preferences().edit().remove(TOKEN).commit();
    }
}
