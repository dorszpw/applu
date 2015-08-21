package pl.coddev.applu.i;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import pl.coddev.applu.b.PInfoHandler;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.p.UninstallWidget;


public class DataService extends Service {
    private static final String TAG = "DataService";


    // DataService
    private final IBinder mBinder = new MyBinder();
    SharedPreferences prefs;
    Context context;


    //==============================================================================================
    // ===== DataService
    //==============================================================================================
    public class MyBinder extends Binder {
        public DataService getService() {
            Log.i(TAG, "getService()");
            return DataService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        context = getApplicationContext();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = getApplicationContext().getPackageManager();
                // one of the most consuming tasks
                ArrayList<PackageInfo> packs = (ArrayList) pm.getInstalledPackages(0);
                PInfoHandler.setAllPInfos();

                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo pi = packs.get(i);

                    if (!PInfoHandler.fallsIntoSelector(pi, UninstallWidget.AppSelectorStatus.ALL, context))
                        continue;

                    PInfo newInfo = new PInfo();
                    // most time consuming task!
                    newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                    //newInfo.setAppname("applu");
                    newInfo.setIsSystemPackage(PInfoHandler.isSystemPackage(pi));
                    newInfo.setPname(pi.packageName);
                    PInfoHandler.addToAll(newInfo);
                }
                Log.d(TAG, "onCreate " + packs.size());
            }
        });
    }
}
