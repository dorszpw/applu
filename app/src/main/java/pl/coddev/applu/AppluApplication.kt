package pl.coddev.applu

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.util.Log
import pl.coddev.applu.broadcastreceiver.PackageModifiedReceiver
import pl.coddev.applu.service.DataService

/**
 * Created by piotr woszczek on 30.12.14.
 */
class AppluApplication : Application() {
    private val featureCount = 0

    // DataService
    var mDataService: DataService? = null
    private val mConnection: ServiceConnection? = null

    // =============================================================================================
    // ===== onCreate
    // =============================================================================================
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "OnCreate invoked")
        instance = this
        val intent = Intent(this, DataService::class.java)
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    startForegroundService(intent);
        //} else {
        startService(intent)
        //}

        //registerBroadcastReceiver();
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

    companion object {
        var TAG = "AppluApplication"
        private lateinit var instance: AppluApplication

        @JvmStatic
        fun get(): AppluApplication {
            return instance
        }
    }
}