package cz.destil.wearsquare.activity;

import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.event.VenueSearchEvent;
import cz.destil.wearsquare.util.DebugLog;

public class CheckInActivity extends BaseActivity {

    @InjectView(R.id.list)
    WearableListView vList;
    @InjectView(R.id.progress)
    ProgressBar vProgress;

    CheckInAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DebugLog.d("sending start message");
        teleport().sendMessage("/start", null);
    }

    @Subscribe
    public void onVenueSearch(VenueSearchEvent event) {
        DebugLog.d("setting up adapter");
        vProgress.setVisibility(View.GONE);
        mAdapter = new CheckInAdapter(CheckInActivity.this, event.getVenues());
        vList.setAdapter(mAdapter);
    }
}