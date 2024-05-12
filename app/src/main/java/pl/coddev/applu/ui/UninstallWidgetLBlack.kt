package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R

/**
 * Created by pw on 16/03/15.
 */
class UninstallWidgetLBlack : UninstallWidgetL() {
    override fun getRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_l_layout_right_theme_black)
    }

    companion object {
        private const val TAG = "UninstallWidgetLBlack"
    }
}