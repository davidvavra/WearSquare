package cz.destil.wearsquare.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.destil.wearsquare.BuildConfig;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.PackageUtils;
import cz.destil.wearsquare.util.ToastUtil;

/**
 * This activity is displayed in the phone, it's used for 4sq login and general information.
 *
 * @author David Vávra (david@vavra.me)
 */
public class PhoneActivity extends BaseActivity implements BillingProcessor.IBillingHandler {

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
    @InjectView(R.id.donation)
    Button vDonation;

    BillingProcessor mBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.inject(this);
        setupAbout();
        mBilling = new BillingProcessor(this, null, this);
        init();
    }

    @Override
    public void onDestroy() {
        if (mBilling != null) {
            mBilling.release();
        }
        super.onDestroy();
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
        switch (item.getItemId()) {
            case R.id.action_logout:
                Preferences.clearFoursquareToken();
                init();
                break;
            case R.id.action_settings:
                SettingsActivity.call(this);
                break;
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
            if (mBilling.isInitialized()) {
                vDonation.setVisibility(View.VISIBLE);
            }
        } else {
            vLoginBox.setVisibility(View.VISIBLE);
            vInstructionsBox.setVisibility(View.GONE);
            vDonation.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    @OnClick(R.id.foursquare_button)
    void connectFoursquare() {
        try {
            Intent intent = FoursquareOAuth.getConnectIntent(this, BuildConfig.CLIENT_ID);
            startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show(R.string.no_google_play);
        }
    }

    @OnClick(R.id.donation)
    void donate() {
        mBilling.purchase("beer");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBilling.handleActivityResult(requestCode, resultCode, data)) {
            switch (requestCode) {
                case REQUEST_CODE_FSQ_CONNECT:
                    AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
                    if (!TextUtils.isEmpty(codeResponse.getCode())) {
                        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, BuildConfig.CLIENT_ID,
                                BuildConfig.CLIENT_SECRET, codeResponse.getCode());
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

    @Override
    public void onProductPurchased(String s) {
        ToastUtil.show(R.string.thanks);
        mBilling.consumePurchase("beer");
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, Throwable throwable) {

    }

    @Override
    public void onBillingInitialized() {
        if (Preferences.hasFoursquareToken()) {
            vDonation.setVisibility(View.VISIBLE);
        }
    }
}
