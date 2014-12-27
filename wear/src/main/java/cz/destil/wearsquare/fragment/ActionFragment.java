package cz.destil.wearsquare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.CircledImageView;
import android.view.View;
import android.widget.TextView;

import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.core.BaseFragment;

/**
 * Fragment displaying big action and label, based on Wear Design Guidelines.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ActionFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.icon)
    CircledImageView vIcon;
    @InjectView(R.id.label)
    TextView vLabel;

    private Listener mListener;

    public static ActionFragment create(int iconResId, int labelResId, Listener listener) {
        ActionFragment fragment = new ActionFragment();
        fragment.mListener = listener;
        Bundle args = new Bundle();
        args.putInt("ICON", iconResId);
        args.putInt("LABEL", labelResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_action;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vIcon.setImageResource(getArguments().getInt("ICON"));
        vLabel.setText(getArguments().getInt("LABEL"));
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onActionPerformed();
    }

    public interface Listener {
        public void onActionPerformed();
    }
}
