package pl.coddev.applu.c;

import android.content.Context;
import android.content.SharedPreferences;

import pl.coddev.applu.AppluApplication;
import pl.coddev.applu.enums.AppSelectorStatus;

public class Prefs {

    public static SharedPreferences prefs = AppluApplication.get()
            .getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    public static final String LAUNCHED_BEFORE = "launchedBefore";
    private static Prefs instance;

    private Integer featureCount;
    private String appSelectorStatus;
    private Integer appIndex;
    private String currentApp;
    private String filterList;
    private String lastApps;
    private String lastAppsSync;

    public static Prefs get() {
        if (instance == null) return new Prefs();
        else return instance;
    }

    public static SharedPreferences.Editor getEdit() {
        return prefs.edit();
    }

    public int getFeatureCount() {
        if (featureCount == null) {
            featureCount = prefs.getInt(Constants.EXTRA_FEATURE_USAGE, 0);
        }
        return featureCount;
    }

    public void setFeatureCount(int featureCount) {
        this.featureCount = featureCount;
        putInt(Constants.EXTRA_FEATURE_USAGE, featureCount);
    }

    public AppSelectorStatus getAppSelectorStatus(int widgetId) {
        if (appSelectorStatus == null) {
            appSelectorStatus = prefs.getString(Constants.APP_SELECTOR_STATUS + widgetId,
                    AppSelectorStatus.USER.name());
        }
        return AppSelectorStatus.valueOf(appSelectorStatus);
    }

    public void setAppSelectorStatus(AppSelectorStatus appSelectorStatus, int appWidgetId) {
        this.appSelectorStatus = appSelectorStatus.name();
        putString(Constants.EXTRA_FEATURE_USAGE + appWidgetId, appSelectorStatus.name());
    }

    public Integer getAppIndex(int widgetId) {
        if (appIndex == null) {
            appIndex = prefs.getInt(Constants.APP_INDEX + widgetId, 0);
        }
        return appIndex;
    }

    public void setAppIndex(Integer appIndex, int widgetId) {
        this.appIndex = appIndex;
        putInt(Constants.APP_INDEX + widgetId, appIndex);
    }

    public void setCurrentApp(String currentApp, int widgetId) {
        this.currentApp = currentApp;
        putString(Constants.EXTRA_FEATURE_USAGE + widgetId, currentApp);
    }

    public String getCurrentApp(int widgetId) {
        if (currentApp == null) {
            currentApp = prefs.getString(Constants.CURRENT_APP + widgetId, "");
        }
        return currentApp;
    }

    public String getFilterList(int widgetId) {
        if (filterList == null) {
            filterList = prefs.getString(Constants.PREFS_FILTER_LIST + widgetId, "");
        }
        return filterList;
    }

    public void setFilterList(String filterList, int widgetId) {
        this.filterList = filterList;
        putString(Constants.PREFS_FILTER_LIST + widgetId, filterList);
    }

    public String getLastApps(int widgetId) {
        if (lastApps == null) {
            lastApps = prefs.getString(Constants.LAST_APPS + widgetId, "");
        }
        return lastApps;
    }

    public void setLastApps(String lastApps, int widgetId) {
        this.lastApps = lastApps;
        putString(Constants.LAST_APPS + widgetId, lastApps);
    }

    public String getLastAppsSync(int widgetId) {
        if (lastAppsSync == null) {
            lastAppsSync = prefs.getString(Constants.LAST_APPS_SYNC + widgetId, "");
        }
        return lastAppsSync;
    }

    public void setLastAppsSync(String lastAppsSync, int widgetId) {
        this.lastAppsSync = lastAppsSync;
        putString(Constants.LAST_APPS_SYNC + widgetId, lastAppsSync);
    }

    public static void remove(String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public static String getString(String key) {
        return prefs.getString(key, null);
    }

    public static void remove(String key) {
        prefs.edit().remove(key)
                .apply();
    }

    public static void putBool(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public static void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public static void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public static boolean bool(String key) {
        return prefs.getBoolean(key, false);
    }

    public static void clear() {
        prefs.edit().clear().apply();
    }

    public static boolean runningThisFirstTime(String tag) {
        if (!prefs.getBoolean(tag, false)) {
            prefs.edit().putBoolean(tag, true).apply();
            return true;
        } else {
            return false;
        }
    }

}
