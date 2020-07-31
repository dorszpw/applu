package pl.coddev.applu.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
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
import pl.coddev.applu.R
import pl.coddev.applu.data.PInfo
import pl.coddev.applu.enums.AppSelectorStatus
import pl.coddev.applu.service.PInfoHandler
import pl.coddev.applu.utils.Constants
import pl.coddev.applu.utils.Log
import pl.coddev.applu.utils.Prefs.addToLastApps
import pl.coddev.applu.utils.Prefs.getAppIndex
import pl.coddev.applu.utils.Prefs.getAppSelectorStatus
import pl.coddev.applu.utils.Prefs.getFilterList
import pl.coddev.applu.utils.Prefs.setAppIndex
import pl.coddev.applu.utils.Prefs.setAppSelectorStatus
import pl.coddev.applu.utils.Prefs.setCurrentApp
import pl.coddev.applu.utils.Prefs.setFilterList
import pl.coddev.applu.utils.Utils
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by pw on 16/03/15.
 */
abstract class UninstallWidget : AppWidgetProvider() {
    @JvmField
    protected var appSelectorStatus = AppSelectorStatus.USER

    enum class WidgetActions {
        TEXTFIELD_BUTTON, BUTTON1, BUTTON2, BUTTON3, BUTTON4, BUTTON5, BUTTON6, BUTTON7, BUTTON8,
        BUTTON_CLEAR, BUTTON_CLEAR_ALL, BUTTON_UNINSTALL, BUTTON_LAUNCH, OTHER, BUTTON_SELECTOR,
        ADDED_NEW_APP, BUTTON_LASTAPP1, BUTTON_LASTAPP2, BUTTON_LASTAPP3, BUTTON_LASTAPP4, BUTTON_LASTAPP5,
        BUTTON_LASTAPP6, BUTTON_LASTAPP7, BUTTON_LASTAPP8, NO_ACTION;

        operator fun next(): WidgetActions {
            val ordinal = if (ordinal + 1 >= values().size) 0 else ordinal + 1
            return values()[ordinal]
        }
    }

    private var action: WidgetActions? = null
    lateinit var pm: PackageManager
    fun setAction(action: WidgetActions?) {
        this.action = action
    }

    abstract fun setupLastAppsButtons(widgetId: Int, views: RemoteViews, context: Context, ids: IntArray)
    abstract fun setupRemoveAllButton(views: RemoteViews, context: Context, ids: IntArray)
    abstract fun switchSelectorStatus(views: RemoteViews)
    abstract fun getRemoteViews(context: Context): RemoteViews
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate" + appWidgetIds.size)
        pm = context.packageManager
        for (i in appWidgetIds.indices) {
            Log.d(TAG, "widget no " + i + ": " + appWidgetIds[i])
            widgetId = appWidgetIds[i]
            val ids = intArrayOf(widgetId)
            appSelectorStatus = getAppSelectorStatus(widgetId)
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
            switchSelectorStatus(views)
            PInfoHandler.setAppIndex(widgetId, getAppIndex(widgetId))
            getInstalledApps(widgetId, action, context, appSelectorStatus, pm)
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
        val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        if (appWidgetIds != null) {
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
                            addToLastApps(currentApp, widgetId)
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
                            addToLastApps(currentApp, widgetId)
                            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
                        }
                    }
                } catch (e: Exception) {
                    if (e.message != null) Log.d(TAG, e.message!!) else e.printStackTrace()
                    Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show()
                }
            } else if (actionString != null && actionString.contains("BUTTON")) {
                Utils.displayRateQuestionIfNeeded()
                if (action == WidgetActions.BUTTON_SELECTOR) {
                    buttonSelectorAction()
                }
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

    private fun buttonSelectorAction() {
        appSelectorStatus = appSelectorStatus.next()
        setAppSelectorStatus(appSelectorStatus, widgetId)
        Log.d(TAG, "Widget/selector: " + widgetId + "/" + appSelectorStatus.name)
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

        @JvmStatic
        fun getInstalledApps(widgetId: Int, button: WidgetActions?, context: Context,
                             appSelectorStatus: AppSelectorStatus?, pm: PackageManager?) {
            val start = Calendar.getInstance().timeInMillis
            Log.d(TAG, "getInstalledApps xstart $start")
            val ptn: Pattern
            var matcher: Matcher
            var filter = getFilterList(widgetId)
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
                WidgetActions.BUTTON_CLEAR -> filter = filter!!.replaceFirst("\\[\\w+]\\[\\^a-zA-Z]\\*$".toRegex(), "")
                WidgetActions.BUTTON_CLEAR_ALL -> filter = ""
                else -> {
                }
            }
            if (PInfoHandler.filteredPInfosExists(widgetId) &&
                    PInfoHandler.sizeOfFiltered(widgetId) > 0 ||
                    PInfoHandler.selectedPInfosNotExist(widgetId)) {
                filter += filterExpansion
            }
            Log.d(TAG, "getInstalledApps, new filter: $filter")
            if (button != WidgetActions.TEXTFIELD_BUTTON || !PInfoHandler.filteredPInfosExists(widgetId)) {
                when (button) {
                    WidgetActions.TEXTFIELD_BUTTON -> PInfoHandler.incrementAppIndex(widgetId, 1)
                    WidgetActions.ADDED_NEW_APP -> {
                    }
                    else -> PInfoHandler.setAppIndex(widgetId, 0)
                }
                setFilterList(filter, widgetId)

                ptn = Pattern.compile(filter, Pattern.CASE_INSENSITIVE)
                if (PInfoHandler.selectedPInfosNotExist(widgetId) || button == WidgetActions.BUTTON_SELECTOR) {
                    if (PInfoHandler.pInfosNotExist()) {
                        val packs = pm!!.getInstalledPackages(0) as ArrayList<PackageInfo>
                        PInfoHandler.setSelectedPInfosMap(widgetId)
                        for (i in packs.indices) {
                            val pi = packs[i]
                            if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus, context)) continue
                            val newInfo = PInfo()
                            newInfo.appname = pi.applicationInfo.loadLabel(pm).toString()
                            newInfo.pname = pi.packageName
                            newInfo.isSystemPackage = PInfoHandler.isSystemPackage(pi)
                            PInfoHandler.addToSelected(widgetId, newInfo)
                        }
                        Log.d(TAG, "Loaded new ALL list. Size: " + PInfoHandler.sizeOfAll())
                    } else {
                        PInfoHandler.setSelectedPInfosMap(widgetId)
                        for (pi in PInfoHandler.getAllPInfos()) {
                            if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus)) continue
                            PInfoHandler.addToSelected(widgetId, pi)
                        }
                    }
                } else {
                    Log.d(TAG, "Using cached SELECTED list. Size: " + PInfoHandler.sizeOfSelected(widgetId))
                }

                PInfoHandler.setFilteredPInfosMap(widgetId)
                //PInfoHandler.clearFiltered(widgetId);
                for (i in 0 until PInfoHandler.sizeOfSelected(widgetId)) {
                    val newInfo = PInfoHandler.getPInfoFromSelected(widgetId, i) ?: continue
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
            Log.d(TAG, "getInstalledApps xend " + Calendar.getInstance().timeInMillis + ", " +
                    (Calendar.getInstance().timeInMillis - start))
        }
    }
}