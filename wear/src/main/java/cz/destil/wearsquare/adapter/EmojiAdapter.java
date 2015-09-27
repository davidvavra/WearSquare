package cz.destil.wearsquare.adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.destil.wearsquare.R;

/**
 * Adapter for selecting emojis.
 *
 * @author David Vávra (david@vavra.me)
 */
public class EmojiAdapter extends WearableListView.Adapter {

    private static List<String> sEmojis;
    private static String sSelectedEmoji;
    private Context mContext;

    public EmojiAdapter(Context context) {
        mContext = context;
    }

    public static void setEmojis(ArrayList<String> emojis) {
        sEmojis = emojis;
    }

    public static String getSelectedEmoji() {
        return sSelectedEmoji;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(new ListItem(mContext));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        String emoji = sEmojis.get(position);
        ListItem listItem = (ListItem) viewHolder.itemView;
        listItem.setTag(emoji);
        if (TextUtils.isEmpty(emoji)) {
            listItem.emoji.setText(R.string.no_emoji);
            listItem.emoji.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        } else {
            listItem.emoji.setText(emoji);
            listItem.emoji.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        }
    }

    @Override
    public int getItemCount() {
        return sEmojis.size();
    }

    class ListItem extends FrameLayout implements WearableListView.OnCenterProximityListener {

        @Bind(R.id.emoji)
        TextView emoji;

        public ListItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_emoji, this);
            ButterKnife.bind(this, this);
        }

        @Override
        public void onCenterPosition(boolean b) {
            emoji.setAlpha(1f);
            sSelectedEmoji = (String) getTag();
        }

        @Override
        public void onNonCenterPosition(boolean b) {
            emoji.setAlpha(0.5f);
        }
    }
}