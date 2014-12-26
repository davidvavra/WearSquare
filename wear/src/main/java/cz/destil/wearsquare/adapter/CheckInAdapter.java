package cz.destil.wearsquare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;

/**
 * Adapter for check-in list.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CheckInAdapter extends WearableListView.Adapter {

    private int mDefaultCircleColor;
    private int mSelectedCircleColor;
    private Context mContext;
    private List<Venue> items;

    public CheckInAdapter(Context context, List<Venue> items) {
        mContext = context;
        this.items = items;
        mDefaultCircleColor = context.getResources().getColor(R.color.medium_gray);
        mSelectedCircleColor = context.getResources().getColor(R.color.action_button);
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(new ListItem(mContext));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        Venue venue = items.get(position);
        ListItem listItem = (ListItem) viewHolder.itemView;
        TextView text = (TextView) listItem.findViewById(R.id.text);
        text.setText(venue.name);
        CircledImageView image = (CircledImageView) listItem.findViewById(R.id.image);
        image.setImageDrawable(new BitmapDrawable(mContext.getResources(), venue.icon));
        listItem.setTag(venue.id);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ListItem extends FrameLayout implements WearableListView.OnCenterProximityListener {

        @InjectView(R.id.image)
        CircledImageView image;
        @InjectView(R.id.text)
        TextView text;

        public ListItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_check_in, this);
            ButterKnife.inject(this, this);
        }

        @Override
        public void onCenterPosition(boolean b) {
            text.setAlpha(1f);
            image.setCircleColor(mSelectedCircleColor);
            image.setAlpha(1f);
            image.setScaleX(1f);
            image.setScaleY(1f);
        }

        @Override
        public void onNonCenterPosition(boolean b) {
            text.setAlpha(0.5f);
            image.setCircleColor(mDefaultCircleColor);
            image.setAlpha(0.5f);
            image.setScaleX(0.8f);
            image.setScaleY(0.8f);
        }
    }

    public static class Venue {
        private String id;
        private String name;
        private String imageUrl;
        private Bitmap icon;

        public Venue(String id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setIcon(Bitmap icon) {
            this.icon = icon;
        }
    }
}