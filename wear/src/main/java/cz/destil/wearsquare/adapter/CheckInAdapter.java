package cz.destil.wearsquare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;

public class CheckInAdapter extends WearableListView.Adapter {

    private float mDefaultCircleRadius;
    private float mSelectedCircleRadius;
    private int mDefaultCircleColor;
    private int mSelectedCircleColor;
    private Context mContext;
    private List<Venue> items;

    public CheckInAdapter(Context context, List<Venue> items) {
        mContext = context;
        this.items = items;
        mDefaultCircleRadius = context.getResources().getDimension(R.dimen.default_settings_circle_radius);
        mSelectedCircleRadius = context.getResources().getDimension(R.dimen.selected_settings_circle_radius);
        mDefaultCircleColor = context.getResources().getColor(R.color.medium_gray);
        mSelectedCircleColor = context.getResources().getColor(R.color.brand);
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

    class ListItem extends FrameLayout implements WearableListView.Item {

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
        public float getProximityMinValue() {
            return mDefaultCircleRadius;
        }

        @Override
        public float getProximityMaxValue() {
            return mSelectedCircleRadius;
        }

        @Override
        public float getCurrentProximityValue() {
            return image.getCircleRadius();
        }

        @Override
        public void setScalingAnimatorValue(float value) {
            image.setCircleRadius(value);
            image.setCircleRadiusPressed(value);
        }

        @Override
        public void onScaleUpStart() {
            image.setAlpha(1f);
            text.setAlpha(1f);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            image.setCircleColor(mSelectedCircleColor);
        }

        @Override
        public void onScaleDownStart() {
            image.setAlpha(0.5f);
            text.setAlpha(0.5f);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            image.setCircleColor(mDefaultCircleColor);
        }
    }

    public static class Venue {
        String id;
        String name;
        Bitmap icon;

        public Venue(String id, String name, Bitmap icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }
    }
}