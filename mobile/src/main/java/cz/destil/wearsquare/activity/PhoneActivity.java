package cz.destil.wearsquare.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.api.Hidden;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.DebugLog;


public class PhoneActivity extends BaseActivity {

    private static final int REQUEST_CODE_FSQ_CONNECT = 42;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 43;

    @InjectView(R.id.primary)
    TextView vPrimary;
    @InjectView(R.id.foursquare_button)
    Button vFoursquareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.inject(this);
        ButterKnife.inject(this);
        vPrimary.setMovementMethod(LinkMovementMethod.getInstance());
        init();
    }

    private void init() {
        if (Preferences.hasFoursquareToken()) {
            vPrimary.setText(R.string.main_message);
            vFoursquareButton.setVisibility(View.GONE);
        } else {
            vPrimary.setText(R.string.login_message);
        }
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
