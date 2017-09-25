package com.nearhere.spycheck;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import io.requery.Persistable;
import io.requery.android.BuildConfig;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

/**
 * Created by lokeshmutyala on 29-06-2017.
 */

public class ProductApplication extends Application {
    private ReactiveEntityStore<Persistable> dataStore;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       // MultiDex.install(this);
    }

    // private static String DB_NAME = "default";//the extension may be .sqlite or .db
    //private String DB_PATH;
    @Override
    public void onCreate() {
        super.onCreate();
      /*  DB_PATH = "/data/data/"
                + getApplicationContext().getPackageName()
                + "/databases/";    */
        StrictMode.enableDefaults();
    }
   public ReactiveEntityStore<Persistable> getData(){
/*
       boolean dbexist = checkdatabase();
       if (!dbexist) {
           copydatabase();
       }
  */      if (dataStore == null) {
            // override onUpgrade to handle migrating to a new version
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT,2);//DB_PATH+DB_NAME, 3);
            if (BuildConfig.DEBUG) {
                // use this in development mode to drop and recreate the tables on every upgrade
                source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
            }
            Configuration configuration = source.getConfiguration();
            dataStore = ReactiveSupport.toReactiveStore(
                    new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;

    }

/*
    private boolean checkdatabase() {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            File dbfileDir = new File(DB_PATH);

            if(!dbfileDir.exists())
                dbfileDir.mkdir();

            //checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() {

        try{
            InputStream myinput = getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outfilename = DB_PATH + DB_NAME;

            //Open the empty db as the output stream
            OutputStream myoutput = new FileOutputStream(DB_PATH+DB_NAME);

            // transfer byte to inputfile to outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myinput.read(buffer))>0) {
                myoutput.write(buffer,0,length);
            }
            myoutput.flush();
            myoutput.close();
            myinput.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        //Open your local db as the input stream


        //Close the streams

    }*/
}
