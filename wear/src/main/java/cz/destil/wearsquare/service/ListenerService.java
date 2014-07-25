package cz.destil.wearsquare.service;

import android.graphics.Bitmap;

import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportService;

import java.util.ArrayList;
import java.util.List;

import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.adapter.ExploreAdapter;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.core.BaseAsyncTask;
import cz.destil.wearsquare.event.CheckInVenueListEvent;
import cz.destil.wearsquare.event.ErrorEvent;
import cz.destil.wearsquare.event.ExploreVenueListEvent;
import cz.destil.wearsquare.util.DebugLog;

public class ListenerService extends TeleportService {

    @Override
    public void onCreate() {
        super.onCreate();
        setOnSyncDataItemTask( new SyncDataTask());
    }

    class SyncDataTask extends OnSyncDataItemTask {
        @Override
        protected void onPostExecute(DataMap data) {
            DebugLog.d("receiving item");
            if (data.containsKey("error_message")) {
                DebugLog.d("error");
                App.bus().post(new ErrorEvent(data.getString("error_message")));
            } else if (data.containsKey("check_in_venues")) {
                DebugLog.d("check in");
                new CheckInVenuesTask(data).start();
            } else if (data.containsKey("explore_venues")) {
                DebugLog.d("explore");
                new ExploreVenuesTask(data).start();
            }
            setOnSyncDataItemTask(new SyncDataTask());
        }
    }

    class CheckInVenuesTask extends BaseAsyncTask {

        private DataMap data;

        CheckInVenuesTask(DataMap data) {
            this.data = data;
        }

        @Override
        public void inBackground() {
            List<CheckInAdapter.Venue> venues = new ArrayList<CheckInAdapter.Venue>();
            ArrayList<DataMap> dataVenues = data.getDataMapArrayList("check_in_venues");
            for (DataMap dataVenue : dataVenues) {
                Bitmap bitmap = dataVenue.getAsset("icon") != null ? loadBitmapFromAsset(dataVenue.getAsset
                        ("icon")) : null;
                venues.add(new CheckInAdapter.Venue(dataVenue.getString("id"), dataVenue.getString("name"), bitmap));
            }
            App.bus().post(new CheckInVenueListEvent(venues));
        }

        @Override
        public void postExecute() {

        }
    }

    class ExploreVenuesTask extends BaseAsyncTask {

        private DataMap data;

        ExploreVenuesTask(DataMap data) {
            this.data = data;
        }

        @Override
        public void inBackground() {
            List<ExploreAdapter.Venue> venues = new ArrayList<ExploreAdapter.Venue>();
            ArrayList<DataMap> dataVenues = data.getDataMapArrayList("explore_venues");
            for (DataMap dataVenue : dataVenues) {
                Bitmap bitmap = dataVenue.getAsset("photo") != null ? loadBitmapFromAsset(dataVenue.getAsset
                        ("photo")) : null;
                venues.add(new ExploreAdapter.Venue(dataVenue.getString("id"), dataVenue.getString("name"), dataVenue.getString("tip"), bitmap));
            }
            App.bus().post(new ExploreVenueListEvent(venues));
        }

        @Override
        public void postExecute() {

        }
    }
}
