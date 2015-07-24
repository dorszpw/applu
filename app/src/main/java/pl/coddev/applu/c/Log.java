package pl.coddev.applu.c;

import com.crashlytics.android.Crashlytics;

/**
 * Created by Piotr Woszczek on 27/04/15.
 */
public class Log {


    public static void d(String tag, String message){
        logIt(android.util.Log.DEBUG, tag, message);
    }

    public static void i(String tag, String message){
        logIt(android.util.Log.INFO, tag, message);
    }

    public static void e(String tag, String message){
        logIt(android.util.Log.ERROR, tag, message);
    }

    public static void v(String tag, String message){
        logIt(android.util.Log.VERBOSE, tag, message);
    }

    public static void w(String tag, String message){
        logIt(android.util.Log.WARN, tag, message);
    }

    private static void logIt(int level, String tag, String message){
        if(Constants.CRASHLYTICS_LOGS) {
            Crashlytics.log(level, tag, message);
        } else {
            android.util.Log.println(level, tag, message);
        }
    }
}
