package pl.coddev.applu.utils

import android.content.Context
import android.content.SharedPreferences
import pl.coddev.applu.AppluApplication
import pl.coddev.applu.enums.AppSelectorStatus
import java.util.*

object Prefs {

    const val LAST_APPS_MAX_SIZE = 6
    const val LAUNCHED_BEFORE = "launchedBefore"
    private var prefs: SharedPreferences = AppluApplication.get()
            .getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)

    private var appSelectorStatus: String? = null
    private var appIndex: Int? = null
    private var currentApp: String? = null
    private var filterList: String? = null
    private var lastApps: String? = null
    private var featureCount = 0

    @JvmStatic
    fun getFeatureCount(): Int {
        return prefs.getInt(Constants.EXTRA_FEATURE_USAGE, 0)
    }

    @JvmStatic
    fun setFeatureCount(featureCount: Int) {
        if (this.featureCount == featureCount) return
        putInt(Constants.EXTRA_FEATURE_USAGE, featureCount)
    }

    @JvmStatic
    fun incrementFeatureCount(): Int {
        setFeatureCount(++featureCount)
        return featureCount
    }

    @JvmStatic
    fun getAppSelectorStatus(widgetId: Int): AppSelectorStatus {
        appSelectorStatus = prefs.getString(Constants.APP_SELECTOR_STATUS + widgetId,
                AppSelectorStatus.ALL.name)
        return AppSelectorStatus.valueOf(appSelectorStatus!!)
    }

    @JvmStatic
    fun setAppSelectorStatus(appSelectorStatus: AppSelectorStatus, appWidgetId: Int) {
        if (appSelectorStatus.name == this.appSelectorStatus) return
        putString(Constants.EXTRA_FEATURE_USAGE + appWidgetId, appSelectorStatus.name)
    }

    @JvmStatic
    fun getAppIndex(widgetId: Int): Int {
        return prefs.getInt(Constants.APP_INDEX + widgetId, 0)
    }

    @JvmStatic
    fun setAppIndex(appIndex: Int?, widgetId: Int) {
        if (this.appIndex == appIndex || appIndex == null) return
        putInt(Constants.APP_INDEX + widgetId, appIndex)
    }

    @JvmStatic
    fun setCurrentApp(currentApp: String?, widgetId: Int) {
        if (this.currentApp == currentApp) return
        putString(Constants.EXTRA_FEATURE_USAGE + widgetId, currentApp)
    }

    @JvmStatic
    fun getCurrentApp(widgetId: Int): String? {
        return prefs.getString(Constants.CURRENT_APP + widgetId, "")
    }

    @JvmStatic
    fun getFilterList(widgetId: Int): String? {
        return prefs.getString(Constants.PREFS_FILTER_LIST + widgetId, "")
    }

    @JvmStatic
    fun setFilterList(filterList: String?, widgetId: Int) {
        if (this.filterList == filterList) return
        putString(Constants.PREFS_FILTER_LIST + widgetId, filterList)
    }

    @JvmStatic
    fun getLastApps(widgetId: Int): String? {
        return prefs.getString(Constants.LAST_APPS + widgetId, "")
    }

    @JvmStatic
    fun setLastApps(lastApps: String?, widgetId: Int) {
        if (this.lastApps == lastApps) return
        putString(Constants.LAST_APPS + widgetId, lastApps)
    }

    @JvmStatic
    fun getLastAppsList(widgetId: Int): LinkedList<String> {
        val lastApps = getLastApps(widgetId)!!.split("\\|".toRegex())
        return LinkedList(lastApps)
    }

    @JvmStatic
    fun setLastAppsList(lastAppsList: LinkedList<String>, widgetId: Int) {
        val lastAppsString = StringBuilder()
        for (app in lastAppsList) {
            lastAppsString.append(app).append("|")
        }
        setLastApps(lastAppsString.toString(), widgetId)
    }

    @JvmStatic
    fun addToLastApps(newPackage: String, widgetId: Int) {
        val lastAppsList = getLastAppsList(widgetId)
        if (lastAppsList.size > LAST_APPS_MAX_SIZE) lastAppsList.removeLast()
        lastAppsList.remove(newPackage)
        lastAppsList.addFirst(newPackage)
        setLastAppsList(lastAppsList, widgetId)
    }

    @JvmStatic
    fun removeFromLastApps(newPackage: String?, widgetId: Int) {
        val lastAppsList = getLastAppsList(widgetId)
        lastAppsList.remove(newPackage)
        setLastAppsList(lastAppsList, widgetId)
    }

    fun putString(key: String?, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    fun putInt(key: String?, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun bool(key: String?): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun runningThisFirstTime(tag: String?): Boolean {
        return if (!prefs.getBoolean(tag, false)) {
            prefs.edit().putBoolean(tag, true).apply()
            true
        } else {
            false
        }
    }
}