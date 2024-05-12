package pl.coddev.applu.utils

import android.content.Context
import android.content.SharedPreferences
import pl.coddev.applu.App
import java.util.*

object Prefs {

    const val LAST_APPS_MAX_SIZE = 6
    const val LAUNCHED_BEFORE = "launchedBefore"
    private var prefs: SharedPreferences = App.get()
            .getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)

    private var appIndex: Int? = null
    private var currentApp: String? = null
    private var filter: String? = null
    private var lastApps: String? = null
    private var lastAppsSync: String? = null
    private var featureCount = 0

    @JvmStatic
    fun getFeatureCount(): Int {
        return prefs.getInt(Constants.EXTRA_FEATURE_USAGE, 0)
    }

    @JvmStatic
    fun setFeatureCount(featureCount: Int) {
        putInt(Constants.EXTRA_FEATURE_USAGE, featureCount)
    }

    @JvmStatic
    fun incrementFeatureCount(): Int {
        setFeatureCount(++featureCount)
        return featureCount
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
    fun getFilter(widgetId: Int): String? {
        return prefs.getString(Constants.PREFS_FILTER_LIST + widgetId, "")
    }

    @JvmStatic
    fun setFilter(filter: String?, widgetId: Int) {
        if (this.filter == filter) return
        putString(Constants.PREFS_FILTER_LIST + widgetId, filter)
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
    fun getLastAppsSync(widgetId: Int): String? {
        return prefs.getString(Constants.LAST_APPS_SYNC + widgetId, "")
    }

    @JvmStatic
    fun setLastAppsSync(lastAppsSync: String?, widgetId: Int) {
        if (this.lastAppsSync == lastAppsSync) return
        putString(Constants.LAST_APPS_SYNC + widgetId, lastAppsSync)
    }

    @JvmStatic
    fun getLastAppsSyncList(widgetId: Int): LinkedList<String> {
        val lastAppsSync = getLastAppsSync(widgetId)!!.split("\\|".toRegex())
        return LinkedList(lastAppsSync)
    }

    @JvmStatic
    fun setLastAppsSyncList(lastAppsSyncList: LinkedList<String>, widgetId: Int) {
        val lastAppsString = StringBuilder()
        for (app in lastAppsSyncList) {
            lastAppsString.append(app).append("|")
        }
        setLastAppsSync(lastAppsString.toString(), widgetId)
    }

    @JvmStatic
    fun addToLastAppsSync(newPackage: String, widgetId: Int) {
        val lastAppsList = getLastAppsSyncList(widgetId)
        if (lastAppsList.size > LAST_APPS_MAX_SIZE) lastAppsList.removeLast()
        lastAppsList.remove(newPackage)
        lastAppsList.addFirst(newPackage)
        setLastAppsSyncList(lastAppsList, widgetId)
    }

    @JvmStatic
    fun removeFromLastAppsSync(newPackage: String?, widgetId: Int) {
        val lastAppsList = getLastAppsSyncList(widgetId)
        lastAppsList.remove(newPackage)
        setLastAppsSyncList(lastAppsList, widgetId)
    }

    fun putString(key: String?, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, value: String): String {
        return prefs.getString(key, value) as String
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