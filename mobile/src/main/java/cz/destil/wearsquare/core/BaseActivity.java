package cz.destil.wearsquare.core;

import android.app.Activity;
import android.os.Bundle;

import com.mariux.teleport.lib.TeleportClient;
/**
 * Base activity for all others. It handles Teleport.
 *
 * @author David Vávra (david@vavra.me)
 */
public abstract class BaseActivity extends Activity {

    private TeleportClient mTeleportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeleportClient = new TeleportClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTeleportClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTeleportClient.disconnect();
    }

    public TeleportClient teleport() {
        return mTeleportClient;
    }
}
