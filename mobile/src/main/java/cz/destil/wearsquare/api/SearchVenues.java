package cz.destil.wearsquare.api;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Processor for 4sq venues/search API.
 *
 * @author David Vávra (david@vavra.me)
 */
public interface SearchVenues {

    public static int LIMIT_VENUES = 10;

    @GET("venues/search?m=swarm&intent=checkin&limit=" + LIMIT_VENUES)
    Call<SearchResponse> searchForCheckIn(@Query("ll") String ll);

    public static class SearchResponse extends Api.FoursquareResponse {
        FoursquareContent response;

        public List<Venue> getVenues() {
            return response.venues;
        }
    }

    public static class Venue {
        public String id;
        public String name;
        List<FoursquareCategory> categories;

        public String getCategoryIconUrl() {
            if (categories.size() > 0) {
                FoursquareIcon icon = categories.get(0).icon;
                return icon.prefix + "44" + icon.suffix;
            }
            return null;
        }

        @Override
        public String toString() {
            return name + "|" + getCategoryIconUrl();
        }
    }

    // parsing classes:

    class FoursquareContent {
        List<Venue> venues;
    }

    class FoursquareCategory {
        FoursquareIcon icon;
    }

    class FoursquareIcon {
        String prefix;
        String suffix;
    }
}
