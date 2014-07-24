package cz.destil.wearsquare.activity;

import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.event.ErrorEvent;
import cz.destil.wearsquare.event.VenueSearchEvent;
import cz.destil.wearsquare.util.DebugLog;

public class CheckInActivity extends BaseActivity {

    @InjectView(R.id.list)
    WearableListView vList;
    @InjectView(R.id.progress)
    ProgressBar vProgress;
    @InjectView(R.id.error)
    TextView vError;

    CheckInAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.inject(this);
    }

    @Override
    public void startConnected() {
        super.startConnected();
        DebugLog.d("sending start message");
        teleport().sendMessage("/start", null);
        vProgress.setVisibility(View.VISIBLE);
        vError.setVisibility(View.GONE);
    }

    @Override
    public void startDisconnected() {
        super.startDisconnected();
        showError(getString(R.string.please_connect));
    }

    @Subscribe
    public void onVenueSearch(VenueSearchEvent event) {
        DebugLog.d("setting up adapter");
        vProgress.setVisibility(View.GONE);
        mAdapter = new CheckInAdapter(CheckInActivity.this, event.getVenues());
        vList.setAdapter(mAdapter);
    }

    @Subscribe
    public void onError(ErrorEvent event) {
        showError(event.getMessage());
    }

    private void showError(String message) {
        vProgress.setVisibility(View.GONE);
        vError.setVisibility(View.VISIBLE);
        vError.setText(message);
    }
}