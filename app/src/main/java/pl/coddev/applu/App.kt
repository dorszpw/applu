package pl.coddev.applu

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.AsyncTask
import android.util.Log
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import pl.coddev.applu.broadcastreceiver.PackageModifiedReceiver
import pl.coddev.applu.service.DataService


/**
 * Created by piotr woszczek on 30.12.14.
 */
class App : Application() {

    // DataService
    var mDataService: DataService? = null
    private val mConnection: ServiceConnection? = null
    lateinit var context: Context

    // =============================================================================================
    // ===== onCreate
    // =============================================================================================
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "OnCreate invoked")
        instance = this
        context = applicationContext
        initAppCenter()
        val intent = Intent(this, DataService::class.java)
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    startForegroundService(intent);
        //} else {
        try {
            startService(intent)
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.message)
        }

        AsyncTask.execute {
            DataService.getAllInstalledApps()
        }
        //}
        registerBroadcastReceiver();
    } // onCreate - end

    private fun registerBroadcastReceiver() {
        val packageModifiedReceiver = PackageModifiedReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addDataScheme("package")
        registerReceiver(packageModifiedReceiver, filter)
    }

    private fun initAppCenter() {
        if (BuildConfig.BUILD_TYPE == "debug") {
            AppCenter.start(this, "eeaff29b-02de-430f-a3bf-642a8ffc863e",
                    Analytics::class.java, Crashes::class.java)
            //AppCenter.setLogLevel(Log.VERBOSE);
        } else if (BuildConfig.BUILD_TYPE == "release") {
            AppCenter.start(this, "af0c46d8-ba26-4203-b40d-539e87120c63",
                    Analytics::class.java, Crashes::class.java)
        }
    }

    companion object {
        var TAG = "AppluApplication"
        private lateinit var instance: App

        @JvmStatic
        fun get(): App {
            return instance
        }
    }
}