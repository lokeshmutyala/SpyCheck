package com.nearhere.spycheck;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.internal.observers.CallbackCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.query.Deletion;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveScalar;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SyncService extends Service {
    public SyncService() {
    }

    private ReactiveEntityStore<Persistable> data;
    boolean isGpsSyncComplete=false;
    private IBinder mBinder=new MyBinder();

    public class MyBinder extends Binder{
        SyncService getService(){
            return SyncService.this;
        }

    }

    @Override
    public void onCreate() {
        Log.i("syncService","on create");
        super.onCreate();
        data=((ProductApplication)getApplication()).getData();
        //syncGpsData();
    }

    @Override
    public void onDestroy() {
        Log.i("syncService","on destroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("syncService", "onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                syncGpsData();
            }
        }).start();
        //super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //return super.onUnbind(intent);
        return false;
    }

    void syncGpsData(){
        final List<GpsCoordinatedEntity> coordinatedEntities = data.select(GpsCoordinatedEntity.class).get().toList();
        Log.i("gps upload","list size="+coordinatedEntities.size());
        if(coordinatedEntities.size()>0) {
            JSONArray jsonArray = new JSONArray();
            int i = 0;
            for (GpsCoordinatedEntity coordinate : coordinatedEntities
                    ) {
                JSONObject object = new JSONObject();
                try {
                    object.put("latitude", coordinate.getLatitude());
                    object.put("longitude", coordinate.getLongitude());
                    object.put("accuracy", coordinate.getAccuracy());
                    object.put("time", coordinate.getcaptureTime());
                    object.put("agent_id", coordinate.getAgentId());
                    jsonArray.put(object);
                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("gps upload", "json error=" + e.toString());
                }
            }
            final int size = i;
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Updatedata.baseUrl).addConverterFactory(new ToStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create()).build();
            RetrofitProductApi api = retrofit.create(RetrofitProductApi.class);
            Call<String> syncGps = api.syncGpsCoordinates(jsonArray);
            syncGps.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    Log.i("gps response", response.body());
                    isGpsSyncComplete = true;
                    if (response.body().contains("success")) {
                        if (size < coordinatedEntities.size()) {
                            Log.i("size", "list size" + size);
                            //List<GpsCoordinatedEntity> tmplist= coordinatedEntities.subList(0,size-1);
                        }
                        for(GpsCoordinatedEntity enti:coordinatedEntities) {
                            data.delete(enti).subscribeOn(AndroidSchedulers.mainThread())
                                    .observeOn(Schedulers.io()).subscribe();
                        }
                        Log.i("gps upload","coordinates deleted");
                    }
                    stopSelf();
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.i("gps upload", "error in network" + t.toString());
                    isGpsSyncComplete = true;
                    stopSelf();
                }

            });
//        while (!isGpsSyncComplete){
//            Log.i("gps upload","in while loop");
//        }
        }else {
            stopSelf();
        }
    }
}
