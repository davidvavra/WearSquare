package cz.destil.wearsquare.service;

import android.graphics.Bitmap;

import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportService;

import java.util.ArrayList;
import java.util.List;

import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.core.BaseAsyncTask;
import cz.destil.wearsquare.event.VenueSearchEvent;
import cz.destil.wearsquare.util.DebugLog;

public class ListenerService extends TeleportService {

    @Override
    public void onCreate() {
        super.onCreate();
        setOnSyncDataItemTask(new CheckInVenuesTask());
    }

    class CheckInVenuesTask extends OnSyncDataItemTask {
        @Override
        protected void onPostExecute(DataMap data) {
            DebugLog.d("receiving item");
            new BitMapsTask(data).start();
            setOnSyncDataItemTask(new CheckInVenuesTask());
        }
    }

    class BitMapsTask extends BaseAsyncTask {

        private DataMap data;

        BitMapsTask(DataMap data) {
            this.data = data;
        }

        @Override
        public void inBackground() {
            List<CheckInAdapter.Venue> venues = new ArrayList<CheckInAdapter.Venue>();
            ArrayList<DataMap> dataVenues = data.getDataMapArrayList("venues");
            for (DataMap dataVenue : dataVenues) {
                Bitmap bitmap = dataVenue.getAsset("icon") != null ? loadBitmapFromAsset(dataVenue.getAsset
                        ("icon")) : null;
                venues.add(new CheckInAdapter.Venue(dataVenue.getString("id"), dataVenue.getString("name"), bitmap));
            }
            App.bus().post(new VenueSearchEvent(venues));
        }

        @Override
        public void postExecute() {

        }
    }
}
