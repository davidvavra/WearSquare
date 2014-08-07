package cz.destil.wearsquare.service;

import android.graphics.Bitmap;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
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
import cz.destil.wearsquare.event.ImageLoadedEvent;
import cz.destil.wearsquare.util.DebugLog;
/**
 * Receives and processes all communication from the phone.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ListenerService extends TeleportService {

    /**
     * Main entry point for data from the phone.
     * Workaround to:  https://github.com/Mariuxtheone/Teleport/issues/3
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                DataMap data = dataMapItem.getDataMap();
                if (data.containsKey("error_message")) {
                    DebugLog.d("error");
                    App.bus().post(new ErrorEvent(data.getString("error_message")));
                } else if (data.containsKey("check_in_venues")) {
                    DebugLog.d("check in");
                    processCheckInList(data);
                } else if (data.containsKey("explore_venues")) {
                    DebugLog.d("explore");
                    processExploreList(data);
                } else if (data.containsKey("image_url")) {
                    new ProcessImageTask(data).start();
                }

            }
        }
    }

    /**
     * Processes check-in list.
     */
    private void processCheckInList(DataMap data) {
        List<CheckInAdapter.Venue> venues = new ArrayList<CheckInAdapter.Venue>();
        ArrayList<DataMap> dataVenues = data.getDataMapArrayList("check_in_venues");
        for (DataMap dataVenue : dataVenues) {
            venues.add(new CheckInAdapter.Venue(dataVenue.getString("id"), dataVenue.getString("name"), dataVenue.getString("image_url")));
        }
        App.bus().post(new CheckInVenueListEvent(venues));
    }

    /**
     * Processes explore list.
     */
    private void processExploreList(DataMap data) {
        List<ExploreAdapter.Venue> venues = new ArrayList<ExploreAdapter.Venue>();
        ArrayList<DataMap> dataVenues = data.getDataMapArrayList("explore_venues");
        for (DataMap dataVenue : dataVenues) {
            venues.add(new ExploreAdapter.Venue(dataVenue.getString("id"), dataVenue.getString("name"), dataVenue.getString("tip"),
                    dataVenue.getDouble("latitude"), dataVenue.getDouble("longitude"), dataVenue.getString("image_url")));
        }
        App.bus().post(new ExploreVenueListEvent(venues));
    }
    /**
     * Decodes BitMap from Asset.
     */
    class ProcessImageTask extends BaseAsyncTask {

        private DataMap data;

        ProcessImageTask(DataMap data) {
            this.data = data;
        }

        @Override
        public void inBackground() {
            Asset asset = data.getAsset("asset");
            String imageUrl = data.getString("image_url");
            Bitmap bitmap = null;
            if (asset != null) {
                bitmap = loadBitmapFromAsset(asset);
            }
            DebugLog.d("image loaded: " + imageUrl);
            App.bus().post(new ImageLoadedEvent(imageUrl, bitmap));
        }

        @Override
        public void postExecute() {

        }
    }
}
