package pl.coddev.applu.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pl.coddev.applu.R
import pl.coddev.applu.broadcastreceiver.PackageModifiedReceiver

class DataService : Service() {
    lateinit var context: Context
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand()")

        //startForeground();
        return START_STICKY
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        super.onCreate()

        context = applicationContext
        registerBroadcastReceiver()
        AsyncTask.execute {
//            val pm = applicationContext.packageManager
//            // one of the most consuming tasks
//            val packs: ArrayList<PackageInfo> = pm.getInstalledPackages(0) as ArrayList<PackageInfo>
//            //PInfoHandler.setAllPInfos();
//            val allInfos = ArrayList<PInfo>()
//            for (i in packs.indices) {
//                val pi = packs[i]
//                if (!PInfoHandler.fallsIntoSelector(pi, AppSelectorStatus.ALL, context)) continue
//                val newInfo = PInfo()
//                // most time consuming task!
//                newInfo.appname = pi.applicationInfo.loadLabel(pm).toString()
//                Prefs.putString(pi.packageName, newInfo.appname)
//                newInfo.isSystemPackage = PInfoHandler.isSystemPackage(pi)
//                newInfo.pname = pi.packageName
//                if (!allInfos.contains(newInfo)) allInfos.add(newInfo)
//            }
//            // add all to synchronized ArrayList, not one by one
//            PInfoHandler.setAllPInfos(allInfos)
//            Log.d(TAG, "onCreate number of packages from PM: " + packs.size)
        }
        //startForeground();
    }

    //    private void startForeground() {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    //            startMyOwnForeground();
    //        else
    //            startForeground(1, new Notification());
    //    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "pl.coddev.applu"
        val channelName = "My Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.small_icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(2, notification)
    }

    private fun registerBroadcastReceiver() {
        val packageModifiedReceiver = PackageModifiedReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addDataScheme("package")
        registerReceiver(packageModifiedReceiver, filter)
    }

    companion object {
        private const val TAG = "DataService"
    }
}