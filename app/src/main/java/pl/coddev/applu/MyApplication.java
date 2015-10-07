package pl.coddev.applu;

import android.app.Application;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

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
        featureCount = Utils.incrementUsage();

        //Fabric.with(this, new Crashlytics());
        Log.i(TAG, "OnCreate invoked");

        Intent intent = new Intent(this, DataService.class);
        startService(intent);

    } // onCreate - end

    public int featureCount(){
        return instance.featureCount;
    }

}
