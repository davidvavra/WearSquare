package cz.destil.wearsquare.activity;

import android.os.Bundle;
import android.support.wearable.view.GridViewPager;

import com.squareup.otto.Subscribe;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.EmptyGridPagerAdapter;
import cz.destil.wearsquare.adapter.ExploreAdapter;
import cz.destil.wearsquare.event.ExploreVenueListEvent;
import cz.destil.wearsquare.util.DebugLog;
import cz.destil.wearsquare.util.UiUtils;

public class ExploreActivity extends ProgressActivity {

    GridViewPager vPager;

    @Override
    int getMainViewResourceId() {
        return R.layout.activity_explore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vPager = (GridViewPager) getMainView();
        vPager.setAdapter(new EmptyGridPagerAdapter()); // bug in the UI library
    }

    @Override
    public void startConnected() {
        super.startConnected();
        DebugLog.d("sending start message");
        teleport().sendMessage("/explore-list/"+ UiUtils.getScreenDimensions(), null);
        showProgress();
    }

    @Subscribe
    public void onVenueList(ExploreVenueListEvent event) {
        DebugLog.d("setting up adapter");
        hideProgress();
        vPager.setAdapter(new ExploreAdapter(getFragmentManager(), event.getVenues()));
    }

}
