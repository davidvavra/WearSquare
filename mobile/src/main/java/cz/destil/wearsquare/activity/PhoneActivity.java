package cz.destil.wearsquare.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.mariux.teleport.lib.TeleportClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.api.Api;
import cz.destil.wearsquare.api.SearchVenues;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.util.ImageUtils;
import cz.destil.wearsquare.util.LocationUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PhoneActivity extends BaseActivity {

    @InjectView(R.id.test)
    TextView vTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.inject(this);
        listenForMessage();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenForMessage() {
        teleport().setOnGetMessageTask(new TeleportClient.OnGetMessageTask() {
            @Override
            protected void onPostExecute(String path) {
                if (path.equals("/start")) {
                    vTest.setText("Calling 4sq");
                    Api.get().create(SearchVenues.class).searchForCheckIn(LocationUtils.getLastLocation(),
                            new Callback<SearchVenues.SearchResponse>() {

                                @Override
                                public void success(SearchVenues.SearchResponse searchResponse, Response response) {
                                    vTest.setText(searchResponse.getVenues().toString());
                                    syncToWear(searchResponse.getVenues());
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    vTest.setText(error.getMessage());
                                }
                            }
                    );
                }
            }
        });
    }

    private void syncToWear(List<SearchVenues.Venue> venues) {
        for (final SearchVenues.Venue venue : venues) {
            Picasso.with(App.get()).load(venue.getCategoryIconUrl()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Asset icon = ImageUtils.createAssetFromBitmap(bitmap);
                    PutDataMapRequest data = PutDataMapRequest.create("/checkin/venue/" + venue.id);
                    data.getDataMap().putString("name", venue.name);
                    data.getDataMap().putAsset("icon", icon);
                    teleport().syncDataItem(data);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    PutDataMapRequest data = PutDataMapRequest.create("/checkin/venue/" + venue.id);
                    data.getDataMap().putString("name", venue.name);
                    teleport().syncDataItem(data);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }
}
