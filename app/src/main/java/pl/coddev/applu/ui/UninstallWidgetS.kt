package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R
import pl.coddev.applu.enums.WidgetActions

/**
 * Created by Piotr Woszczek on 23/07/15.
 */
abstract class UninstallWidgetS : UninstallWidget() {
    override fun setupLastAppsButtons(widgetId: Int, views: RemoteViews, context: Context, ids: IntArray) {}
    fun addToLastApps(newPackage: String, widgetId: Int) {}

    //    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids){};
    override fun setupRemoveAllButton(views: RemoteViews, context: Context, ids: IntArray) {
        views!!.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name, ids))
    }

    abstract override fun getRemoteViews(context: Context): RemoteViews

    companion object {
        private const val TAG = "UninstallWidgetS"
    }
}