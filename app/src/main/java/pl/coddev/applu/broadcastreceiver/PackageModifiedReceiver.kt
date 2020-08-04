package pl.coddev.applu.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import pl.coddev.applu.data.PInfo
import pl.coddev.applu.enums.WidgetActions
import pl.coddev.applu.service.DataService
import pl.coddev.applu.service.PInfoHandler.addToAll
import pl.coddev.applu.service.PInfoHandler.removeFromAll
import pl.coddev.applu.utils.Log.d
import pl.coddev.applu.utils.Log.e
import pl.coddev.applu.utils.Prefs.removeFromLastAppsSync

class PackageModifiedReceiver : BroadcastReceiver() {
    private lateinit var pm: PackageManager
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: ")
        val data = intent.data
        pm = context.packageManager
        var modifiedPackageName = data!!.encodedSchemeSpecificPart
        if (modifiedPackageName == null) modifiedPackageName = "no package"
        val action = intent.action
        val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
//        val appWidgetManager = AppWidgetManager.getInstance(context)
//        val appWidgetProviderInfoList = appWidgetManager.installedProviders
        val pi: PackageInfo?
        var modifiedInfo: PInfo? = null
        try {
            pi = pm.getPackageInfo(modifiedPackageName, 0)
            modifiedInfo = PInfo()
            modifiedInfo.appname = pi.applicationInfo.loadLabel(pm).toString()
            modifiedInfo.pname = pi.packageName
        } catch (e: PackageManager.NameNotFoundException) {
            e(TAG, e.message!!)
        }
        val packageRemoved = Intent.ACTION_PACKAGE_REMOVED == action || Intent.ACTION_PACKAGE_FULLY_REMOVED == action
        if (!replacing && packageRemoved) {
            d(TAG, "Package removed: $modifiedPackageName")
            removeFromAll(modifiedPackageName)
        } else if (replacing && Intent.ACTION_PACKAGE_REMOVED == action) {
            d(TAG, "Package replaced: $modifiedPackageName")
        } else if (Intent.ACTION_PACKAGE_ADDED == action) {
            d(TAG, "Package added: $modifiedPackageName")
            addToAll(modifiedInfo!!)
        }

        for ((widgetClass, widgetIds) in DataService.getMyWidgets()) {
            for (widgetId in widgetIds) {
                d(TAG, "onReceive PMR, class: $widgetClass")
                // add to ALL lists, if falls into selector
                if (packageRemoved) {
                    removeFromLastAppsSync(modifiedPackageName, widgetId)
                }
            }
            DataService.updateWiddgetsOfClassOnAction(widgetClass, widgetIds, WidgetActions.ADDED_NEW_APP)
        }

//        for (i in appWidgetProviderInfoList.indices) {
//            val widgetPackage = appWidgetProviderInfoList[i].provider.packageName
//            if (widgetPackage == context.packageName) {
//                var widgetIds: IntArray
//                try {
//                    val widgetClass = Class.forName(appWidgetProviderInfoList[i].provider.className)
//                    widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, widgetClass))
//                    d(TAG, "appWIdgetIds length: " + widgetIds.size)
//                    for (widgetId in widgetIds) {
//                        d(TAG, "onReceive PMR, class: $widgetClass")
//
//                        // add to ALL lists, if falls into selector
//                        if (Intent.ACTION_PACKAGE_ADDED == action) {
//                            addToAll(modifiedInfo!!)
//                        } else if (packageRemoved) {
//                            removeFromLastAppsSync(modifiedPackageName, widgetId)
//                        }
//
////                        if (!PInfoHandler.filteredPInfosExists(widgetId)) {
////                            UninstallWidget.getInstalledApps(widgetId, UninstallWidget.WidgetActions.BUTTON_SELECTOR,
////                                    context, selector, this.pm);
////                        }
//                    }
//                    if (widgetIds.isNotEmpty()) {
//                        val intentWidget = Intent(get(), widgetClass)
//                        intentWidget.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//                        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
//// since it seems the onUpdate() is only fired on that:
//                        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
//                        intentWidget.putExtra("Action", WidgetActions.ADDED_NEW_APP.name)
//                        get().sendBroadcast(intentWidget)
//                    }
//                } catch (e: ClassNotFoundException) {
//                    e(TAG, e.message!!)
//                }
//            }
//        }
    }

    companion object {
        const val TAG = "PackageModifiedReceiver"
    }
}