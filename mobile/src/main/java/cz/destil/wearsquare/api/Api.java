package cz.destil.wearsquare.api;

import android.text.TextUtils;

import cz.destil.wearsquare.BuildConfig;
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
        return new RestAdapter.Builder().setEndpoint(URL).setLogLevel(RestAdapter.LogLevel.FULL).setLog(new
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
                if (TextUtils.isEmpty("")) {
                    requestFacade.addQueryParam("client_id", Hidden.CLIENT_ID);
                    requestFacade.addQueryParam("client_secret", Hidden.CLIENT_SECRET);
                } else {
                    requestFacade.addQueryParam("oauth_token", "");
                }
            }
        }).build();
    }

    // parent classes common for all requests:

    public static class FoursquareResponse {
        FoursquareError meta;

        public boolean isMissingAuth() {
            return meta.isMissingAuth();
        }
    }

    public static class FoursquareError {
        int code;
        String errorType;

        public boolean isMissingAuth() {
            return "invalid_auth".equals(errorType) || "not_authorized".equals(errorType);
        }
    }

    public static class FoursquareNotification {
        String type;
        FoursquareNotificationDetail item;

        @Override
        public String toString() {
            return type + " : " + item;
        }
    }

    public static class FoursquareNotificationDetail {
        String message;
        String text;
        String shout;

        @Override
        public String toString() {
            return message + " " + text + " " + shout;
        }
    }
}
