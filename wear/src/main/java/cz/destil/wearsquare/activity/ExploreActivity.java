package cz.destil.wearsquare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;

import com.squareup.otto.Subscribe;

import java.util.List;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.ExploreAdapter;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.event.ErrorEvent;
import cz.destil.wearsquare.event.ExitEvent;
import cz.destil.wearsquare.event.ExploreVenueListEvent;
import cz.destil.wearsquare.event.ImageLoadedEvent;

/**
 * Displays a list of interesting venues around the user with images and tips. User can swipe to navigate to them,
 * check-in there and open them on the phone.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ExploreActivity extends GridPagerActivity {

    private static final int ON_PHONE_ACTIVITY = 41;
    private List<ExploreAdapter.Venue> mVenues;
    private ExploreAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        finishOtherActivities();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startConnected() {
        super.startConnected();
        if (mVenues == null) {
            teleport().sendMessage("/explore-list", null);
            showProgress();
        }
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
        hideProgress();
        mVenues = event.getVenues();
        if (mVenues != null && mVenues.size() > 0) {
            mAdapter = new ExploreAdapter(this, mVenues);
            setAdapter(mAdapter);
        } else {
            showError(getString(R.string.no_venues_nearby));
        }
    }

    @Subscribe
    public void onError(ErrorEvent event) {
        showError(event.getMessage());
    }

    @Subscribe
    public void onExit(ExitEvent event) {
        finish();
    }

    @Subscribe
    public void onImageLoaded(ImageLoadedEvent event) {
        if (mVenues != null) {
            int row = 0;
            for (ExploreAdapter.Venue venue : mVenues) {
                if (venue.getImageUrl() != null && venue.getImageUrl().equals(event.getImageUrl())) {
                    venue.setPhoto(event.getBitmap());
                    break;
                }
                row++;
            }
            if (event.getBitmap() != null) {
                mAdapter.notifyRowBackgroundChanged(row);
            }
        }
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
