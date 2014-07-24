package cz.destil.wearsquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.util.DebugLog;

public class CheckInActivity extends BaseActivity {

    @InjectView(R.id.confirmation)
    DelayedConfirmationView vConfirmation;
    @InjectView(R.id.name)
    TextView vName;

    public static void call(Activity activity, String venueId, String venueName) {
        Intent intent = new Intent(activity, CheckInActivity.class);
        intent.putExtra("VENUE_ID", venueId);
        intent.putExtra("VENUE_NAME", venueName);
        activity.startActivity(intent);
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
        vConfirmation.setTotalTimeMs(3000);
        vConfirmation.start();
        vConfirmation.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {

            }

            @Override
            public void onTimerSelected(View view) {
                finish();
            }
        });
    }

}
