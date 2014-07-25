/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.destil.wearsquare.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.ImageReference;

import java.util.List;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a
 * different background is provided.
 */
public class ExploreAdapter extends FragmentGridPagerAdapter {

    private List<Venue> items;

    public ExploreAdapter(FragmentManager fm, List<ExploreAdapter.Venue> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Venue venue = items.get(row);
        CardFragment fragment = CardFragment.create(venue.name, venue.tip);
        fragment.setExpansionEnabled(true);
        return fragment;
    }

    @Override
    public ImageReference getBackground(int row, int column) {
        Bitmap photo = items.get(row).photo;
        return photo == null ? null : ImageReference.forBitmap(photo);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return 1;
    }

    public static class Venue {
        private String id;
        private String name;
        private String tip;
        private Bitmap photo;

        public Venue(String id, String name, String tip, Bitmap photo) {
            this.id = id;
            this.name = name;
            this.tip = tip;
            this.photo = photo;
        }
    }
}
