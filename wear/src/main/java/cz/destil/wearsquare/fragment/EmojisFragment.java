package cz.destil.wearsquare.fragment;

import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.Bind;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.activity.CheckInActivity;
import cz.destil.wearsquare.adapter.EmojiAdapter;
import cz.destil.wearsquare.core.BaseFragment;

/**
 * Fragment displaying one emoji which will be added to a check-in.
 *
 * @author David Vávra (david@vavra.me)
 */
public class EmojisFragment extends BaseFragment {

    private static String sSelectedEmoji = "";

    @Bind(R.id.list)
    WearableListView vList;
    @Bind(R.id.header)
    FrameLayout vHeader;
    @Bind(R.id.header_text)
    TextView vHeaderText;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_fragment_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vList.setGreedyTouchMode(true);
        EmojiAdapter mAdapter = new EmojiAdapter(getActivity());
        vList.setAdapter(mAdapter);
        vList.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                ((CheckInActivity)getActivity()).sendCheckInMessage();
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });
        vHeader.setVisibility(View.VISIBLE);
        vHeaderText.setText(R.string.sticker);
    }
}
