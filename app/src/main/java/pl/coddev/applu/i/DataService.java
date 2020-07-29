package pl.coddev.applu.i;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import pl.coddev.applu.R;
import pl.coddev.applu.b.PInfoHandler;
import pl.coddev.applu.b.PackageModifiedReceiver;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.enums.AppSelectorStatus;


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

        //startForeground();

        return Service.START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        context = getApplicationContext();
        registerBroadcastReceiver();

        AsyncTask.execute(() -> {
            PackageManager pm = getApplicationContext().getPackageManager();
            // one of the most consuming tasks
            ArrayList<PackageInfo> packs = (ArrayList) pm.getInstalledPackages(0);
            //PInfoHandler.setAllPInfos();
            ArrayList<PInfo> allInfos = new ArrayList<>();
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo pi = packs.get(i);

                if (!PInfoHandler.fallsIntoSelector(pi, AppSelectorStatus.ALL, context))
                    continue;

                PInfo newInfo = new PInfo();
                // most time consuming task!
                newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                newInfo.setIsSystemPackage(PInfoHandler.isSystemPackage(pi));
                newInfo.setPname(pi.packageName);
                if (!allInfos.contains(newInfo)) allInfos.add(newInfo);
            }
            // add all to synchronized ArrayList, not one by one
            PInfoHandler.setAllPInfos(allInfos);
            Log.d(TAG, "onCreate number of packages from PM: " + packs.size());
        });
        //startForeground();
    }

//    private void startForeground() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            startMyOwnForeground();
//        else
//            startForeground(1, new Notification());
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "pl.coddev.applu";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.small_icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
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
