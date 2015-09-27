package cz.destil.wearsquare.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mariux.teleport.lib.TeleportClient;

import cz.destil.wearsquare.R;

/**
 * Base activity for all others. It handles Teleport.
 *
 * @author David Vávra (david@vavra.me)
 */
public abstract class BaseActivity extends AppCompatActivity {

    private TeleportClient mTeleportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeleportClient = new TeleportClient(this);
        setContentView(R.layout.activity_base);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShowUpArrow());
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, getFragment()).commit();
        }
    }

    public abstract Fragment getFragment();

    public abstract boolean shouldShowUpArrow();

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
