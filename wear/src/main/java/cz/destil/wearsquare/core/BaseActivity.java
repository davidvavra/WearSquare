package cz.destil.wearsquare.core;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mariux.teleport.lib.TeleportClient;

import cz.destil.wearsquare.util.DebugLog;

public class BaseActivity extends Activity {

    private TeleportClient mTeleportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeleportClient = new TeleportClient(this);
        mTeleportClient.getGoogleApiClient().registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                DebugLog.d("on connected");
                BaseActivity.this.onConnected();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    protected void onConnected() {
        // override in children
    }

    @Override
    protected void onStart() {
        super.onStart();
        DebugLog.d("connecting");
        mTeleportClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugLog.d("disconnecting");
        //TODO mTeleportClient.disconnect();
    }

    public TeleportClient teleport() {
        return mTeleportClient;
    }
}
