package cz.destil.wearsquare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.api.Hidden;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.PackageUtils;


public class PhoneActivity extends BaseActivity {

    private static final int REQUEST_CODE_FSQ_CONNECT = 42;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 43;

    @InjectView(R.id.about)
    TextView vAbout;
    @InjectView(R.id.mini_launcher_info)
    TextView vMiniLauncherInfo;
    @InjectView(R.id.login_box)
    LinearLayout vLoginBox;
    @InjectView(R.id.instructions_box)
    LinearLayout vInstructionsBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.inject(this);
        setupAbout();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_logout).setVisible(Preferences.hasFoursquareToken());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Preferences.clearFoursquareToken();
            init();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAbout() {
        vAbout.setMovementMethod(LinkMovementMethod.getInstance());
        vMiniLauncherInfo.setMovementMethod(LinkMovementMethod.getInstance());
        vMiniLauncherInfo.setVisibility(PackageUtils.isWearLauncherInstalled() ? View.GONE : View.VISIBLE);
    }

    private void init() {
        if (Preferences.hasFoursquareToken()) {
            vLoginBox.setVisibility(View.GONE);
            vInstructionsBox.setVisibility(View.VISIBLE);
        } else {
            vLoginBox.setVisibility(View.VISIBLE);
            vInstructionsBox.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    @OnClick(R.id.foursquare_button)
    void connectFoursquare() {
        Intent intent = FoursquareOAuth.getConnectIntent(this, Hidden.CLIENT_ID);
        startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT:
                AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
                if (!TextUtils.isEmpty(codeResponse.getCode())) {
                    Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, Hidden.CLIENT_ID,
                            Hidden.CLIENT_SECRET, codeResponse.getCode());
                    startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
                }
                break;
            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
                AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
                if (!TextUtils.isEmpty(tokenResponse.getAccessToken())) {
                    Preferences.setFoursquareToken(tokenResponse.getAccessToken());
                    init();
                }
                break;
        }
    }


}
