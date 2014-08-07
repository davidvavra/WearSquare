package cz.destil.wearsquare.adapter;

import android.support.wearable.view.GridPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
/**
 * Needed because of a bug in support library:
 * http://stackoverflow.com/questions/24752716/nullpointerexception-while-using-gridviewpager-class-on-android-wear
 *
 * @author David Vávra (david@vavra.me)
 */
public class EmptyGridPagerAdapter extends GridPagerAdapter {
    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    @Override
    protected Object instantiateItem(ViewGroup viewGroup, int i, int i2) {
        return null;
    }

    @Override
    protected void destroyItem(ViewGroup viewGroup, int i, int i2, Object o) {

    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return false;
    }
}
