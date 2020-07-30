package pl.coddev.applu;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;

import pl.coddev.applu.broadcastreceiver.PackageModifiedReceiver;
import pl.coddev.applu.service.DataService;
import pl.coddev.applu.utils.Prefs;

/**
 * Created by piotr woszczek on 30.12.14.
 */
public class AppluApplication extends Application {

    public static String TAG = "AppluApplication";

    private static AppluApplication instance;
    private int featureCount = 0;

    public static AppluApplication get() {
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
        Log.i(TAG, "OnCreate invoked");
        instance = this;
        featureCount = Prefs.get().getFeatureCount();

        Intent intent = new Intent(this, DataService.class);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    startForegroundService(intent);
        //} else {
        startService(intent);
        //}

        //registerBroadcastReceiver();

    } // onCreate - end

    public int featureCount() {
        return instance.featureCount;
    }

    public int incrementFeatureCount() {
        return ++instance.featureCount;
    }

    private void registerBroadcastReceiver() {
        PackageModifiedReceiver packageModifiedReceiver = new PackageModifiedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        registerReceiver(packageModifiedReceiver, filter);
    }
}
