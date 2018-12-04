package pl.coddev.applu.c;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by pw on 23/03/15.
 */
public class Constants {
    public static final String TAG = "Constants";
    public static final String EXTRA_RAN_BEFORE = "ranBefore";
    public static final String PREFS_FILTER_LIST = "filter_list";
    public static final String SHOW_MORE_STRING = "▼ ";
    public static final String NO_MATCHES_STRING = "[:( no results]";
    public static final boolean CRASHLYTICS_LOGS = false;
    public static final String APP_SELECTOR_STATUS = "appSelectorStatus";
    public static final String APP_INDEX = "appIndex";
    public static final String CURRENT_APP = "currentApp";
    public static final String LAST_APPS_SYNC = "lastAppsSync";
    public static final String LAST_APPS = "lastApps";
    public static final String LAST_APP_PACKAGE = "lastAppPackage";
    public static final String PREF_FILE = "pref_file_";
    public static final String EXTRA_PACKAGE_NAME = "extra_packageName";
    public static final String PREFS_FILE = "prefs_main" ;
//    public static final String STORE = "play";
//    //public static final String STORE = "amazon";
//    public static final String STORE_PLAY = "play";
//    public static final String STORE_AMAZON = "amazon";
    public static final String APPSTORE_LINK_PRO;
    public static final String APPSTORE_LINK;
    public static final String EXTRA_FEATURE_USAGE = "feature_usage";
    public static final int FEATURE_USAGE_MAX = 50;


    static {
        try {
            Properties props = new Properties();
            for (Object obj : props.values()) {
                android.util.Log.i("PROPS", (String) obj);
            }
            props.load(Constants.class.getClassLoader().getResourceAsStream("constants.properties"));

            APPSTORE_LINK_PRO = props.getProperty("appstore.pro.url");
            APPSTORE_LINK = props.getProperty("appstore.url");
            Log.d(TAG, "pro link, free link: " + APPSTORE_LINK_PRO + ", " + APPSTORE_LINK);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
