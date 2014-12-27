package cz.destil.wearsquare.event;


import java.util.List;

import cz.destil.wearsquare.adapter.CheckInListAdapter;

/**
 * Otto event which is fired when list check-in venues is received.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CheckInVenueListEvent {
    public List<CheckInListAdapter.Venue> getVenues() {
        return venues;
    }

    private List<CheckInListAdapter.Venue> venues;

    public CheckInVenueListEvent(List<CheckInListAdapter.Venue> venues) {
        this.venues = venues;
    }
}
