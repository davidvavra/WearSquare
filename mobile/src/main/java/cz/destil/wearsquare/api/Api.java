package cz.destil.wearsquare.api;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cz.destil.wearsquare.data.Preferences;
import cz.destil.wearsquare.util.L;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Processor for 4sq API.
 *
 * @author David Vávra (david@vavra.me)
 */
public class Api {

    public static final String BUILD_DATE = "20141226";

    public static final String URL = "https://api.foursquare.com/v2/";

    public static Retrofit get() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                HttpUrl newUrl = originalRequest.httpUrl().newBuilder()
                        .addQueryParameter("v", BUILD_DATE)
                        .addQueryParameter("oauth_token", Preferences.getFoursquareToken())
                        .build();
                Request newRequest = originalRequest.newBuilder().url(newUrl).build();
                L.i("Calling " + newUrl);
                Response response = chain.proceed(newRequest);
                L.i("Response code: " + response.code());
                return response;
            }
        });
        return new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // parent classes common for all requests:

    public static class FoursquareResponse {
    }
}
