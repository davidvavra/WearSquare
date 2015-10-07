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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.api.Api;
import cz.destil.wearsquare.api.CheckIns;
import cz.destil.wearsquare.api.ExploreVenues;
import cz.destil.wearsquare.api.SearchVenues;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.ImageUtils;
import cz.destil.wearsquare.util.IntentUtils;
import cz.destil.wearsquare.util.L;
import cz.destil.wearsquare.util.LocationUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

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
                downloadExploreList();
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
    }

    /**
     * Downloads explore list of venues.
     */
    private void downloadExploreList() {
        try {
            LocationUtils.getLastLocation(new LocationUtils.LocationListener() {
                @Override
                public void onLocationUpdate(String location) {
                    Call<ExploreVenues.ExploreVenuesResponse> call = Api.get().create(ExploreVenues.class).best(location);
                    call.enqueue(new Callback<ExploreVenues.ExploreVenuesResponse>() {
                                     @Override
                                     public void onResponse(Response<ExploreVenues.ExploreVenuesResponse> response, Retrofit retrofit) {
                                         syncExploreToWear(response.body().getVenues());
                                     }

                                     @Override
                                     public void onFailure(Throwable t) {
                                         sendError(getString(R.string.connect_to_internet));
                                     }
                                 }
                    );
                }
            });
        } catch (SecurityException e) {
            sendError(getString(R.string.please_grant_location_permission));
        }
    }

    /**
     * Downloads list of venues for a check-in.
     */
    private void downloadCheckInList() {
        try {
            LocationUtils.getLastLocation(new LocationUtils.LocationListener() {
                @Override
                public void onLocationUpdate(String location) {
                    Call<SearchVenues.SearchResponse> call = Api.get().create(SearchVenues.class).searchForCheckIn(location);
                    call.enqueue(new Callback<SearchVenues.SearchResponse>() {
                        @Override
                        public void onResponse(Response<SearchVenues.SearchResponse> response, Retrofit retrofit) {
                            syncCheckInListToWear(response.body().getVenues());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            sendError(getString(R.string.connect_to_internet));
                        }
                    });
                }
            });
        } catch (SecurityException e) {
            sendError(getString(R.string.please_grant_location_permission));
        }
    }

    /**
     * Actually pushes a check-in to 4sq.
     */
    private void sendCheckIn(String path) {
        Uri uri = Uri.parse(path);
        List<String> pathSegments = uri.getPathSegments();
        final String id = pathSegments.get(1);
        final String shout = pathSegments.size() > 2 ? pathSegments.get(2) : "";
        LocationUtils.getLastLocation(new LocationUtils.LocationListener() {
            @Override
            public void onLocationUpdate(String location) {
                Call<CheckIns.CheckInResponse> call = Api.get()
                        .create(CheckIns.class)
                        .add(id, location, LocationUtils.getLastAccuracy(),
                                LocationUtils.getLastAltitude(), Preferences.getBroadcast(), shout);
                call.enqueue(new Callback<CheckIns.CheckInResponse>() {
                    @Override
                    public void onResponse(Response<CheckIns.CheckInResponse> response, Retrofit retrofit) {
                        // ignore for now, maybe log it to some check-in log in the future
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        sendError(getString(R.string.connect_to_internet));
                    }
                });
            }
        });
    }

    /**
     * Sends error to wearable.
     */
    private void sendError(String message) {
        L.e(message);
        PutDataMapRequest data = PutDataMapRequest.createWithAutoAppendedId("/error");
        data.getDataMap().putString("error_message", message);
        syncDataItem(data);
    }

    /**
     * Pushes list of explore venues to wear and starts downloading images.
     */
    private void syncExploreToWear(final List<ExploreVenues.Venue> venues) {
        final ArrayList<DataMap> dataVenues = new ArrayList<>();
        Set<String> images = new HashSet<>();
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
        Set<String> images = new HashSet<>();
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
    private void downloadImages(Set<String> imageUrls) {
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
