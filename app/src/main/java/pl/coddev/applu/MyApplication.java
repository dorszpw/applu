package pl.coddev.applu;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import pl.coddev.applu.c.Constants;
import pl.coddev.applu.c.Utils;
import pl.coddev.applu.i.DataService;

/**
 * Created by ted on 30.12.14.
 */
public class MyApplication extends Application {

    public static String TAG = "MyApplication";

    private static MyApplication instance;
    private int featureCount = 0;

    public static MyApplication get() {
        return instance;
    }

    // DataService
    public DataService mDataService;
    private ServiceConnection mConnection;

    // =============================================================================================
    // ===== onCreate
    // =============================================================================================
    public void onCreate() {
        super.onCreate();
        instance = this;
        SharedPreferences preferences = getSharedPreferences(
                Constants.PREFS_FILE, Context.MODE_PRIVATE);
        featureCount = preferences.getInt(Constants.EXTRA_FEATURE_USAGE, 0);

        //Fabric.with(this, new Crashlytics());
        Log.i(TAG, "OnCreate invoked");

        Intent intent = new Intent(this, DataService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

    } // onCreate - end

    public int featureCount(){
        return instance.featureCount;
    }

    public int incrementFeatureCount(){
        return ++instance.featureCount;
    }

}
