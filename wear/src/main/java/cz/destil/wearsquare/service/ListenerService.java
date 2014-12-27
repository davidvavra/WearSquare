package cz.destil.wearsquare.service;

import android.graphics.Bitmap;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cz.destil.wearsquare.adapter.CheckInListAdapter;
import cz.destil.wearsquare.adapter.EmojiAdapter;
import cz.destil.wearsquare.adapter.ExploreAdapter;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.event.CheckInVenueListEvent;
import cz.destil.wearsquare.event.ErrorEvent;
import cz.destil.wearsquare.event.ExceptionEvent;
import cz.destil.wearsquare.event.ExploreVenueListEvent;
import cz.destil.wearsquare.event.ImageLoadedEvent;
import cz.destil.wearsquare.util.ExceptionHandler;

/**
 * Receives and processes all communication from the phone.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ListenerService extends TeleportService {

    @Override
    public void onCreate() {
        super.onCreate();
        App.bus().register(this);
        setOnSyncDataItemCallback(new OnSyncDataItemCallback() {
            @Override
            public void onDataSync(DataMap dataMap) {
                handleDataChanged(dataMap);
            }
        });
    }

    @Override
    public void onDestroy() {
        App.bus().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onUncaughtException(ExceptionEvent e) {
        ExceptionHandler.sendExceptionToPhone(e.getException(), this);
    }

    /**
     * Handles received data.
     */
    private void handleDataChanged(DataMap data) {
        if (data.containsKey("error_message")) {
            App.bus().post(new ErrorEvent(data.getString("error_message")));
        } else if (data.containsKey("check_in_venues")) {
            processCheckInList(data);
        } else if (data.containsKey("explore_venues")) {
            processExploreList(data);
        } else if (data.containsKey("image_url")) {
            Asset asset = data.getAsset("asset");
            if (asset != null) {
                String imageUrl = data.getString("image_url");
                new ProcessImageTask(imageUrl).execute(asset, getGoogleApiClient());
            }

        }
    }

    /**
     * Processes check-in list.
     */
    private void processCheckInList(DataMap data) {
        List<CheckInListAdapter.Venue> venues = new ArrayList<>();
        ArrayList<DataMap> dataVenues = data.getDataMapArrayList("check_in_venues");
        for (DataMap dataVenue : dataVenues) {
            venues.add(new CheckInListAdapter.Venue(dataVenue.getString("id"), dataVenue.getString("name"), dataVenue.getString("image_url")));
        }
        ArrayList<String> emojis = data.getDataMap("emojis").getStringArrayList("emojis");
        EmojiAdapter.setEmojis(emojis);
        App.bus().post(new CheckInVenueListEvent(venues));
    }

    /**
     * Processes explore list.
     */
    private void processExploreList(DataMap data) {
        List<ExploreAdapter.Venue> venues = new ArrayList<>();
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
    class ProcessImageTask extends cz.destil.wearsquare.util.ImageFromAssetTask {

        private String imageUrl;

        ProcessImageTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            App.bus().post(new ImageLoadedEvent(imageUrl, bitmap));
        }
    }
}
