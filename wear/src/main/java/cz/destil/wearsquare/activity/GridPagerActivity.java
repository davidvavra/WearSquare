package cz.destil.wearsquare.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.WindowInsets;

import cz.destil.wearsquare.R;

/**
 * Activity which uses FragmentGridViewPager in a generic way.
 *
 * @author David Vávra (david@vavra.me)
 */
public class GridPagerActivity extends ProgressActivity {

    GridViewPager vPager;
    DotsPageIndicator vPagerIndicator;

    @Override
    int getMainViewResourceId() {
        return R.layout.activity_gridpager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
    }

    public void setAdapter(FragmentGridPagerAdapter adapter) {
        vPager.setAdapter(adapter);
    }

    private void setup() {
        vPager = (GridViewPager) getMainView().findViewById(R.id.pager);
        vPagerIndicator = (DotsPageIndicator) getMainView().findViewById(R.id.page_indicator);
        vPagerIndicator.setPager(vPager);

        vPager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = getResources().getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = getResources().getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                vPager.setPageMargins(rowMargin, colMargin);

                // add padding if black bar is present (Moto 360)
                // hack because the insets.getSystemWindowInsetBottom() is always 0
                // see: http://stackoverflow.com/questions/27660447/how-to-detect-height-of-black-bar-on-moto-360
                if (Build.MODEL.equals("Moto 360")) {
                    vPagerIndicator.setPadding(0, 0, 0, 70);
                }

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                vPager.onApplyWindowInsets(insets);
                return insets;
            }
        });
    }
}
