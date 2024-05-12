package pl.coddev.applu.utils

import android.util.Log

/**
 * Created by Piotr Woszczek on 27/04/15.
 */
object Log {
    @JvmStatic
    fun d(tag: String, message: String) {
        logIt(Log.DEBUG, tag, message)
    }

    fun i(tag: String, message: String) {
        logIt(Log.INFO, tag, message)
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        logIt(Log.ERROR, tag, message)
    }

    fun v(tag: String, message: String) {
        logIt(Log.VERBOSE, tag, message)
    }

    fun w(tag: String, message: String) {
        logIt(Log.WARN, tag, message)
    }

    private fun logIt(level: Int, tag: String, message: String) {
        if (Constants.CRASHLYTICS_LOGS) {
            //Crashlytics.log(level, tag, message);
        } else {
            Log.println(level, tag, message)
        }
    }
}