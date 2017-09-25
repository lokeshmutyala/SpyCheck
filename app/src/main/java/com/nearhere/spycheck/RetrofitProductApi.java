package com.nearhere.spycheck;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by lokeshmutyala on 15-09-2017.
 */

public interface RetrofitProductApi {
    @POST("syncspystoredata.php")
    Call<String> syncdata(@Body JSONArray jsonArray);

    @POST("spyauditlogin.php")
    Call<String> login(@Body JSONObject object);

    @POST("getraspimacaddresses.php")
    Call<String[]> getaddresses();

    @GET("start.php")
    Call<String> startcam(@Query("name") String name);

    @POST("stop.php")
    Call<String> stopcam();

    @POST("isconnect.php")
    Call<String> connect();

    @POST("syncspycoordinates.php")
    Call<String> syncGpsCoordinates(@Body JSONArray carray);
}
