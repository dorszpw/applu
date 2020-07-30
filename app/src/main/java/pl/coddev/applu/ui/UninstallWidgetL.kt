package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R

/**
 * Created by Piotr Woszczek on 23/07/15.
 */
abstract class UninstallWidgetL : UninstallWidgetM() {
    override fun setupRemoveAllButton(views: RemoteViews, context: Context, ids: IntArray) {
        views.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name, ids))
    }

    companion object {
        private const val TAG = "UninstallWidgetL"
    }
}