package com.nearhere.spycheck;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.aware.WifiAwareManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class AuditPage extends AppCompatActivity implements LocationListener{
    private ReactiveEntityStore<Persistable> data;
    private MediaRecorder mRecorder = null;
    private static String mAudioFileName = null;
    private boolean isRecording=false;
    String camDuration="";
    LinearLayout mainView;
    boolean isrecorded=false;
    int prices=0;
    EditText landmark;
    RadioGroup tmp_perm;
    Retrofit retrofitraspi;
//    LinearLayout total;
//    ProgressBar progressBar;
//    private boolean isRaspiconnected=false;
    TextView lat;
    TextView lng;
    int price_charms=0;
    RadioGroup charmsprice;
    TextView accuracy;
    boolean isSellCigar=false;
    boolean isSellBristole=false;
    boolean isSellCharms=false;
    LinearLayout root_cigar;
    LinearLayout root_bristole;
    LinearLayout root_charms;
    RadioGroup sellCigar;
    RadioGroup sellBristole;
    RadioGroup hastv;
    RadioGroup sellCharms;
    EditText store_name;
    RadioGroup store_type;
    Button start_video;
    Button take_pic;
    Chronometer timer;
    RadioGroup price;
    CheckBox ra_itc;
    CheckBox ra_vst;
    CheckBox ra_marlboro;
    CheckBox dalla_itc;
    CheckBox dalla_vst;
    CheckBox dalla_marlboro;
    CheckBox tv_itc;
    CheckBox tv_vst;
    CheckBox tv_marlboro;
    CheckBox shelf_itc;
    CheckBox shelf_vst;
    CheckBox shelf_marlboro;
    CheckBox psu_itc;
    CheckBox psu_vst;
    CheckBox psu_marlboro;
    CheckBox glow_itc;
    CheckBox glow_vst;
    CheckBox glow_marlboro;
    CheckBox nonlit_itc;
    CheckBox nonlit_vst;
    CheckBox nonlit_marlboro;
    Button submit;
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    LocationManager locationManager;
    Location loc;
    String acc_text="C";
    float acc_no=0;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    String storeid="";
    static final int REQUEST_TAKE_PHOTO = 1;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000;//1000 * 60 * 1;
    private static final String baseurl="http://192.168.42.1/";
    ImageView mImageView;
    String mCurrentPhotoPath=Environment.getExternalStorageDirectory()+"/SpyCheck/images";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
////            Bundle extras = data.getExtras();
////            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            File imgFile=new File(mCurrentPhotoPath+"/"+storeid+".jpg");
//            if(imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                mImageView.setImageBitmap(myBitmap);
//            }
//        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeid=Page1.auditid+System.currentTimeMillis();
        mAudioFileName= Environment.getExternalStorageDirectory().getAbsolutePath()+"/SpyCheck/audio/"+storeid+".3gp";
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        data=((ProductApplication)getApplication()).getData();
        setContentView(R.layout.activity_audit_page);
        mImageView=(ImageView)findViewById(R.id.image);
//        total=(LinearLayout)findViewById(R.id.total);
//        progressBar=(ProgressBar)findViewById(R.id.progress);
        lat=(TextView)findViewById(R.id.lat);
        lng=(TextView)findViewById(R.id.lng);
        accuracy=(TextView)findViewById(R.id.accuracy);
        //updatelatlng();
        landmark=(EditText)findViewById(R.id.landmark);
        tmp_perm=(RadioGroup)findViewById(R.id.temp_perm);
        start_video=(Button)findViewById(R.id.start_video);
        start_video.setAlpha(0.5f);
        start_video.setClickable(false);
        take_pic =(Button)findViewById(R.id.end_video);
        timer=(Chronometer)findViewById(R.id.timer);
        store_name=(EditText)findViewById(R.id.store_name);
        store_type=(RadioGroup)findViewById(R.id.store_type);
        price=(RadioGroup) findViewById(R.id.price);
        dalla_itc=(CheckBox) findViewById(R.id.dalla_itc);
        dalla_vst=(CheckBox) findViewById(R.id.dalla_vst);
        dalla_marlboro=(CheckBox) findViewById(R.id.dalla_marlboro);
        tv_itc=(CheckBox) findViewById(R.id.tv_itc);
        tv_vst=(CheckBox) findViewById(R.id.tv_vst);
        tv_marlboro=(CheckBox) findViewById(R.id.tv_marlboro);
        mainView=(LinearLayout) findViewById(R.id.main_screen);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(AuditPage.this);
            }
        });
        ra_itc=(CheckBox)findViewById(R.id.ra_itc);
        ra_vst=(CheckBox)findViewById(R.id.ra_vst);
        ra_marlboro=(CheckBox)findViewById(R.id.ra_marlboro);
        shelf_itc=(CheckBox)findViewById(R.id.shelf_itc);
        shelf_vst=(CheckBox)findViewById(R.id.shelf_vst);
        shelf_marlboro=(CheckBox)findViewById(R.id.shelf_marlboro);
        psu_itc=(CheckBox)findViewById(R.id.psu_itc);
        psu_vst=(CheckBox)findViewById(R.id.psu_vst);
        psu_marlboro=(CheckBox)findViewById(R.id.psu_marlboro);
        glow_itc=(CheckBox)findViewById(R.id.glow_itc);
        glow_vst=(CheckBox)findViewById(R.id.glow_vst);
        glow_marlboro=(CheckBox)findViewById(R.id.glow_marlboro);
        nonlit_itc=(CheckBox)findViewById(R.id.nonlit_itc);
        nonlit_vst=(CheckBox)findViewById(R.id.nonlit_vst);
        nonlit_marlboro=(CheckBox)findViewById(R.id.nonlit_marlboro);
        sellCigar=(RadioGroup) findViewById(R.id.sell_cigar);
        sellBristole=(RadioGroup) findViewById(R.id.sell_bristole);
        sellCharms=(RadioGroup)findViewById(R.id.sell_charms);
        root_cigar=(LinearLayout)findViewById(R.id.root_cigar);
        root_bristole=(LinearLayout) findViewById(R.id.root_bristole);
        root_charms=(LinearLayout)findViewById(R.id.root_charms);
        charmsprice=(RadioGroup)findViewById(R.id.price_charms);
        charmsprice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.five_charms){
                    price_charms=5;
                }else if(i==R.id.six_charms){
                    price_charms=6;
                }else if(i==R.id.seven_charms){
                    price_charms=7;
                }else {
                    price_charms=8;
                }
            }
        });
        sellCigar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes && !isrecorded){
                    isSellCigar=true;
                    start_video.setClickable(true);
                    start_video.setAlpha(1f);
                    root_cigar.setVisibility(View.VISIBLE);
                }else {
                    isSellCigar=false;
                    start_video.setAlpha(0.5f);
                    start_video.setClickable(false);
                    root_cigar.setVisibility(View.GONE);
                }
            }
        });
        sellBristole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes_bristole){
                    isSellBristole=true;
                    root_bristole.setVisibility(View.VISIBLE);
                }else {
                    isSellBristole=false;
                    root_bristole.setVisibility(View.GONE);
                }
            }
        });
        sellCharms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.yes_charms){
                    isSellCharms=true;
                    root_charms.setVisibility(View.VISIBLE);
                }else {
                    isSellCharms=false;
                    price_charms=0;
                    root_charms.setVisibility(View.GONE);
                }
            }
        });
        submit=(Button)findViewById(R.id.submit);
        if (!isGPS && !isNetwork) {
            Log.i("gps", "Connection off");
            showSettingsAlert();
            //getLastLocation();
        } else {
            Log.d("gps", "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d("gps", "Permission requests");
                    canGetLocation = false;
                }
            }
            // get location
            getLocation();
        }
        start_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSellCigar || isrecorded){
                    return;
                }
                if(isRecording){
                    Log.i("audio","stopping recording");
                    stopRecording();
                    isrecorded=true;
                    timer.stop();
                    Log.i("audio","recording stopped");
                    start_video.setClickable(false);
                    start_video.setAlpha(0.5f);
                    take_pic.setClickable(true);
                    take_pic.setAlpha(1);
                }else {
                    Log.i("audio","starting recording");
                    startRecording();
                    Log.i("audio","recording started");
                    start_video.setText("Stop Video");
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                }
                //Log.i("timer",""+timer.getBase());

                isRecording=!isRecording;
            }
        });
        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //take picture
                Log.i("image","button clicked");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    Log.i("image","inside if");
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("image","creation error="+ex.toString());
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = //FileProvider.getUriForFile(getApplication(), getApplicationContext().getPackageName() , photoFile);
                        //Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/SpyCheck/images/"+storeid+".jpg"));
                        Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                    }
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isRecording){
                    stopRecording();
                    isRecording=false;
                }
//                if(!canGetLocation){
//                    Toast.makeText(getApplicationContext(),"Location Not available",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                String latitude=lat.getText().toString();
                String longitude=lng.getText().toString();
                String acc=accuracy.getText().toString();

                if(acc_no>25 || acc_no==0 || latitude.isEmpty() || longitude.isEmpty()){
                    Toast.makeText(getApplication(),"accuracy should be less than 25",Toast.LENGTH_LONG).show();
                }else {
                   String name=store_name.getText().toString();
                    String type="";
                    String landMark=landmark.getText().toString();
                    String store_prm_temp="";
                    if(tmp_perm.getCheckedRadioButtonId()==R.id.temporary){
                        store_prm_temp="Temporary";
                    }else {
                        store_prm_temp="Permanent";
                    }
                    if(store_type.getCheckedRadioButtonId()==R.id.cigarette_tea){
                        type="Cigarette_Tea_Stall";
                    }else if(store_type.getCheckedRadioButtonId()==R.id.cigarette_pan){
                        type="Cigarette_Pan";
                    }else if(store_type.getCheckedRadioButtonId()==R.id.kirana){
                        type="Kirana";
                    }else if(store_type.getCheckedRadioButtonId()==R.id.backery){
                        type="Backery";
                    }else if(store_type.getCheckedRadioButtonId()==R.id.chemist){
                        type="Chemist Store";
                    }else if(store_type.getCheckedRadioButtonId()==R.id.cosmetic){
                        type="Cosmetic Store";
                    }else if(store_type.getCheckedRadioButtonId()==R.id.supermarket){
                        type="Supermarket";
                    }else {
                        type="Wholesale";
                    }
                    prices=0;
                    if(isSellBristole && isSellCigar) {
                        switch (price.getCheckedRadioButtonId()){
                            case R.id.five:
                                prices=5;
                                break;
                            case R.id.six:
                                prices=6;
                                break;
                            case R.id.seven:
                                prices=7;
                                break;
                            case R.id.eight:
                                prices=8;
                                break;
                        }
                    }
                    Log.i("Lokesh","prices="+prices+isSellCigar+isSellBristole);
                    if(name.isEmpty() || type.isEmpty() || landMark.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Fill required fields",Toast.LENGTH_SHORT).show();
                    }else {
                        boolean[] ra = {false, false, false};
                        boolean[] dalla={false,false,false};
                        boolean[] shelf = {false, false, false};
                        boolean[] psu = {false, false, false};
                        boolean[] glow = {false, false, false};
                        boolean[] nonlit = {false, false, false};
                        boolean[] tv={false,false,false};
                        if (isSellCigar) {
                            if (ra_itc.isChecked()) {
                                ra[0] = true;
                            }
                            if (ra_vst.isChecked()) {
                                ra[1] = true;
                            }
                            if (ra_marlboro.isChecked()) {
                                ra[2] = true;
                            }
                            if(dalla_itc.isChecked()){
                                dalla[0]=true;
                            }
                            if(dalla_vst.isChecked()){
                                dalla[1]=true;
                            }
                            if(dalla_marlboro.isChecked()){
                                dalla[2]=true;
                            }
                            if(tv_itc.isChecked()){
                                tv[0]=true;
                            }
                            if(tv_vst.isChecked()){
                                tv[1]=true;
                            }
                            if(tv_marlboro.isChecked()){
                                tv[2]=true;
                            }
                            if (shelf_itc.isChecked()) {
                                shelf[0] = true;
                            }
                            if (shelf_vst.isChecked()) {
                                shelf[1] = true;
                            }
                            if (shelf_marlboro.isChecked()) {
                                shelf[2] = true;
                            }
                            if (psu_itc.isChecked()) {
                                psu[0] = true;
                            }
                            if (psu_vst.isChecked()) {
                                psu[1] = true;
                            }
                            if (psu_marlboro.isChecked()) {
                                psu[2] = true;
                            }
                            if (glow_itc.isChecked()) {
                                glow[0] = true;
                            }
                            if (glow_vst.isChecked()) {
                                glow[1] = true;
                            }
                            if (glow_marlboro.isChecked()) {
                                glow[2] = true;
                            }
                            if (nonlit_itc.isChecked()) {
                                nonlit[0] = true;
                            }
                            if (nonlit_vst.isChecked()) {
                                nonlit[1] = true;
                            }
                            if (nonlit_marlboro.isChecked()) {
                                nonlit[2] = true;
                            }
                        }
                        StoreDataEntity storeDataEntity = new StoreDataEntity();
                        storeDataEntity.setLatitude(Double.parseDouble(latitude));
                        storeDataEntity.setLongitude(Double.parseDouble(longitude));
                        storeDataEntity.setAccuracy(acc_no);
                        storeDataEntity.setStoreName(name);
                        storeDataEntity.setStoreType(type);
                        storeDataEntity.setPrice(prices);
                        storeDataEntity.setSyncflag(false);
                        storeDataEntity.setTime("" + new SimpleDateFormat("yyy.MM.dd.HH.mm.ss").format(new Date()));
                        storeDataEntity.setStoreId(storeid);
                        storeDataEntity.setRA_ITC(ra[0]);
                        storeDataEntity.setRA_VST(ra[1]);
                        storeDataEntity.setRA_Marlboro(ra[2]);
                        storeDataEntity.setShelf_ITC(shelf[0]);
                        storeDataEntity.setShelf_VST(shelf[1]);
                        storeDataEntity.setShelf_Marlboro(shelf[2]);
                        storeDataEntity.setPSU_ITC(psu[0]);
                        storeDataEntity.setPSU_VST(psu[1]);
                        storeDataEntity.setPSU_Marlboro(psu[2]);
                        storeDataEntity.setGlow_ITC(glow[0]);
                        storeDataEntity.setGlow_VST(glow[1]);
                        storeDataEntity.setGlow_Marlboro(glow[2]);
                        storeDataEntity.setNonlit_ITC(nonlit[0]);
                        storeDataEntity.setNonlit_VST(nonlit[1]);
                        storeDataEntity.setNonlit_Marlboro(nonlit[2]);
                        long duration = SystemClock.elapsedRealtime() - timer.getBase();
                        storeDataEntity.setVideoDuration("" + duration);
                        storeDataEntity.setIsSellCigarette(isSellCigar);
                        storeDataEntity.setIsSellBristole(isSellBristole);
                        storeDataEntity.setCamDuration(camDuration);
                        storeDataEntity.setIsSellCharms(isSellCharms);
                        storeDataEntity.setPriceCharms(price_charms);
                        storeDataEntity.setDalla_ITC(dalla[0]);
                        storeDataEntity.setDalla_VST(dalla[1]);
                        storeDataEntity.setDalla_Marlboro(dalla[2]);
                        storeDataEntity.setTv_ITC(tv[0]);
                        storeDataEntity.setTv_VST(tv[1]);
                        storeDataEntity.setTv_Marlboro(tv[2]);
                        storeDataEntity.setLandMark(landMark);
                        storeDataEntity.setStoreCat(store_prm_temp);
                        data.insert(storeDataEntity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new io.reactivex.functions.Consumer<StoreDataEntity>() {
                            @Override
                            public void accept(@NonNull StoreDataEntity storeDataEntity) throws Exception {
                                Toast.makeText(getApplicationContext(), "details saved successfully", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }
    private void updatelatlng(){

    }

    @Override
    public void onLocationChanged(Location location) {

        //lat.setText(String.valueOf(location.getLatitude()));
        //lng.setText(String.valueOf(location.getLongitude()));
        //accuracy.setText(String.valueOf(location.getAccuracy()));
        Log.i("location","location="+location);
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
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
                    canGetLocation = true;
                    getLocation();
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
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isRecording){
            stopRecording();
        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.i("gps", "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.i("gps", "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.i("gps", "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.i("gps", "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void updateUI(Location loc) {
        Log.d("gps", "updateUI");
        lat.setText(Double.toString(loc.getLatitude()));
        lng.setText(Double.toString(loc.getLongitude()));
        if(acc_no>loc.getAccuracy() || acc_no==0){
        acc_no=loc.getAccuracy();
        if(acc_no<10) {
            acc_text="A";
            accuracy.setText(" A ");
        }else if(acc_no>25){
            accuracy.setText(" C ");
            acc_text="C";
        }else {
            acc_text="B";
            accuracy.setText(" B ");
        }
    }
    }
    private void startRecording() {
        OkHttpClient okHttpClient=new OkHttpClient();
        okHttpClient.setReadTimeout(250, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(250,TimeUnit.SECONDS);
        retrofitraspi=new Retrofit.Builder().baseUrl(baseurl)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        RetrofitProductApi api=retrofitraspi.create(RetrofitProductApi.class);
        Call<String> record=api.startcam(storeid);
        //Log.i("video","check id"+name.toString());
        record.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.body().contains("recording")) {
                    camDuration = response.body().substring(response.body().indexOf("time") + 5);
                    Log.i("video", "time=" + camDuration);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("video","error="+t.toString());

            }
        });

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mAudioFileName);
            Log.i("audio", "file=" + mAudioFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.i("audio", "prepare() failed"+e.toString());
            }

            mRecorder.start();
    }
    private void stopRecording() {
        retrofitraspi=new Retrofit.Builder().baseUrl(baseurl)
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitProductApi api=retrofitraspi.create(RetrofitProductApi.class);
        Call<String> stoprecording=api.stopcam();
        stoprecording.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                Log.i("video","success="+response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("video","error="+t.toString());

            }
        });

            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }catch (Exception e){
                Log.i("audio","check stop error="+e.toString());
            }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = storeid;//"JPEG_" + timeStamp + "_";
        File storageDir =new File(Environment.getExternalStorageDirectory()+"/SpyCheck/images");// getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
