package cz.destil.wearsquare.adapter;

import android.app.Fragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import cz.destil.wearsquare.activity.CheckInActivity;
import cz.destil.wearsquare.fragment.CheckInFragment;
import cz.destil.wearsquare.fragment.EmojisFragment;

/**
 * Adapter for the actual check-in process.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CheckInAdapter extends FragmentGridPagerAdapter {

    private String mVenueName;

    public CheckInAdapter(CheckInActivity activity, String venueName) {
        super(activity.getFragmentManager());
        mVenueName = venueName;
    }

    @Override
    public Fragment getFragment(int row, int column) {
        if (column == 0) {
            return CheckInFragment.create(mVenueName);
        } else {
            return new EmojisFragment();
        }
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int row) {
            return 2;
    }
}
