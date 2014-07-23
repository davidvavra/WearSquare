package cz.destil.wearsquare.service;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.mariux.teleport.lib.TeleportService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import cz.destil.wearsquare.api.Api;
import cz.destil.wearsquare.api.SearchVenues;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.util.DebugLog;
import cz.destil.wearsquare.util.ImageUtils;
import cz.destil.wearsquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FoursquareService extends TeleportService {

    @Override
    public void onCreate() {
        super.onCreate();
        setOnGetMessageTask(new ListenForMessageTask());
    }

    class ListenForMessageTask extends OnGetMessageTask {
        @Override
        protected void onPostExecute(String path) {
            if (path.equals("/start")) {
                DebugLog.d("service called from wear");
                Api.get().create(SearchVenues.class).searchForCheckIn(LocationUtils.getLastLocation(),
                        new Callback<SearchVenues.SearchResponse>() {

                            @Override
                            public void success(SearchVenues.SearchResponse searchResponse, Response response) {
                                DebugLog.d("success=" + searchResponse.getVenues());
                                syncToWear(searchResponse.getVenues());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                DebugLog.e(error.getMessage());
                            }
                        }
                );

                setOnGetMessageTask(new ListenForMessageTask());
            }
        }
    }

    private void syncToWear(final List<SearchVenues.Venue> venues) {
        final ArrayList<DataMap> dataVenues = new ArrayList<DataMap>();
        for (final SearchVenues.Venue venue : venues) {
            final DataMap dataMap = new DataMap();
            dataMap.putString("id", venue.id);
            dataMap.putString("name", venue.name);
            dataVenues.add(dataMap);
            Picasso.with(App.get()).load(venue.getCategoryIconUrl()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    DebugLog.d("bitmap loaded for " + venue.name);
                    Asset icon = ImageUtils.createAssetFromBitmap(bitmap);
                    dataMap.putAsset("icon", icon);
                    possiblySendVenues(dataVenues, venues.size());
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    DebugLog.w("bitmap failed for " + venue.name);
                    possiblySendVenues(dataVenues, venues.size());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    int bitmapsDownloaded = 0;

    private synchronized void possiblySendVenues(ArrayList<DataMap> dataVenues, int numberOfVenues) {
        bitmapsDownloaded++;
        if (bitmapsDownloaded >= numberOfVenues) {
            DebugLog.d("Sending checkin venues to wear");

            final PutDataMapRequest data = PutDataMapRequest.create("/check-in");
            data.getDataMap().putDataMapArrayList("venues", dataVenues);
            syncDataItem(data);
        }
    }
}
