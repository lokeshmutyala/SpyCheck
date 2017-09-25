package com.nearhere.spycheck;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lokeshmutyala on 15-09-2017.
 */

public class Updatedata {
    public static boolean isComplete=false;
    public static boolean isSuccess=false;
    public final static String baseUrl="http://54.213.26.115/";
    public static void update(JSONArray object){
        isSuccess=false;
        isComplete=false;
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi syncdata=retrofit.create(RetrofitProductApi.class);
        Call<String> upload=syncdata.syncdata(object);
        upload.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                Log.i("responce",response.body().toString());
                isComplete=true;
                if(response.body().contains("success")){
                    isSuccess=true;
                    Log.i("success check","success "+isComplete);
                }else {
                    isSuccess=false;
                    Log.i("sync error","here 11");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                isComplete=true;
                isSuccess=false;
                Log.i("error here",t.toString());
            }
        });
    }
}
