package cz.destil.wearsquare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.ConfirmationActivity;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.adapter.EmojiAdapter;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.event.ExitEvent;
import cz.destil.wearsquare.event.PageChangedEvent;

/**
 * Displays a timer which performs check-in after period or inactivity. Or cancels check-in when user presses the timer.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CheckInActivity extends GridPagerActivity {

    public static final int CONFIRM_ACTIVITY = 42;

    public static void call(Activity activity, String venueId, String venueName) {
        Intent intent = new Intent(activity, CheckInActivity.class);
        intent.putExtra("VENUE_ID", venueId);
        intent.putExtra("VENUE_NAME", venueName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new CheckInAdapter(this, getIntent().getStringExtra("VENUE_NAME")));
        setPagerListener(new PagerListener() {
            @Override
            public void onPageChanged() {
                App.bus().post(new PageChangedEvent());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIRM_ACTIVITY) {
            finish();
            App.bus().post(new ExitEvent());
        }
    }

    public void sendCheckInMessage() {
        teleport().sendMessage("check-in/" + getIntent().getStringExtra("VENUE_ID") + "/" + EmojiAdapter.getSelectedEmoji(), null);
        Intent i = new Intent(this, ConfirmationActivity.class);
        i.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        i.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.checked_in));
        startActivityForResult(i, CheckInActivity.CONFIRM_ACTIVITY);
        vibrate();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 150, 75, 150};
        vibrator.vibrate(vibrationPattern, -1 /* don't repeat */);
    }
}
