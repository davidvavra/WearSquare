package cz.destil.wearsquare;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CheckInActivity extends BaseActivity {

    @InjectView(R.id.viewpager)
    GridViewPager vPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.inject(this);
       // vPager.setAdapter(new CheckInAdapter());
    }

    @Override
    protected void onStart() {
        super.onStart();
        teleport().sendMessage("/start", null);
    }

    class CheckInAdapter extends FragmentGridPagerAdapter {

        public CheckInAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getFragment(int row, int column) {
            return null;
        }

        @Override
        public int getRowCount() {
            return 2;
        }

        @Override
        public int getColumnCount(int row) {
            return 1;
        }
    }
}
