package cz.destil.wearsquare;

import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * Screen with app settings.
 *
 * @author David Vávra (david@vavra.me)
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
