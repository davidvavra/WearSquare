package cz.destil.wearsquare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.GridViewPager;

import com.squareup.otto.Subscribe;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.EmptyGridPagerAdapter;
import cz.destil.wearsquare.adapter.ExploreAdapter;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.event.ErrorEvent;
import cz.destil.wearsquare.event.ExitEvent;
import cz.destil.wearsquare.event.ExploreVenueListEvent;
import cz.destil.wearsquare.util.DebugLog;
import cz.destil.wearsquare.util.UiUtils;

public class ExploreActivity extends ProgressActivity {

    private static final int ON_PHONE_ACTIVITY = 41;
    GridViewPager vPager;

    @Override
    int getMainViewResourceId() {
        return R.layout.activity_explore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        finishOtherActivities();
        super.onCreate(savedInstanceState);
        vPager = (GridViewPager) getMainView();
        vPager.setAdapter(new EmptyGridPagerAdapter()); // bug in the UI library
    }

    @Override
    public void startConnected() {
        super.startConnected();
        DebugLog.d("sending start message");
        teleport().sendMessage("/explore-list/" + UiUtils.getScreenDimensions(), null);
        showProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ON_PHONE_ACTIVITY) {
            finish();
            App.bus().post(new ExitEvent());
        }
    }

    @Subscribe
    public void onVenueList(ExploreVenueListEvent event) {
        DebugLog.d("setting up adapter");
        hideProgress();
        vPager.setAdapter(new ExploreAdapter(this, event.getVenues()));
    }

    @Subscribe
    public void onError(ErrorEvent event) {
        showError(event.getMessage());
    }

    @Subscribe
    public void onExit(ExitEvent event) {
        DebugLog.d("on exit");
        finish();
    }

    public void navigate(ExploreAdapter.Venue venue) {
        teleport().sendMessage("/navigate/" + venue.getLatitude() + "/" + venue.getLongitude() + "/" + venue.getName(), null);
        openOnPhoneAnimation();
    }

    public void checkIn(ExploreAdapter.Venue venue) {
        CheckInActivity.call(this, venue.getId(), venue.getName());
    }

    public void openOnPhone(ExploreAdapter.Venue venue) {
        teleport().sendMessage("/open/" + venue.getId(), null);
        openOnPhoneAnimation();
    }

    private void openOnPhoneAnimation() {
        Intent i = new Intent(this, ConfirmationActivity.class);
        i.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        startActivityForResult(i, ON_PHONE_ACTIVITY);
    }
}
