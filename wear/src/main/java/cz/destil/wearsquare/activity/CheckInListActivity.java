package cz.destil.wearsquare.activity;

import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.event.CheckInVenueListEvent;
import cz.destil.wearsquare.event.ErrorEvent;
import cz.destil.wearsquare.event.ExitEvent;
import cz.destil.wearsquare.util.DebugLog;

public class CheckInListActivity extends ProgressActivity {

    WearableListView vList;

    @Override
    int getMainViewResourceId() {
        return R.layout.activity_check_in_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vList = (WearableListView) getMainView();
    }

    @Override
    public void startConnected() {
        super.startConnected();
        DebugLog.d("sending start message");
        teleport().sendMessage("/check-in-list", null);
        showProgress();
    }

    @Subscribe
    public void onVenueList(CheckInVenueListEvent event) {
        DebugLog.d("setting up adapter: " + event.getVenues());
        hideProgress();
        vList.setAdapter(new CheckInAdapter(CheckInListActivity.this, event.getVenues()));
        vList.setClickListener(new WearableListView.ClickListener() {
                                   @Override
                                   public void onClick(WearableListView.ViewHolder viewHolder) {
                                       String id = (String) viewHolder.itemView.getTag();
                                       String name = ((TextView) viewHolder.itemView.findViewById(R.id.text)).getText
                                               ().toString();
                                       DebugLog.d("id=" + id + " name=" + name);
                                       CheckInActivity.call(CheckInListActivity.this, id, name);
                                   }

                                   @Override
                                   public void onTopEmptyRegionClick() {

                                   }
                               }
        );
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
}