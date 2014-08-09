package cz.destil.wearsquare.util;

import android.content.Intent;
import android.net.Uri;

import java.util.List;

import cz.destil.wearsquare.core.App;

/**
 * Intent-related utils.
 *
 * @author David Vávra (david@vavra.me)
 */
public class IntentUtils {


    /**
     * Opens venue in the 4sq app on the phone.
     */
    public static void openOnPhone(String path) {
        String id = Uri.parse(path).getLastPathSegment();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.foursquare.com/venue/" + id));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.get().startActivity(intent);
    }

    /**
     * Launches navigation on the phone
     */
    public static void launchNavigation(String path) {
        List<String> segments = Uri.parse(path).getPathSegments();
        String latitude = segments.get(1);
        String longitude = segments.get(2);
        String name = segments.get(3);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:ll=" + latitude + "," +
                "" + longitude + "&q=" + name + "&mode=w"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.get().startActivity(intent);
    }

    public static void sendEmail(String exception) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"david@vavra.me"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "WearSquare crash report");
        intent.putExtra(Intent.EXTRA_TEXT, "If you send this crash report to author, " +
                "he will most likely fix the problem" +
                " in the next release. Details:\n\n" + exception);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.get().startActivity(intent);
    }
}
