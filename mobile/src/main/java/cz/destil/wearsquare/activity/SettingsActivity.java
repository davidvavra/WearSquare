package cz.destil.wearsquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cz.destil.wearsquare.SettingsFragment;
import cz.destil.wearsquare.core.BaseActivity;

/**
 * Activity displaying app settings.
 *
 * @author David Vávra (david@vavra.me)
 */
public class SettingsActivity extends BaseActivity {

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
