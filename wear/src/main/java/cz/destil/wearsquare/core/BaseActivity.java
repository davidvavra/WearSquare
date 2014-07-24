package cz.destil.wearsquare.core;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mariux.teleport.lib.TeleportClient;

import butterknife.ButterKnife;
import cz.destil.wearsquare.util.DebugLog;

public class BaseActivity extends Activity {

    private TeleportClient mTeleportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.d("onCreate");
        mTeleportClient = new TeleportClient(this);
        App.bus().register(this);
    }

    @Override
    protected void onDestroy() {
        App.bus().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DebugLog.d("onStart");
        mTeleportClient.connect();
                Wearable.NodeApi.getConnectedNodes(mTeleportClient.getGoogleApiClient()).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {


                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getNodes().size()>0) {
                            startConnected();
                        } else {
                            startDisconnected();
                        }
                    }
                });
    }

    protected void startDisconnected() {
        DebugLog.d("start disconnected");
    }

    protected void startConnected() {
        DebugLog.d("start connected");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugLog.d("onStop");
        mTeleportClient.disconnect();
    }

    public TeleportClient teleport() {
        return mTeleportClient;
    }

    public boolean isConnected() {
        return mTeleportClient.getGoogleApiClient().isConnected();
    }
}
