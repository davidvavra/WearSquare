package cz.destil.wearsquare.event;


import java.util.List;

import cz.destil.wearsquare.adapter.CheckInAdapter;

public class VenueSearchEvent {
    public List<CheckInAdapter.Venue> getVenues() {
        return venues;
    }

    private List<CheckInAdapter.Venue> venues;

    public VenueSearchEvent(List<CheckInAdapter.Venue> venues) {
        this.venues = venues;
    }
}
