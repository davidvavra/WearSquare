package cz.destil.wearsquare.api;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Processor for 4sq venues/explore API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public interface ExploreVenues {

    public static int LIMIT_VENUES = 7;

    @GET("/venues/explore?openNow=1&sortByDistance=1&venuePhotos=1&limit=" + LIMIT_VENUES)
    void best(@Query("ll") String ll, Callback<ExploreVenuesResponse> callback);

    public static class ExploreVenuesResponse extends Api.FoursquareResponse {
        // TODO: don't hardcode it, find a generic solution
        private static final int GEAR_WATCH_PIXEL_SIZE = 320;
        FoursquareContent response;

        public List<Venue> getVenues() {
            List<Venue> venues = new ArrayList<Venue>();
            for (FoursquareItem item : response.groups.get(0).items) {
                venues.add(parseVenue(item));
            }
            return venues;
        }

        public static Venue parseVenue(FoursquareItem item) {
            String photo = null;
            if (item.venue.photos.groups.size() > 0) {
                FoursquarePhotoGroupItem groupItem = item.venue.photos.groups.get(0).items.get(0);
                photo = groupItem.prefix + GEAR_WATCH_PIXEL_SIZE + "x" + GEAR_WATCH_PIXEL_SIZE + groupItem.suffix;
            }
            String tip = (item.tips != null && item.tips.size() > 0) ? item.tips.get(0).text : "";
            String hours = (item.venue.hours != null) ? item.venue.hours.status : null;
            String categoryIcon = "";
            if (item.venue.categories.size() > 0) {
                FoursquareIcon icon = item.venue.categories.get(0).icon;
                categoryIcon = icon.prefix + "44" + icon.suffix;
            }
            return new Venue(item.venue.name, categoryIcon, photo, item.venue.location.distance,
                    item.venue.location.lat, item.venue.location.lng, hours, item.venue.id, tip);
        }
    }

    public static class ExploreVenueResponse extends Api.FoursquareResponse {
        FoursquareItem response;

        public Venue getVenue() {
            return ExploreVenuesResponse.parseVenue(response);
        }
    }

    public static class Venue {
        public String id;
        public String name;
        public String categoryIcon;
        public String imageUrl;
        public int distance;
        public double latitude;
        public double longitude;
        public String hours;
        public String tip;

        public Venue(String name, String categoryIcon, String imageUrl, int distance, double latitude, double longitude, String hours, String id,
                     String tip) {
            this.name = name;
            this.categoryIcon = categoryIcon;
            this.imageUrl = imageUrl;
            this.distance = distance;
            this.latitude = latitude;
            this.longitude = longitude;
            this.hours = hours;
            this.id = id;
            this.tip = tip;
        }

        @Override
        public String toString() {
            return "Venue{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", categoryIcon='" + categoryIcon + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", distance=" + distance +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", hours='" + hours + '\'' +
                    ", tip='" + tip + '\'' +
                    '}';
        }
    }

    // parsing classes:

    class FoursquareContent {
        List<FoursquareGroup> groups;
    }

    class FoursquareGroup {
        List<FoursquareItem> items;
    }

    class FoursquareItem {
        FoursquareVenue venue;
        List<FoursquareTip> tips;
    }

    class FoursquareTip {
        String text;
    }

    class FoursquareVenue {
        String id;
        String name;
        List<FoursquareCategory> categories;
        FoursquarePhotos photos;
        FoursquareLocation location;
        FoursquareHours hours;
    }

    class FoursquareHours {
        String status;
    }

    class FoursquareLocation {
        int distance;
        double lat;
        double lng;
    }

    class FoursquareCategory {
        FoursquareIcon icon;
    }

    class FoursquareIcon {
        String prefix;
        String suffix;
    }

    class FoursquarePhotos {
        List<FoursquarePhotoGroup> groups;
    }

    class FoursquarePhotoGroup {
        List<FoursquarePhotoGroupItem> items;
    }

    class FoursquarePhotoGroupItem {
        String prefix;
        String suffix;
    }
}