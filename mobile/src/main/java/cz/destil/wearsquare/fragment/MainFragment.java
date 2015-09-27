package cz.destil.wearsquare.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.destil.wearsquare.BuildConfig;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.activity.SettingsActivity;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.ToastUtil;
import permissions.dispatcher.DeniedPermission;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import permissions.dispatcher.ShowsRationale;

/**
 * Main fragment in the phone.
 *
 * @author David Vávra (david@vavra.me)
 */
@RuntimePermissions
public class MainFragment extends Fragment implements BillingProcessor.IBillingHandler {

    private static final int REQUEST_CODE_FSQ_CONNECT = 42;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 43;

    @Bind(R.id.about)
    TextView vAbout;
    @Bind(R.id.login_box)
    LinearLayout vLoginBox;
    @Bind(R.id.instructions_box)
    LinearLayout vInstructionsBox;
    @Bind(R.id.donation)
    Button vDonation;

    BillingProcessor mBilling;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupAbout();
        mBilling = new BillingProcessor(getActivity(), null, this);
        MainFragmentPermissionsDispatcher.initWithCheck(this);
    }

    @Override
    public void onDestroyView() {
        if (mBilling != null) {
            mBilling.release();
        }
        super.onDestroyView();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_logout).setVisible(Preferences.hasFoursquareToken());
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Preferences.clearFoursquareToken();
                init();
                break;
            case R.id.action_settings:
                SettingsActivity.call(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupAbout() {
        vAbout.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void init() {
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
        getActivity().invalidateOptionsMenu();
    }

    @OnClick(R.id.foursquare_button)
    void connectFoursquare() {
        try {
            Intent intent = FoursquareOAuth.getConnectIntent(getContext(), BuildConfig.CLIENT_ID);
            startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show(R.string.no_google_play);
        }
    }

    @OnClick(R.id.donation)
    void donate() {
        mBilling.purchase(getActivity(), "beer");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBilling.handleActivityResult(requestCode, resultCode, data)) {
            switch (requestCode) {
                case REQUEST_CODE_FSQ_CONNECT:
                    AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
                    if (!TextUtils.isEmpty(codeResponse.getCode())) {
                        Intent intent = FoursquareOAuth.getTokenExchangeIntent(getContext(), BuildConfig.CLIENT_ID,
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
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainFragmentPermissionsDispatcher.
                onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @ShowsRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation() {
        ToastUtil.show(R.string.location_permission_rationale);
    }

    @DeniedPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        ToastUtil.show(R.string.location_permission_denied);
    }
}
