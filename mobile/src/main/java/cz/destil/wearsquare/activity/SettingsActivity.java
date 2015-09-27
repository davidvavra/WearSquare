package cz.destil.wearsquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;

import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.fragment.SettingsFragment;

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
    public Fragment getFragment() {
        return new SettingsFragment();
    }

    @Override
    public boolean shouldShowUpArrow() {
        return true;
    }
}
