package cz.destil.wearsquare.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.data.Preferences;


/**
 * Screen with app settings.
 *
 * @author David Vávra (david@vavra.me)
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        syncSummary("emoji_1");
        syncSummary("emoji_2");
        syncSummary("emoji_3");
        syncSummary("emoji_4");
        syncSummary("emoji_5");
    }

    private void syncSummary(String key) {
        String emoji = Preferences.getEmoji(key);
        Preference preference = findPreference(key);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setSummary(preference, (String) newValue);
                return true;
            }
        });
        setSummary(preference, emoji);
    }

    private void setSummary(Preference preference, String summary) {
        if (TextUtils.isEmpty(summary)) {
            preference.setSummary(R.string.no_emoji);
        } else {
            preference.setSummary(summary);
        }
    }
}
