package pl.coddev.applu.ui

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import pl.coddev.applu.R
import pl.coddev.applu.utils.Prefs
import pl.coddev.applu.utils.Prefs.getLastApps

/**
 * Created by Piotr Woszczek on 23/07/15.
 */
abstract class UninstallWidgetM : UninstallWidget() {
    abstract override fun getRemoteViews(context: Context): RemoteViews
    override fun getLastApp(action: String?, widgetId: Int): String? {
        if (action == null) return null
        var lastApp = ""
        val buttonNumber = action.substring(action.length - 1).toInt()
        // get from "display" list
        val lastApps = getLastApps(widgetId)!!.split("\\|".toRegex()).toTypedArray()
        if (lastApps.size >= buttonNumber) {
            lastApp = lastApps[buttonNumber - 1]
        }
        return lastApp
    }

    override fun setupLastAppsButtons(widgetId: Int, views: RemoteViews, context: Context, ids: IntArray) {
        val lastAppsString = getLastApps(widgetId)
        val lastApps = lastAppsString!!.split("\\|".toRegex())
        views.setOnClickPendingIntent(R.id.lastApp1, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP1.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp2, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP2.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp3, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP3.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp4, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP4.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp5, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP5.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp6, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP6.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp7, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP7.name, ids))
        views.setOnClickPendingIntent(R.id.lastApp8, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP8.name, ids))
        var hideLabel = false
        for (i in 0 until Prefs.LAST_APPS_MAX_SIZE) {
            val id = context.resources.getIdentifier("lastApp" + (i + 1), "id", context.packageName)
            if (i < lastApps.size && lastApps[i].isNotBlank()) {
                hideLabel = true
                val iconBitmap = Cache.instance?.getBitmapFromMemCache(lastApps[i], pm)
                views.setImageViewBitmap(id, iconBitmap)
            } else {
                views.setImageViewBitmap(id, null)
            }
        }
        if (hideLabel) views.setViewVisibility(R.id.lastAppsLabel, View.GONE)
    }

    override fun setupRemoveAllButton(views: RemoteViews, context: Context, ids: IntArray) {
        views.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name, ids))
    }

    abstract override fun switchSelectorStatus(views: RemoteViews)

    companion object {
        private const val TAG = "UninstallWidgetM"
    }
}