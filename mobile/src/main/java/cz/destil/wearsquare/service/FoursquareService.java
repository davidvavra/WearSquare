package cz.destil.wearsquare.service;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.SparseArray;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.mariux.teleport.lib.TeleportService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
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
import cz.destil.wearsquare.util.IntentUtils;
import cz.destil.wearsquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Receives all communication from wearable and processes it.
 *
 * @author David Vávra (david@vavra.me)
 */
public class FoursquareService extends TeleportService {

    SparseArray<Target> mTargets; // need to hold strong reference to targets, because Picasso holds WeakReferences

    @Override
    public void onCreate() {
        super.onCreate();
        setOnGetMessageCallback(new OnGetMessageCallback() {
            @Override
            public void onCallback(String path) {
                handleMessage(path);
            }
        });
    }

    /**
     * Handle all messages here.
     */
    private void handleMessage(String path) {
        try {
            if (path.equals("/check-in-list")) {
                if (Preferences.hasFoursquareToken()) {
                    downloadCheckInList();
                } else {
                    sendError(getString(R.string.please_connect_foursquare_first));
                }
            } else if (path.startsWith("check-in")) {
                sendCheckIn(path);
            } else if (path.startsWith("/explore-list")) {
                if (Preferences.hasFoursquareToken()) {
                    downloadExploreList(path);
                } else {
                    sendError(getString(R.string.please_connect_foursquare_first));
                }
            } else if (path.startsWith("/navigate")) {
                IntentUtils.launchNavigation(path);
            } else if (path.startsWith("/open")) {
                IntentUtils.openOnPhone(path);
            } else if (path.startsWith("exception:")) {
                IntentUtils.sendEmail(path.split("exception:")[1]);
            }
        } catch (LocationUtils.LocationNotFoundException e) {
            sendError(getString(R.string.no_location));
        }
    }

    /**
     * Downloads explore list of venues.
     */
    private void downloadExploreList(String path) throws LocationUtils.LocationNotFoundException {
        Api.get().create(ExploreVenues.class).best(LocationUtils.getLastLocation(),
                new Callback<ExploreVenues.ExploreVenuesResponse>() {
                    @Override
                    public void success(ExploreVenues.ExploreVenuesResponse exploreVenuesResponse, Response response) {
                        syncExploreToWear(exploreVenuesResponse.getVenues());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(error.isNetworkError() ? getString(R.string.connect_to_internet) : error.getMessage());
                    }
                }
        );
    }

    /**
     * Downloads list of venues for a check-in.
     */
    private void downloadCheckInList() throws LocationUtils.LocationNotFoundException {
        Api.get().create(SearchVenues.class).searchForCheckIn(LocationUtils.getLastLocation(),
                new Callback<SearchVenues.SearchResponse>() {

                    @Override
                    public void success(SearchVenues.SearchResponse searchResponse, Response response) {
                        syncCheckInListToWear(searchResponse.getVenues());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(error.isNetworkError() ? getString(R.string.connect_to_internet) : error.getMessage
                                ());
                    }
                }
        );
    }

    /**
     * Actually pushes a check-in to 4sq.
     */
    private void sendCheckIn(String path) throws LocationUtils.LocationNotFoundException {
        Uri uri = Uri.parse(path);
        List<String> pathSegments = uri.getPathSegments();
        String id = pathSegments.get(1);
        String shout = pathSegments.size() > 2 ? pathSegments.get(2) : "";
        Api.get().create(CheckIns.class).add(id, LocationUtils.getLastLocation(), LocationUtils.getLastAccuracy(),
                LocationUtils.getLastAltitude(), Preferences.getBroadcast(), shout,
                new Callback<CheckIns.CheckInResponse>() {
                    @Override
                    public void success(CheckIns.CheckInResponse checkInResponse, Response response) {
                        // ignore for now, maybe log it to some check-in log in the future
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        sendError(error.isNetworkError() ? getString(R.string.connect_to_internet) : error.getMessage
                                ());
                    }
                }
        );
    }

    /**
     * Sends error to wearable.
     */
    private void sendError(String message) {
        DebugLog.e(message);
        PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/error");
        data.getDataMap().putString("error_message", message);
        syncDataItem(data);
    }

    /**
     * Pushes list of explore venues to wear and starts downloading images.
     */
    private void syncExploreToWear(final List<ExploreVenues.Venue> venues) {
        final ArrayList<DataMap> dataVenues = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (final ExploreVenues.Venue venue : venues) {
            final DataMap dataMap = new DataMap();
            dataMap.putString("id", venue.id);
            dataMap.putString("name", venue.name);
            dataMap.putString("tip", venue.tip);
            dataMap.putDouble("latitude", venue.latitude);
            dataMap.putDouble("longitude", venue.longitude);
            if (venue.imageUrl != null) {
                dataMap.putString("image_url", venue.imageUrl);
                images.add(venue.imageUrl);
            }
            dataVenues.add(dataMap);
        }
        PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/explore-list");
        data.getDataMap().putDataMapArrayList("explore_venues", dataVenues);
        syncDataItem(data);
        downloadImages(images);
    }

    /**
     * Pushes check-in list of venues to wearable and starts downloading images.
     */
    private void syncCheckInListToWear(final List<SearchVenues.Venue> venues) {
        final ArrayList<DataMap> dataVenues = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (final SearchVenues.Venue venue : venues) {
            final DataMap dataMap = new DataMap();
            dataMap.putString("id", venue.id);
            dataMap.putString("name", venue.name);
            dataMap.putString("image_url", venue.getCategoryIconUrl());
            dataVenues.add(dataMap);
            images.add(venue.getCategoryIconUrl());
        }
        PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/check-in-list");
        data.getDataMap().putDataMapArrayList("check_in_venues", dataVenues);
        DataMap emojiMap = new DataMap();
        emojiMap.putStringArrayList("emojis", Preferences.getEmojis());
        data.getDataMap().putDataMap("emojis", emojiMap);
        syncDataItem(data);
        downloadImages(images);
    }

    /**
     * Downloads images in parallel and pushes them to wearable as Assets.
     */
    private void downloadImages(List<String> imageUrls) {
        int i = 0;
        mTargets = new SparseArray<>(); // needs strong reference
        for (final String imageUrl : imageUrls) {
            mTargets.put(i, new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Asset asset = ImageUtils.createAssetFromBitmap(bitmap);
                    final PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/image");
                    data.getDataMap().putString("image_url", imageUrl);
                    data.getDataMap().putAsset("asset", asset);
                    syncDataItem(data);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    final PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/image");
                    data.getDataMap().putString("image_url", imageUrl);
                    syncDataItem(data);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            Picasso.with(App.get()).load(imageUrl).into(mTargets.get(i));
            i++;
        }
    }
}
