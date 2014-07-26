package cz.destil.wearsquare.api;

import cz.destil.wearsquare.BuildConfig;
import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.DebugLog;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Processor for 4sq API.
 *
 * @author David 'Destil' Vavra (david@vavra.me)
 */
public class Api {

    public static final String BUILD_DATE = "20140130";

    public static final String URL = "https://api.foursquare.com/v2";

    public static RestAdapter get() {
        return new RestAdapter.Builder().setEndpoint(URL).setLogLevel(RestAdapter.LogLevel.BASIC).setLog(new
                                                                                                                 RestAdapter.Log() {
                                                                                                                     @Override
                                                                                                                     public void log(String s) {
                                                                                                                         if (BuildConfig.DEBUG) {
                                                                                                                             DebugLog.i(s);
                                                                                                                         }
                                                                                                                     }
                                                                                                                 })
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addQueryParam("v", BUILD_DATE);
                        requestFacade.addQueryParam("oauth_token", Preferences.getFoursquareToken());
                    }
                }).build();
    }

    // parent classes common for all requests:

    public static class FoursquareResponse {
    }
}
