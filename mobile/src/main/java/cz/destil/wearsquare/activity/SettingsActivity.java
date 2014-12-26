package cz.destil.wearsquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import cz.destil.wearsquare.SettingsFragment;

/**
 * Activity displaying app settings.
 *
 * @author David Vávra (david@vavra.me)
 */
public class SettingsActivity extends PreferenceActivity {

    public static void call(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
