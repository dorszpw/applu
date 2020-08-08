package pl.coddev.applu.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pl.coddev.applu.App
import pl.coddev.applu.R
import pl.coddev.applu.broadcastreceiver.PackageModifiedReceiver
import pl.coddev.applu.data.PInfo
import pl.coddev.applu.enums.WidgetActions
import pl.coddev.applu.utils.Prefs
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

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
        private var gettingAllInstalledApps: AtomicBoolean = AtomicBoolean(false)
        private const val TAG = "DataService"
        private var forceUpdateLabels = false

        fun getAllInstalledApps() {
            if (!gettingAllInstalledApps.get()) {
                gettingAllInstalledApps.set(true)
                val start = Calendar.getInstance().timeInMillis
                Log.d(TAG, "getAllInstalledApps start $start")

                updateAllWidgetsOnAction(WidgetActions.ON_RELOAD_APP_LIST)

                try {
                    val pm = App.get().packageManager
                    // one of the most consuming tasks
                    val packs: ArrayList<PackageInfo> = pm.getInstalledPackages(0) as ArrayList<PackageInfo>
                    //PInfoHandler.setAllPInfos();
                    val allInfos = ArrayList<PInfo>()
                    for (i in packs.indices) {
                        val pi = packs[i]
                        if (pm.getLaunchIntentForPackage(pi.packageName) == null) continue
                        val newInfo = PInfo()
                        if (forceUpdateLabels) {
                            newInfo.appname = pi.applicationInfo.loadLabel(pm).toString()
                        } else {
                            newInfo.appname = Prefs.getString(pi.packageName, "")
                            if (newInfo.appname == "") {
                                // most time consuming task!
                                newInfo.appname = pi.applicationInfo.loadLabel(pm).toString()
                                Prefs.putString(pi.packageName, newInfo.appname)
                            }
                        }
                        newInfo.isSystemPackage = PInfoHandler.isSystemPackage(pi)
                        newInfo.pname = pi.packageName
                        if (!allInfos.contains(newInfo)) allInfos.add(newInfo)
                    }
                    forceUpdateLabels = false
                    // add all to synchronized ArrayList, not one by one
                    PInfoHandler.setAllPInfos(allInfos)
                    Log.d(TAG, "onCreate number of packages from PM: " + packs.size)
                } catch (e: Exception) {
                    Toast.makeText(App.get(), App.get().getString(R.string.cannot_get_all_apps),
                            Toast.LENGTH_LONG).show()
                    Log.e(TAG, "getAllInstalledApps: {${e.message}", e)
                }
                updateAllWidgetsOnAction(WidgetActions.RELOADED_APP_LIST)
                Log.d(TAG, "getAllInstalledApps END, duration = " +
                        (Calendar.getInstance().timeInMillis - start))

                Timer().schedule(timerTask { gettingAllInstalledApps.set(false) }, 1000)
            } else {
                Log.d(TAG, "getAllInstalledApps: getting in progress already")
            }
        }

        fun getAppsByFilter(widgetId: Int, button: WidgetActions?) {
            val start = Calendar.getInstance().timeInMillis
            Log.d(TAG, "getAppsByFilter start $start for widgetId: $widgetId")
            val ptn: Pattern
            var matcher: Matcher
            var filter = Prefs.getFilter(widgetId)
            Log.d(TAG, "getAppsByFilter, last filter: $filter")
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
            Log.d(TAG, "getAppsByFilter, new filter: $filter")
            if (button != WidgetActions.TEXTFIELD_BUTTON ||
                    !PInfoHandler.filteredPInfosExists(widgetId)) {
                when (button) {
                    WidgetActions.TEXTFIELD_BUTTON ->
                        PInfoHandler.incrementAppIndex(widgetId, 1)
                    WidgetActions.ADDED_OR_REMOVED_APP -> {
                    }
                    else -> PInfoHandler.setAppIndex(widgetId, 0)
                }
                Prefs.setFilter(filter, widgetId)

                ptn = Pattern.compile(filter, Pattern.CASE_INSENSITIVE)
                if (PInfoHandler.pInfosNotExist()) {
                    AsyncTask.execute {
                        getAllInstalledApps()
                    }
                    return
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
            Log.d(TAG, "getAppsByFilter end " + Calendar.getInstance().timeInMillis + ", " +
                    (Calendar.getInstance().timeInMillis - start))
        }

        fun setForceUpdateLabels() {
            PInfoHandler.getAllPInfos().clear()
            forceUpdateLabels = true
        }

        fun getMyWidgets(): HashMap<Class<*>, IntArray> {
            val widgetMap = HashMap<Class<*>, IntArray>()
            val appWidgetManager = AppWidgetManager.getInstance(App.get())
            val appWidgetProviderInfoList = appWidgetManager.installedProviders

            for (pi in appWidgetProviderInfoList.filter { awpi -> awpi.provider.packageName == App.get().packageName }) {
                var widgetIds: IntArray
                try {
                    val widgetClass = Class.forName(pi.provider.className)
                    widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(App.get(), widgetClass))
                    Log.d(TAG, "appWIdgetIds length: " + widgetIds.size)
                    if (widgetIds != null && widgetIds.isNotEmpty()) widgetMap[widgetClass] = widgetIds
                } catch (e: ClassNotFoundException) {
                    Log.e(TAG, e.message!!)
                }
            }
            return widgetMap
        }

        fun updateAllWidgetsOnAction(action: WidgetActions) {
            for ((widgetClass, widgetIds) in getMyWidgets()) {
                updateWiddgetsOfClassOnAction(widgetClass, widgetIds, action)
            }
        }

        fun updateWiddgetsOfClassOnAction(widgetClass: Class<*>, widgetIds: IntArray, action: WidgetActions) {
            val intentWidget = Intent(App.get(), widgetClass)
            intentWidget.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
            intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            intentWidget.action = action.name
            Log.d(TAG, "updateWiddgetsOfClassOnAction: widgetClass: ${widgetClass.name} widgetIds: ${widgetIds.contentToString()}")
            App.get().sendBroadcast(intentWidget)
        }
    }
}