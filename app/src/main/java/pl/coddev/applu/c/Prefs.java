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

    public AppSelectorStatus getAppSelectorStatus(int appWidgetId) {
        if (appSelectorStatus == null) {
            appSelectorStatus = prefs.getString(Constants.APP_SELECTOR_STATUS + appWidgetId,
                    AppSelectorStatus.USER.name());
        }
        return AppSelectorStatus.valueOf(appSelectorStatus);
    }

    public void setAppSelectorStatus(AppSelectorStatus appSelectorStatus) {
        this.appSelectorStatus = appSelectorStatus.name();
        putString(Constants.EXTRA_FEATURE_USAGE, appSelectorStatus.name());
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
