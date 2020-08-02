package pl.coddev.applu.ui

import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat
import pl.coddev.applu.App
import pl.coddev.applu.R
import pl.coddev.applu.data.PInfo
import pl.coddev.applu.enums.WidgetActions
import pl.coddev.applu.service.DataService
import pl.coddev.applu.service.PInfoHandler
import pl.coddev.applu.utils.Constants
import pl.coddev.applu.utils.Log
import pl.coddev.applu.utils.Prefs.addToLastAppsSync
import pl.coddev.applu.utils.Prefs.getAppIndex
import pl.coddev.applu.utils.Prefs.setAppIndex
import pl.coddev.applu.utils.Prefs.setCurrentApp
import pl.coddev.applu.utils.Utils

/**
 * Created by pw on 16/03/15.
 */
abstract class UninstallWidget : AppWidgetProvider() {
    private var action: WidgetActions? = null
    lateinit var pm: PackageManager
    fun setAction(action: WidgetActions?) {
        this.action = action
    }

    abstract fun setupLastAppsButtons(widgetId: Int, views: RemoteViews, context: Context, ids: IntArray)
    abstract fun setupRemoveAllButton(views: RemoteViews, context: Context, ids: IntArray)
    abstract fun getRemoteViews(context: Context): RemoteViews
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate" + appWidgetIds.size)
        pm = context.packageManager



        for (i in appWidgetIds.indices) {
            Log.d(TAG, "widget no " + i + ": " + appWidgetIds[i])
            widgetId = appWidgetIds[i]
            val ids = intArrayOf(widgetId)
            // Get the layout for the App Widget and attach an on-click listener
            val views: RemoteViews = getRemoteViews(context)
            views.setOnClickPendingIntent(R.id.searchText, buildPendingIntent(context, WidgetActions.TEXTFIELD_BUTTON.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton1, buildPendingIntent(context, WidgetActions.BUTTON1.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton2, buildPendingIntent(context, WidgetActions.BUTTON2.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton3, buildPendingIntent(context, WidgetActions.BUTTON3.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton4, buildPendingIntent(context, WidgetActions.BUTTON4.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton5, buildPendingIntent(context, WidgetActions.BUTTON5.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton6, buildPendingIntent(context, WidgetActions.BUTTON6.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton7, buildPendingIntent(context, WidgetActions.BUTTON7.name, ids))
            views.setOnClickPendingIntent(R.id.searchButton8, buildPendingIntent(context, WidgetActions.BUTTON8.name, ids))
            views.setOnClickPendingIntent(R.id.clearButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR.name, ids))
            views.setOnClickPendingIntent(R.id.appSelectorButton, buildPendingIntent(context, WidgetActions.BUTTON_SELECTOR.name, ids))
            setupLastAppsButtons(widgetId, views, context, ids)
            setupRemoveAllButton(views, context, ids)
            PInfoHandler.setAppIndex(widgetId, getAppIndex(widgetId))
            DataService.getAppsByFilter(widgetId, action)
            PInfoHandler.rollIndex(widgetId)
            var packageName = ""
            if (PInfoHandler.sizeOfFiltered(widgetId) > 0) {
                val pinfo = PInfoHandler.getCurrentPInfo(widgetId)
                if (pinfo != null) {
                    packageName = pinfo.pname
                    views.setOnClickPendingIntent(R.id.uninstallButton,
                            buildPendingIntentForActionButtons(context, packageName,
                                    WidgetActions.BUTTON_UNINSTALL.name, ids))
                    views.setOnClickPendingIntent(R.id.launchButton,
                            buildPendingIntentForActionButtons(context, packageName,
                                    WidgetActions.BUTTON_LAUNCH.name, ids))
                    Log.d(TAG, "App package name: " + pinfo.pname)
                    val iconBitmap = Cache.instance?.getBitmapFromMemCache(pinfo.pname, pm)
                    views.setImageViewBitmap(R.id.launchButton, iconBitmap)
                    views.setTextViewText(R.id.searchText, getSpannableForField(context, pinfo))
                }
            } else {
                views.setTextViewText(R.id.searchText, context.getString(R.string.no_matches))
                views.setImageViewResource(R.id.launchButton, R.drawable.search_problem_128)
                views.setOnClickPendingIntent(R.id.uninstallButton,
                        buildPendingIntentForActionButtons(context, null,
                                WidgetActions.NO_ACTION.name, ids))
                views.setOnClickPendingIntent(R.id.launchButton,
                        buildPendingIntentForActionButtons(context, null,
                                WidgetActions.NO_ACTION.name, ids))
            }
            setAppIndex(PInfoHandler.getAppIndex(widgetId), widgetId)
            setCurrentApp(packageName, widgetId)
            Log.d(TAG, "onUpdate prefs, class:  " + this.javaClass.name +
                    ", saved: " + PInfoHandler.getAppIndex(widgetId) + "/" + packageName)
            views.setViewVisibility(R.id.progressBar, View.GONE)
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    private fun getSpannableForField(context: Context, pInfo: PInfo): CharSequence {
        val spannableShowMore: SpannableString
        val spannableAppName: SpannableString
        var showMore = ""
        if (PInfoHandler.sizeOfFiltered(widgetId) > 1) {
            val sb = StringBuilder(7)
            sb.append(Constants.SHOW_MORE_STRING)
                    .append(PInfoHandler.getAppIndex(widgetId) + 1)
                    .append("/")
                    .append(PInfoHandler.sizeOfFiltered(widgetId))
                    .append(" ")
            showMore = sb.toString()
        }
        if (!pInfo.isRemoved) {
            spannableShowMore = SpannableString(showMore)
            spannableShowMore.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_nasty)),
                    0, showMore.length, 0)
            spannableAppName = SpannableString(pInfo.appname)
            spannableAppName.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_nasty)),
                    pInfo.match, pInfo.match + pInfo.matcherGroup.length, 0)
        } else {
            spannableShowMore = SpannableString(showMore)
            spannableShowMore.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_nasty)),
                    0, showMore.length, 0)
            spannableAppName = SpannableString(pInfo.appname)
            spannableAppName.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)),
                    0, pInfo.appname.length, 0)
            spannableAppName.setSpan(StrikethroughSpan(), 0, pInfo.appname.length, 0)
        }
        return TextUtils.concat(spannableShowMore, spannableAppName)
    }

    fun buildPendingIntent(context: Context?, actionName: String?, ids: IntArray?): PendingIntent {
        val intent = Intent(context, this.javaClass)
        intent.action = actionName
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // separate method to add packageName, just to be sure that what user uninstalls is right
    private fun buildPendingIntentForActionButtons(context: Context?, packageName: String?,
                                                   actionName: String?, ids: IntArray?): PendingIntent {
        val intent = Intent(context, this.javaClass)
        intent.action = actionName
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        intent.putExtra(Constants.CURRENT_APP, packageName)
        return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "---onReceive received")
        android.util.Log.d(TAG, "onReceive: ${intent.getStringExtra("Action")}")

        val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        if (appWidgetIds != null) {
            if (WidgetActions.ADDED_NEW_APP.name.equals(intent.getStringExtra("Action"))) {
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
            }
            val widgetId = appWidgetIds[0]
            val currentApp: String?
            val actionString = intent.action
            Log.d(TAG, "Intent action: $actionString")
            action = try {
                WidgetActions.valueOf(actionString!!)
            } catch (e: IllegalArgumentException) {
                WidgetActions.OTHER
            }
            if (action == WidgetActions.BUTTON_UNINSTALL) {
                currentApp = intent.getStringExtra(Constants.CURRENT_APP)
                if (currentApp != null && currentApp != "") {
                    val packageUri = Uri.parse("package:$currentApp")
                    val uninstallIntent = Intent(Intent.ACTION_DELETE, packageUri)
                    uninstallIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    uninstallIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", true)
                    context.startActivity(uninstallIntent)
                }
            } else if (action == WidgetActions.BUTTON_LAUNCH) {
                currentApp = intent.getStringExtra(Constants.CURRENT_APP)
                if (currentApp != null && currentApp != "") {
                    try {
                        val i = context.packageManager.getLaunchIntentForPackage(currentApp)
                        if (i != null) {
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(i)
                            addToLastAppsSync(currentApp, widgetId)
                        }
                    } catch (e: Exception) {
                        if (e.message != null) Log.d(TAG, e.message!!) else e.printStackTrace()
                        Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show()
                    }
                }
            } else if (actionString != null && actionString.contains("LASTAPP")) {
                currentApp = getLastApp(actionString, widgetId)
                try {
                    if (currentApp != null && currentApp != "") {
                        val i = context.packageManager.getLaunchIntentForPackage(currentApp)
                        if (i != null) {
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(i)
                            addToLastAppsSync(currentApp, widgetId)
                            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
                        }
                    }
                } catch (e: Exception) {
                    if (e.message != null) Log.d(TAG, e.message!!) else e.printStackTrace()
                    Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show()
                }
            } else if (actionString != null && actionString.contains("BUTTON")) {
                Utils.displayRateQuestionIfNeeded()
                val views = getRemoteViews(context)
                //views.setProgressBar(R.id.progressBar, 0, 0, true);
                views.setViewVisibility(R.id.progressBar, View.VISIBLE)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                // every widget is separate!!!
                appWidgetManager.partiallyUpdateAppWidget(widgetId, views)
                onUpdate(context, appWidgetManager, appWidgetIds)
            } else {
                super.onReceive(context, intent)
            }
        } else {
            super.onReceive(context, intent)
        }
    }

    open fun getLastApp(action: String?, widgetId: Int): String? {
        return null
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d(TAG, "onDeleted")
    }

    companion object {
        private const val TAG = "UninstallWidget"
        private var widgetId = 0



        private fun isServiceRunning(): Boolean {
            val manager =
                    App.get().context.getSystemService(Context.ACTIVITY_SERVICE)
                            as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (DataService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        private fun startDataService() {
            val serviceIntent = Intent(App.get().context, DataService::class.java)
            App.get().context.startService(serviceIntent)
        }
    }
}