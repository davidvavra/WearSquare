package cz.destil.wearsquare.adapter;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;

import java.util.List;

import cz.destil.wearsquare.R;
import cz.destil.wearsquare.activity.ExploreActivity;
import cz.destil.wearsquare.core.App;
import cz.destil.wearsquare.fragment.ActionFragment;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a
 * different background is provided.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ExploreAdapter extends FragmentGridPagerAdapter {

    private ExploreActivity activity;
    private List<Venue> items;

    public ExploreAdapter(ExploreActivity activity, List<ExploreAdapter.Venue> items) {
        super(activity.getFragmentManager());
        this.activity = activity;
        this.items = items;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        final Venue venue = items.get(row);
        switch (col) {
            case 0:
                CardFragment fragment = CardFragment.create(venue.name, venue.tip);
                fragment.setExpansionEnabled(true);
                // Add some extra bottom margin to leave room for the page indicator
                fragment.setCardMarginBottom(
                        activity.getResources().getDimensionPixelSize(R.dimen.card_margin_bottom));
                return fragment;
            case 1:
                return ActionFragment.create(R.drawable.ic_full_navigate, R.string.action_navigate, new ActionFragment.Listener() {
                    @Override
                    public void onActionPerformed() {
                        activity.navigate(venue);
                    }
                });
            case 2:
                return ActionFragment.create(R.drawable.ic_full_check_in, R.string.check_in, new ActionFragment.Listener() {
                    @Override
                    public void onActionPerformed() {
                        activity.checkIn(venue);
                    }
                });
            case 3:
                return ActionFragment.create(R.drawable.ic_full_open_on_phone, R.string.open_on_phone, new ActionFragment.Listener() {
                    @Override
                    public void onActionPerformed() {
                        activity.openOnPhone(venue);
                    }
                });
            default:
                return null;
        }

    }

    @Override
    public Drawable getBackgroundForRow(int row) {
        BitmapDrawable background = items.get(row).photo;
        if (background == null) {
            return GridPagerAdapter.BACKGROUND_NONE;
        } else {
            return background;
        }
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return 4;
    }

    public static class Venue {
        private String id;
        private String name;
        private String tip;
        private BitmapDrawable photo;
        private double latitude;
        private double longitude;
        private String imageUrl;

        public Venue(String id, String name, String tip, double latitude, double longitude, String imageUrl) {
            this.id = id;
            this.name = name;
            this.tip = tip;
            this.latitude = latitude;
            this.longitude = longitude;
            this.imageUrl = imageUrl;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTip() {
            return tip;
        }

        public BitmapDrawable getPhoto() {
            return photo;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setPhoto(Bitmap photo) {
            if (photo != null) {
                this.photo = new BitmapDrawable(App.get().getResources(), photo);
            }
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
