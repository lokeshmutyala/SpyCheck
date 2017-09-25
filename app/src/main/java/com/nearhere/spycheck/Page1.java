package com.nearhere.spycheck;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class Page1 extends AppCompatActivity {
    private StorageReference mStorageRef;
    File audfolder;
    File processFolder;
    File completeFolder;
    File imgFolder;
    File imgProcess;
    File imgComplete;
    boolean anError;
    boolean isImgError;
    boolean isSyncing=false;
    TextView records;
    Button start;
    Button sync;
    Button logout;
    TextView today_survey;
    private ReactiveEntityStore<Persistable> data;
    LinearLayout root;
    ProgressBar progress;
    Handler uiHandler;
    Handler mHandler;
    public static String auditid="";
    SharedPreferences preferences;
    public static String MY_PREFS="details";
    public static boolean isRaspiConnected=false;
    public static String macAddress="";
    public static String ipAddress="";
    static final int REQUEST_TAKE_PHOTO = 1;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    @Override
    protected void onPostResume() {
        start.setClickable(true);
        updateRecords();
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,GpsService.class));
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        askPermission();
        audfolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/audio");
        processFolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/audioProcess");
        completeFolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/audioComplete");
        imgFolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/images");
        imgProcess=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/imageProcess");
        imgComplete=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/imageComplete");
        File inrfolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/video");
        if(!processFolder.isDirectory() || !completeFolder.isDirectory() || !inrfolder.isDirectory() || !imgFolder.isDirectory() || !audfolder.isDirectory() || !imgProcess.isDirectory() || !imgComplete.isDirectory()){
            if(!processFolder.mkdirs() || !completeFolder.mkdirs() || ! inrfolder.mkdirs() || !imgFolder.mkdirs() || !audfolder.mkdirs() || !imgProcess.mkdirs() || !imgComplete.mkdirs()){
               // Toast.makeText(this,"error while creating directory",Toast.LENGTH_SHORT).show();
                Log.i("folder","creation error");
            }
        }
        if(isNetworkAvailable()){
            getMacAddresss();
        }
        mStorageRef = FirebaseStorage.getInstance().getReference();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("thread","in run audio");
                uploadAudio();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("thread","in run image");
                UploadImages();
            }
        }).start();
        if(!isCamConnected()){
            Toast.makeText(getApplicationContext(),"Camera not connected",Toast.LENGTH_LONG).show();
        }
        preferences=getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(preferences.getString("audit_id","").isEmpty()){
            Intent login=new Intent(getApplication(),LoginActivity.class);
            startActivity(login);
        }else{
            auditid=preferences.getString("audit_id","");
        }
        data=((ProductApplication)getApplication()).getData();
        setContentView(R.layout.activity_page1);
        start=(Button) findViewById(R.id.start);
        sync=(Button)findViewById(R.id.sync);
        records=(TextView)findViewById(R.id.records);
        today_survey=(TextView)findViewById(R.id.today_surveys);
        root=(LinearLayout)findViewById(R.id.root_layout);
        progress=(ProgressBar)findViewById(R.id.progress);
        updateRecords();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRaspiConnected) {
                    //replace below 3 lines if camera necessary
                }else {
                    Toast.makeText(getApplication(),"Camera not connected",Toast.LENGTH_SHORT).show();
                }
                start.setClickable(false);
                Intent intent = new Intent(getApplicationContext(), AuditPage.class);
                startActivity(intent);
            }
        });
        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=preferences.edit();
                editor.remove("audit_id");
                editor.commit();
                Intent login=new Intent(getApplication(),LoginActivity.class);
                startActivity(login);
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                root.setVisibility(View.GONE);
//                start.setVisibility(View.GONE);
//                sync.setVisibility(View.GONE);
//                progress.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sync.setClickable(false);
                        sync.setAlpha(0.5f);
                        startService(new Intent(getApplicationContext(),SyncService.class));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"sync started",Toast.LENGTH_SHORT).show();
                            }
                        });
                        final List<StoreDataEntity> entities = data.select(StoreDataEntity.class).where(StoreDataEntity.SYNCFLAG.eq(false)).get().toList();
                        if(entities.size()<=0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"No data to sync",Toast.LENGTH_SHORT).show();
                                }
                            });
                            sync.setClickable(true);
                            sync.setAlpha(1f);
                        }else {
                            JSONArray array=new JSONArray();
                            for (StoreDataEntity store:entities
                                    ) {
                                JSONObject object=new JSONObject();
                                try {
                                    object.put("store_id",store.getStoreId());
                                    object.put("time",store.getTime());
                                    object.put("latitude",store.getLatitude());
                                    object.put("longitude",store.getLongitude());
                                    object.put("accuracy",store.getAccuracy());
                                    object.put("store_name",store.getStoreName());
                                    object.put("store_type",store.getStoreType());
                                    object.put("price",store.getPrice());
                                    object.put("ra_itc",store.getRA_ITC());
                                    object.put("ra_vst",store.getRA_VST());
                                    object.put("ra_marlboro",store.getRA_Marlboro());
                                    object.put("shelf_itc",store.getShelf_ITC());
                                    object.put("shelf_vst",store.getShelf_VST());
                                    object.put("shelf_marlboro",store.getShelf_Marlboro());
                                    object.put("psu_itc",store.getPSU_ITC());
                                    object.put("psu_vst",store.getPSU_VST());
                                    object.put("psu_marlboro",store.getPSU_Marlboro());
                                    object.put("glow_itc",store.getGlow_ITC());
                                    object.put("glow_vst",store.getGlow_VST());
                                    object.put("glow_marlboro",store.getGlow_Marlboro());
                                    object.put("nonlit_itc",store.getNonlit_ITC());
                                    object.put("nonlit_vst",store.getNonlit_VST());
                                    object.put("nonlit_marlboro",store.getNonlit_Marlboro());
                                    object.put("video_duration",store.getVideoDuration());
                                    object.put("is_sell_cigarette",store.getIsSellCigarette());
                                    object.put("is_sell_bristole",store.getIsSellBristole());
                                    object.put("is_sell_charms",store.getIsSellCharms());
                                    object.put("price_charms",store.getPriceCharms());
                                    object.put("cam_duration",store.getCamDuration());
                                    object.put("dalla_itc",store.getDalla_ITC());
                                    object.put("dalla_vst",store.getDalla_VST());
                                    object.put("dalla_marlboro",store.getDalla_Marlboro());
                                    object.put("tv_itc",store.getTv_ITC());
                                    object.put("tv_vst",store.getTv_VST());
                                    object.put("tv_marlboro",store.getTv_Marlboro());
                                    object.put("landmark",store.getLandMark());
                                    object.put("store_cat",store.getStoreCat());

                                    array.put(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.i("error",e.toString());
                                }
                            }
                            Updatedata.update(array);
                            //mHandler=new Handler();
//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
                                    while(!Updatedata.isComplete && !Updatedata.isSuccess){
                                        //do nothing
                                        Log.i("check","in loop "+Updatedata.isComplete);
                                    }
                                    if(Updatedata.isSuccess){
                                        for (StoreDataEntity store:entities
                                                ) {
                                            store.setSyncflag(true);
                                            data.update(store);
                                        }
                                        data.update(entities).observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.io()).subscribe(new Consumer<Iterable<StoreDataEntity>>() {
                                            @Override
                                            public void accept(@NonNull Iterable<StoreDataEntity> storeDataEntities) throws Exception {
                                                //do nothing
                                                Log.i("synccheck","completed");
                                                records.setText(String.valueOf(data.count(StoreDataEntity.class).where(StoreDataEntity.SYNCFLAG.eq(false)).get().value()));
                                            }
                                        });
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"data sync completed",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        Log.i("check","error"+Updatedata.isComplete+Updatedata.isSuccess);
                                    }
                                    sync.setAlpha(1f);
                                    sync.setClickable(true);
                                //}
                            //},5000);

                        }
                    }
                }).start();
//                progress.setVisibility(View.GONE);
//                root.setVisibility(View.VISIBLE);
//                start.setVisibility(View.VISIBLE);
//                sync.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getMacAddresss() {
        Retrofit retrofir=new Retrofit.Builder().baseUrl(Updatedata.baseUrl)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi api=retrofir.create(RetrofitProductApi.class);
        Call<String[]> getaddress=api.getaddresses();
        getaddress.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Response<String[]> response, Retrofit retrofit) {

                if(response.body().length>0){
                    for (String mac:response.body()
                         ) {
                        if(data.count(MacaddressesEntity.MAC_ADDRESS).where(MacaddressesEntity.MAC_ADDRESS.eq(mac)).get().value()<=0) {
                            MacaddressesEntity macaddress = new MacaddressesEntity();
                            macaddress.setMacAddress(mac);
                            data.insert(macaddress).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<MacaddressesEntity>() {
                                        @Override
                                        public void accept(@NonNull MacaddressesEntity macaddressesEntity) throws Exception {
                                       Log.i("mac","inserted");
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("mac","error="+t.toString());
            }
        });
    }

    public boolean getClientList() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                Log.i("line","line="+line);
                String[] splitted = line.split(" +");
                if (splitted != null ) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if(data.count(MacaddressesEntity.class).where(MacaddressesEntity.MAC_ADDRESS.eq(mac)).get().value()>0){
                        macAddress=mac;
                        ipAddress=splitted[0];
                        return true;
                    }
                    //System.out.println("Mac : Outside If "+ mac );
//                    if (mac.matches("..:..:..:..:..:..")) {
//                        macCount++;
//                   /* ClientList.add("Client(" + macCount + ")");
//                    IpAddr.add(splitted[0]);
//                    HWAddr.add(splitted[3]);
//                    Device.add(splitted[5]);*/
//                        AsyncTask.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    InetAddress ip=InetAddress.getByName("192.168.43.77");
//                                InetAddress inetAddress= InetAddress.getByAddress(ip.getAddress());
//                                String name=inetAddress.getHostName();
//                                String lname=inetAddress.getLocalHost().getHostName();
//                                   String cname= inetAddress.getCanonicalHostName();
//                                    Log.i("name","name="+name);
//                                    Log.i("name","lname="+lname);
//                                    Log.i("name","cname="+cname);
//                                } catch (UnknownHostException e) {
//                                    e.printStackTrace();
//                                    Log.i("name","error="+e.toString());
//                                }
//                            }
//                        });
//                        Log.i("macip","Mac : "+ mac + " IP Address : "+splitted[0] );
//                        Log.i("count","Mac_Count  " + macCount + " MAC_ADDRESS  "+ mac);
//
//                    }
                }
            }
            br.close();
        } catch(Exception e) {
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean isCamConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService (Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo ();
            String ssid  = info.getSSID();
            Log.i("ssid",ssid);
            if(ssid.contains("Pi_AP")) {
              isRaspiConnected=true;
                return true;
            }
        }
        return false;
    }
    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d("gps", "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d("gps", "No rejected permissions.");
                    //canGetLocation = true;
                    //getLocation();
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
            Toast.makeText(getApplicationContext(),"allow mic to record",Toast.LENGTH_SHORT).show();
            //finish();
        }
        File folder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/audio");
        File inrfolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/video");
        File imgFolder=new File(Environment.getExternalStorageDirectory()+"/SpyCheck/images");
        if(!folder.isDirectory() || !inrfolder.isDirectory() || !imgFolder.isDirectory()){
            if(!folder.mkdirs() || ! inrfolder.mkdirs() || !imgFolder.mkdirs()){
                Toast.makeText(this,"error while creating directory",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConnected = wifi != null && wifi.isConnectedOrConnecting();
            if (isConnected) {
                //isCamConnected();
                WifiManager wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo ();
                String ssid  = info.getSSID();
                Log.i("ssid",ssid);
                if(ssid.contains("Pi_AP")) {
                    isRaspiConnected=true;
                    //return true;
                }

                Log.i("Network Available ", "YES");
            } else {
                Log.i("Network Available ", "NO");
            }
        }
    }
   public void askPermission(){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if (permissionsToRequest.size() > 0) {
               requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                       ALL_PERMISSIONS_RESULT);
               Log.d("gps", "Permission requests");
               //canGetLocation = false;
           }
       }
   }
   private void uploadAudio() {
       if(!isNetworkAvailable()){
           return;
       }
       if (audfolder.isDirectory() && processFolder.isDirectory() && completeFolder.isDirectory()) {
           File[] listFiles=processFolder.listFiles();//
           if(listFiles.length>0){
               for(int i=0;i<listFiles.length;i++){
                   if (listFiles[i].renameTo(new File(audfolder+"/" + listFiles[i].getName()))) {
                       Log.i("firebase", "success copying file");
                   } else {
                       Log.i("firebase", "file moving error");
                       return;
                   }
               }
           }
           listFiles= audfolder.listFiles();
           anError=false;
           for(int i=0;i<listFiles.length && !anError;i++) {
               if (listFiles[i].renameTo(new File(processFolder+"/" + listFiles[i].getName()))) {
                   Log.i("firebase", "success copying file");
               } else {
                   Log.i("firebase", "file moving error");
                   return;
               }
               File[] listProcessFiles = processFolder.listFiles();
               if (listProcessFiles.length > 0) {
                   final Uri file = Uri.fromFile(listProcessFiles[0]);    //(new File("path/to/images/rivers.jpg"));
                   StorageReference riversRef = mStorageRef.child("audio/"+listProcessFiles[0].getName());
                   riversRef.putFile(file)
                           .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   // Get a URL to the uploaded content
                                   //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                   Toast.makeText(getApplicationContext(), "Upload success", Toast.LENGTH_SHORT).show();
                                   File tmp=new File(file.getPath());
                                   if(tmp.renameTo(new File(completeFolder+"/"+tmp.getName()))){

                                   }else{
                                       tmp.delete();
                                   }
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {
                                   // Handle unsuccessful uploads
                                   // ...
                                   File tmp=new File(file.getPath());
                                   if(!tmp.renameTo(new File(audfolder+"/"+tmp.getName()))){
                                      Log.i("firebase","file upload error but stayed in process folder");
                                   }
                                   anError = true;
                                   Log.i("Firebase", "error=" + exception.toString());
                               }
                           });
               }
           }
       }else {
           if(!audfolder.mkdirs() || !processFolder.mkdirs() || !completeFolder.mkdirs()){
               Log.i("folder","creation error persists");
           }
       }
   }

   private void UploadImages(){
       if(!isNetworkAvailable()){
           return;
       }
       if(imgFolder.isDirectory() && imgProcess.isDirectory() && imgComplete.isDirectory()){
           File[] listimgs=imgProcess.listFiles();
           if(listimgs.length>0){
               for(int i=0;i<listimgs.length;i++){
                   if (listimgs[i].renameTo(new File(imgFolder+"/" + listimgs[i].getName()))) {
                       Log.i("firebase", "success copying file");
                   } else {
                       Log.i("firebase", "file moving error");
                       return;
                   }
               }
           }
           listimgs=imgFolder.listFiles();
           if(listimgs.length>0){
               isImgError=false;
               for(int i=0;i<listimgs.length && !isImgError;i++){
                   if (listimgs[i].renameTo(new File(imgProcess+"/" + listimgs[i].getName()))) {
                       Log.i("firebase", "success copying file");
                   } else {
                       Log.i("firebase", "file moving error");
                       return;
                   }
              File[]  listimgProcess=imgProcess.listFiles();
                   if(listimgProcess.length>0){
                       final Uri imgfile=Uri.fromFile(listimgProcess[0]);
                       StorageReference imgref=mStorageRef.child("images/"+listimgProcess[0].getName());
                       imgref.putFile(imgfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               Toast.makeText(getApplicationContext(), "Upload image success", Toast.LENGTH_SHORT).show();
                               File tmp=new File(imgfile.getPath());
                               if(tmp.renameTo(new File(imgComplete+"/"+tmp.getName()))){
                                   Log.i("firebase","error moving image");
                               }else{
                                   //tmp.delete();
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@android.support.annotation.NonNull Exception e) {
                               File tmp=new File(imgfile.getPath());
                               if(!tmp.renameTo(new File(imgFolder+"/"+tmp.getName()))){
                                   Log.i("firebase","file upload error but stayed in process folder");
                               }
                               isImgError = true;
                               Log.i("Firebase", "error=" + e.toString());
                           }
                       });
                   }
               }
           }
       }else {
           if(!imgFolder.mkdirs() || !imgProcess.mkdirs() || !imgComplete.mkdirs()){
               Log.i("folder","creation error persists");
           }
       }
   }
    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplication(),GpsService.class));
        super.onDestroy();
    }
    private void updateRecords(){
        Calendar tm=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("YYYY.MM.dd");
        String tmmp=sdf.format(tm.getTime());
        today_survey.setText(""+data.count(StoreDataEntity.class).where(StoreDataEntity.TIME.like(tmmp+"%")).get().value());
        records.setText(String.valueOf(data.count(StoreDataEntity.class).where(StoreDataEntity.SYNCFLAG.eq(false)).get().value()));

    }
}
