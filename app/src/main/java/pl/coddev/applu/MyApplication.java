package pl.coddev.applu;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import pl.coddev.applu.i.DataService;

/**
 * Created by ted on 30.12.14.
 */
public class MyApplication extends Application {

    public static String TAG = "MyApplication";

    private static MyApplication instance;

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

        //Fabric.with(this, new Crashlytics());
        Log.i(TAG, "OnCreate invoked");

        // DataService
//        mConnection = new ServiceConnection() {
//            public void onServiceConnected(ComponentName className, IBinder binder) {
//                Log.i(TAG, "onServiceConnected()");
//                mDataService = ((DataService.MyBinder) binder).getService();
////                getInvitationsList();
//            }
//
//            public void onServiceDisconnected(ComponentName className) {
//                Log.i(TAG, "onServiceDisconnected()");
//                mDataService = null;
//            }
//        };
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    } // onCreate - end

}
