package pl.coddev.applu.i;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import pl.coddev.applu.b.PInfoHandler;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.p.UninstallWidget;


public class DataService extends Service {
    private static final String TAG = "DataService";

    SharedPreferences prefs;
    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this, "channel1")
                    .build());
        }
        return Service.START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        context = getApplicationContext();
        AsyncTask.execute(() -> {
            PackageManager pm = getApplicationContext().getPackageManager();
            // one of the most consuming tasks
            ArrayList<PackageInfo> packs = (ArrayList) pm.getInstalledPackages(0);
            //PInfoHandler.setAllPInfos();
            ArrayList<PInfo> allInfos = new ArrayList<>();
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo pi = packs.get(i);

                if (!PInfoHandler.fallsIntoSelector(pi, UninstallWidget.AppSelectorStatus.ALL, context))
                    continue;

                PInfo newInfo = new PInfo();
                // most time consuming task!
                newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                newInfo.setIsSystemPackage(PInfoHandler.isSystemPackage(pi));
                newInfo.setPname(pi.packageName);
                if(!allInfos.contains(newInfo))allInfos.add(newInfo);
            }
            // add all to synchronized ArrayList, not one by one
            PInfoHandler.setAllPInfos(allInfos);
            Log.d(TAG, "onCreate " + packs.size());
        });
        int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this, "channel1")
                    .build());
        }
    }
}
