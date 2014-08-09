package cz.destil.wearsquare.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import cz.destil.wearsquare.core.App;

/**
 * Location-related utils.
 *
 * @author David Vávra (david@vavra.me)
 */
public class LocationUtils {

    public static String getLastLocation() throws LocationNotFoundException {
        // return "51.497470, -0.135633";//"40.765068,-73.983172"; // FAKE FOR screenshots
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location == null) {
            throw new LocationNotFoundException();
        }
        return location.getLatitude() + "," + location.getLongitude();
    }



    public static int getLastAccuracy() {
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        return (int) location.getAccuracy();
    }

    public static int getLastAltitude() {
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        return (int) location.getAltitude();
    }

    public static class LocationNotFoundException extends Exception {
    }
}
