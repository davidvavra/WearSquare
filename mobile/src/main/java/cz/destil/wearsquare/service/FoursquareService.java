package cz.destil.wearsquare.service;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.mariux.teleport.lib.TeleportService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.api.Api;
import cz.destil.wearsquare.api.CheckIns;
import cz.destil.wearsquare.api.ExploreVenues;
import cz.destil.wearsquare.api.SearchVenues;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.DebugLog;
import cz.destil.wearsquare.util.ImageUtils;
import cz.destil.wearsquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FoursquareService extends TeleportService {

    // for image downloading:
    HashMap<String, Target> mTargets; // need to hold strong reference to targets, because Picasso holds WeakReferences
    int mBitmapsDownloaded;
    String mPath;
    String mKey;
    ArrayList<DataMap> mDataVenues;
    int mNumberOfBitmaps;

    @Override
    public void onCreate() {
        super.onCreate();
        DebugLog.d("Service started");
        setOnGetMessageTask(new ListenForMessageTask());
    }

    class ListenForMessageTask extends OnGetMessageTask {
        @Override
        protected void onPostExecute(String path) {
            if (path.equals("/check-in-list")) {
                DebugLog.d("downloading venues");
                if (Preferences.hasFoursquareToken()) {
                    downloadCheckInList();
                } else {
                    sendError(getString(R.string.please_connect_foursquare_first));
                }
            } else if (path.startsWith("check-in")) {
                DebugLog.d("sending check in");
                sendCheckIn(path);
            } else if (path.equals("/explore-list")) {
                DebugLog.d("downloading explore");
                if (Preferences.hasFoursquareToken()) {
                    downloadExploreList();
                } else {
                    sendError(getString(R.string.please_connect_foursquare_first));
                }
            }
            setOnGetMessageTask(new ListenForMessageTask());
        }
    }

    private void downloadExploreList() {
        Api.get().create(ExploreVenues.class).best(LocationUtils.getLastLocation(), new Callback<ExploreVenues.ExploreVenuesResponse>() {
            @Override
            public void success(ExploreVenues.ExploreVenuesResponse exploreVenuesResponse, Response response) {
                DebugLog.d("success=" + exploreVenuesResponse.getVenues());
                syncExploreToWear(exploreVenuesResponse.getVenues());
            }

            @Override
            public void failure(RetrofitError error) {
                sendError(error.getMessage());
            }
        });
    }

    private void downloadCheckInList() {
        Api.get().create(SearchVenues.class).searchForCheckIn(LocationUtils.getLastLocation(),
                new Callback<SearchVenues.SearchResponse>() {

                    @Override
                    public void success(SearchVenues.SearchResponse searchResponse, Response response) {
                        DebugLog.d("success=" + searchResponse.getVenues());
                        syncCheckInListToWear(searchResponse.getVenues());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(error.getMessage());
                    }
                }
        );
    }

    private void sendCheckIn(String path) {
        Uri uri = Uri.parse(path);
        String id = uri.getLastPathSegment();
        Api.get().create(CheckIns.class).add(id, LocationUtils.getLastLocation(), LocationUtils.getLastAccuracy(), LocationUtils.getLastAltitude(),
                new Callback<CheckIns.CheckInResponse>() {
                    @Override
                    public void success(CheckIns.CheckInResponse checkInResponse, Response response) {
                        DebugLog.d("check in successful");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(error.getMessage());
                    }
                });
    }

    private void sendError(String message) {
        DebugLog.e(message);
        PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/error");
        data.getDataMap().putString("error_message", message);
        syncDataItem(data);
    }

    private void syncExploreToWear(final List<ExploreVenues.Venue> venues) {
        final ArrayList<DataMap> dataVenues = new ArrayList<DataMap>();
        List<String> images = new ArrayList<String>();
        for (final ExploreVenues.Venue venue : venues) {
            final DataMap dataMap = new DataMap();
            dataMap.putString("id", venue.id);
            dataMap.putString("name", venue.name);
            dataMap.putString("tip", venue.tip);
            dataVenues.add(dataMap);
            images.add(venue.imageUrl);
        }
        downloadImagesAndSync(images, "photo", dataVenues, "/explore-list", "explore_venues");
    }

    private void syncCheckInListToWear(final List<SearchVenues.Venue> venues) {
        final ArrayList<DataMap> dataVenues = new ArrayList<DataMap>();
        List<String> images = new ArrayList<String>();
        for (final SearchVenues.Venue venue : venues) {
            final DataMap dataMap = new DataMap();
            dataMap.putString("id", venue.id);
            dataMap.putString("name", venue.name);
            dataVenues.add(dataMap);
            images.add(venue.getCategoryIconUrl());
        }
        downloadImagesAndSync(images, "icon", dataVenues, "/check-in-list", "check_in_venues");
    }

    /**
     * Downloads images in parallel and synces everything to Wear when complete.
     */
    private void downloadImagesAndSync(List<String> imageUrls, String assetKey, ArrayList<DataMap> dataVenues, String path, String key) {
        mTargets = new HashMap<String, Target>();
        mDataVenues = dataVenues;
        mBitmapsDownloaded = 0;
        mPath = path;
        mKey = key;
        mNumberOfBitmaps = imageUrls.size();
        int i = 0;
        for (String imageUrl : imageUrls) {
            downloadImage(imageUrl, dataVenues.get(i), assetKey);
            i++;
        }
    }

    private void downloadImage(final String imageUrl, final DataMap dataMap, final String assetKey) {
        if (TextUtils.isEmpty(imageUrl)) {
            possiblySync();
        } else {
            mTargets.put(imageUrl, new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    DebugLog.d(assetKey + " bitmap loaded for " + imageUrl);
                    Asset asset = ImageUtils.createAssetFromBitmap(bitmap);
                    dataMap.putAsset(assetKey, asset);
                    possiblySync();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    DebugLog.w(assetKey + " bitmap failed for " + imageUrl);
                    possiblySync();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            Picasso.with(App.get()).load(imageUrl).into(mTargets.get(imageUrl));
        }
    }


    private synchronized void possiblySync() {
        mBitmapsDownloaded++;
        if (mBitmapsDownloaded >= mNumberOfBitmaps) {
            DebugLog.d("Sending venues to wear");
            final PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId(mPath);
            data.getDataMap().putDataMapArrayList(mKey, mDataVenues);
            syncDataItem(data);
            mTargets = null;
            mDataVenues = null;
        }
    }
}
