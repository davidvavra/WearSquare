package cz.destil.wearsquare.fragment;

import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.Bind;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.activity.CheckInActivity;
import cz.destil.wearsquare.core.BaseFragment;
import cz.destil.wearsquare.event.PageChangedEvent;

/**
 * Fragment which actually performs a check-in after timeout.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CheckInFragment extends BaseFragment {

    @Bind(R.id.confirmation)
    DelayedConfirmationView vConfirmation;
    @Bind(R.id.name)
    TextView vName;
    @Bind(R.id.checking_in)
    TextView vCheckinIn;
    @Bind(R.id.delayed_box)
    LinearLayout vDelayedBox;
    @Bind(R.id.confirm_box)
    RelativeLayout vConfirmBox;
    @Bind(R.id.cancel)
    CircledImageView vCancel;
    @Bind(R.id.ok)
    CircledImageView vOk;

    private boolean mTimerInvalid;

    public static CheckInFragment create(String venueName) {
        CheckInFragment fragment = new CheckInFragment();
        Bundle args = new Bundle();
        args.putString("VENUE_NAME", venueName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_check_in;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vName.setText(getArguments().getString("VENUE_NAME"));
        setupListeners();
        vConfirmBox.setVisibility(View.GONE);
        vDelayedBox.setVisibility(View.VISIBLE);
        startConfirmation();
    }

    private void setupListeners() {
        vConfirmation.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                if (!mTimerInvalid) {
                    vCheckinIn.setVisibility(View.INVISIBLE);
                    ((CheckInActivity)getActivity()).sendCheckInMessage();
                }
            }

            @Override
            public void onTimerSelected(View view) {
                mTimerInvalid = true;
                getActivity().finish();
            }
        });
        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        vOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CheckInActivity)getActivity()).sendCheckInMessage();
            }
        });
    }

    private void startConfirmation() {
        vConfirmation.setTotalTimeMs(3000);
        vConfirmation.start();
        mTimerInvalid = false;
        vCheckinIn.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onPageChanged(PageChangedEvent e) {
        vConfirmBox.setVisibility(View.VISIBLE);
        vDelayedBox.setVisibility(View.GONE);
        mTimerInvalid = true;
    }
}
