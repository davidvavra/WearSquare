package cz.destil.wearsquare.core;

import android.app.Activity;
import android.os.Bundle;

import com.mariux.teleport.lib.TeleportClient;

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

}
