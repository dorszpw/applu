package pl.coddev.applu.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pl.coddev.applu.App
import pl.coddev.applu.R
import pl.coddev.applu.broadcastreceiver.PackageModifiedReceiver
import pl.coddev.applu.data.PInfo
import pl.coddev.applu.enums.WidgetActions
import pl.coddev.applu.utils.Prefs
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class DataService : Service() {
    lateinit var context: Context
    private val packageModifiedReceiver = PackageModifiedReceiver()
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
            getAllInstalledApps()
        }
        //startForeground();
    }

    //    private void startForeground() {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    //            startMyOwnForeground();
    //        else
    //            startForeground(1, new Notification());
    //    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unregisterReceiver(packageModifiedReceiver)
    }

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
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addDataScheme("package")
        registerReceiver(packageModifiedReceiver, filter)
    }

    companion object {
        private const val TAG = "DataService"

        fun getAllInstalledApps() {
            val pm = App.get().packageManager
            // one of the most consuming tasks
            val packs: ArrayList<PackageInfo> = pm.getInstalledPackages(0) as ArrayList<PackageInfo>
            //PInfoHandler.setAllPInfos();
            val allInfos = ArrayList<PInfo>()
            for (i in packs.indices) {
                val pi = packs[i]
                //if (PInfoHandler.isSystemPackage(pi)) continue
                val newInfo = PInfo()
                newInfo.appname = Prefs.getString(pi.packageName, "")
                if (newInfo.appname == "") {
                    // most time consuming task!
                    newInfo.appname = pi.applicationInfo.loadLabel(pm).toString()
                    Prefs.putString(pi.packageName, newInfo.appname)
                }
                newInfo.isSystemPackage = PInfoHandler.isSystemPackage(pi)
                newInfo.pname = pi.packageName
                if (!allInfos.contains(newInfo)) allInfos.add(newInfo)
            }
            // add all to synchronized ArrayList, not one by one
            PInfoHandler.setAllPInfos(allInfos)
            Log.d(TAG, "onCreate number of packages from PM: " + packs.size)
        }

        fun getAppsByFilter(widgetId: Int, button: WidgetActions?) {
            val start = Calendar.getInstance().timeInMillis
            Log.d(TAG, "getInstalledApps xstart $start")
            val ptn: Pattern
            var matcher: Matcher
            var filter = Prefs.getFilterList(widgetId)
            Log.d(TAG, "getInstalledApps, last filter: $filter")
            val commonChars = "[^a-zA-Z]*"
            var filterExpansion = ""
            when (button) {
                WidgetActions.BUTTON1 -> filterExpansion = "[abc]$commonChars"
                WidgetActions.BUTTON2 -> filterExpansion = "[def]$commonChars"
                WidgetActions.BUTTON3 -> filterExpansion = "[ghi]$commonChars"
                WidgetActions.BUTTON4 -> filterExpansion = "[jkl]$commonChars"
                WidgetActions.BUTTON5 -> filterExpansion = "[mno]$commonChars"
                WidgetActions.BUTTON6 -> filterExpansion = "[pqrs]$commonChars"
                WidgetActions.BUTTON7 -> filterExpansion = "[tuv]$commonChars"
                WidgetActions.BUTTON8 -> filterExpansion = "[wxyz]$commonChars"
                WidgetActions.BUTTON_CLEAR -> filter =
                        filter!!.replaceFirst("\\[\\w+]\\[\\^a-zA-Z]\\*$".toRegex(), "")
                WidgetActions.BUTTON_CLEAR_ALL -> filter = ""
                else -> {
                }
            }
            if (PInfoHandler.filteredPInfosExists(widgetId) &&
                    PInfoHandler.sizeOfFiltered(widgetId) > 0) {
                filter += filterExpansion
            }
            Log.d(TAG, "getInstalledApps, new filter: $filter")
            if (button != WidgetActions.TEXTFIELD_BUTTON ||
                    !PInfoHandler.filteredPInfosExists(widgetId)) {
                when (button) {
                    WidgetActions.TEXTFIELD_BUTTON ->
                        PInfoHandler.incrementAppIndex(widgetId, 1)
                    WidgetActions.ADDED_NEW_APP -> {
                    }
                    else -> PInfoHandler.setAppIndex(widgetId, 0)
                }
                Prefs.setFilterList(filter, widgetId)

                ptn = Pattern.compile(filter, Pattern.CASE_INSENSITIVE)
                if (PInfoHandler.pInfosNotExist()) {
                    getAllInstalledApps()
                } else {
                    Log.d(TAG, "Using cached list. Size: " + PInfoHandler.sizeOfAll())
                }

                PInfoHandler.setFilteredPInfosMap(widgetId)
                for (i in 0 until PInfoHandler.sizeOfAll()) {
                    val newInfo = PInfoHandler.getAllPInfos()[i]
                    matcher = ptn.matcher(newInfo.appname)
                    if (matcher.find()) {
                        newInfo.match = matcher.start()
                        newInfo.matcherGroup = matcher.group()
                        PInfoHandler.addToFiltered(widgetId, newInfo)
                    }
                }
                PInfoHandler.sortFilteredByMatch(widgetId)

            } else {
                PInfoHandler.incrementAppIndex(widgetId, 1)
            }
            Log.d(TAG, "getInstalledApps end " + Calendar.getInstance().timeInMillis + ", " +
                    (Calendar.getInstance().timeInMillis - start))
        }
    }
}