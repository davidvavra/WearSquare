package cz.destil.wearsquare.activity;

import android.support.v4.app.Fragment;

import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.fragment.MainFragment;

/**
 * This activity is displayed in the phone, it's used for 4sq login and general information.
 *
 * @author David Vávra (david@vavra.me)
 */
public class PhoneActivity extends BaseActivity {

    @Override
    public Fragment getFragment() {
        return new MainFragment();
    }

    @Override
    public boolean shouldShowUpArrow() {
        return false;
    }
}
