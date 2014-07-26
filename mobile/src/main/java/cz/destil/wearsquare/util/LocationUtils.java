package cz.destil.wearsquare.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import cz.destil.wearsquare.core.App;

public class LocationUtils {

    public static String getLastLocation() {
        // return "51.497470, -0.135633";//"40.765068,-73.983172"; // FAKE FOR screenshots
        LocationManager locationManager = (LocationManager) App.get().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
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
}
