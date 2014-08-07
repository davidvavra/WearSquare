package cz.destil.wearsquare.event;


import java.util.List;

import cz.destil.wearsquare.adapter.ExploreAdapter;
/**
 * Otto event which is fired when list of explore venues is received.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ExploreVenueListEvent {
    public List<ExploreAdapter.Venue> getVenues() {
        return venues;
    }

    private List<ExploreAdapter.Venue> venues;

    public ExploreVenueListEvent(List<ExploreAdapter.Venue> venues) {
        this.venues = venues;
    }
}
