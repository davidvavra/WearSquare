package cz.destil.wearsquare.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import cz.destil.wearsquare.core.App;

/**
 * Simplifies access to SharedPreferences.
 *
 * @author David Vávra (david@vavra.me)
 */
public class Preferences {

    private static final String TOKEN = "foursquare_token";
    private static final String TWITTER = "twitter";
    private static final String FACEBOOK = "facebook";

    private static final String[] DEFAULT_EMOJIS = new String[]{"⌚","\uD83C\uDF7A","\uD83C\uDF74","\uD83D\uDC95",""};
    private static final String[] EMOJIS_KEYS = new String[] {"emoji_1", "emoji_2", "emoji_3", "emoji_4", "emoji_5"};

    public static void init() {
        if (preferences().getString(EMOJIS_KEYS[0], null) == null) {
            SharedPreferences.Editor edit = preferences().edit();
            for (int i = 0; i < EMOJIS_KEYS.length; i++) {
                edit.putString(EMOJIS_KEYS[i], DEFAULT_EMOJIS[i]);
            }
            edit.apply();
        }
    }

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

    public static String getBroadcast() {
        boolean twitter = preferences().getBoolean(TWITTER, false);
        boolean facebook = preferences().getBoolean(FACEBOOK, false);
        String broadcast = "public";
        if (twitter) {
            broadcast += ",twitter";
        }
        if (facebook) {
            broadcast += ",facebook";
        }
        return broadcast;
    }

    public static String getEmoji(String key) {
        return preferences().getString(key, "");
    }

    public static ArrayList<String> getEmojis() {
        ArrayList<String> emojis = new ArrayList<>();
        for (String key: EMOJIS_KEYS) {
            emojis.add(getEmoji(key));
        }
        return emojis;
    }
}
