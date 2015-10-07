package cz.destil.wearsquare.api;

import retrofit.Call;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Processor for 4sq checkings/add API.
 *
 * @author David Vávra (david@vavra.me)
 */
public interface CheckIns {

    @POST("checkins/add?m=swarm")
    Call<CheckInResponse> add(@Query("venueId") String venueId, @Query("ll") String ll, @Query("llAcc") int accuracy, @Query("alt") int altitude,
                              @Query("broadcast") String broadcast,
                              @Query("shout") String shout);

    class CheckInResponse extends Api.FoursquareResponse {
    }
}