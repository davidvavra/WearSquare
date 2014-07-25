package cz.destil.wearsquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.event.ExitEvent;
import cz.destil.wearsquare.util.DebugLog;

public class CheckInActivity extends BaseActivity {


    private static final int CONFIRM_ACTIVITY = 42;
    @InjectView(R.id.confirmation)
    DelayedConfirmationView vConfirmation;
    @InjectView(R.id.name)
    TextView vName;
    private boolean mTimerSelected;

    public static void call(Activity activity, String venueId, String venueName) {
        Intent intent = new Intent(activity, CheckInActivity.class);
        intent.putExtra("VENUE_ID", venueId);
        intent.putExtra("VENUE_NAME", venueName);
        activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLog.d("onActivityResult");
        finish();
        App.bus().post(new ExitEvent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.inject(this);
        vName.setText(getIntent().getStringExtra("VENUE_NAME"));
        startConfirmation();
    }

    private void startConfirmation() {
        DebugLog.d("starting timer");
        vConfirmation.setTotalTimeMs(2500);
        vConfirmation.start();
        mTimerSelected = false;
        vConfirmation.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                if (!mTimerSelected) {
                    teleport().sendMessage("check-in/"+getIntent().getStringExtra("VENUE_ID"), null);
                    Intent i = new Intent(CheckInActivity.this, ConfirmationActivity.class);
                    i.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                    i.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.checked_in));
                    startActivityForResult(i, CONFIRM_ACTIVITY);
                }
            }

            @Override
            public void onTimerSelected(View view) {
                mTimerSelected = true;
                finish();
            }
        });
    }

}
