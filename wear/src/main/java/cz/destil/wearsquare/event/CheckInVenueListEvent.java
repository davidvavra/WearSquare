package cz.destil.wearsquare.event;


import java.util.List;

import cz.destil.wearsquare.adapter.CheckInAdapter;
/**
 * Otto event which is fired when list check-in venues is received.
 *
 * @author David Vávra (david@vavra.me)
 */
public class CheckInVenueListEvent {
    public List<CheckInAdapter.Venue> getVenues() {
        return venues;
    }

    private List<CheckInAdapter.Venue> venues;

    public CheckInVenueListEvent(List<CheckInAdapter.Venue> venues) {
        this.venues = venues;
    }
}
