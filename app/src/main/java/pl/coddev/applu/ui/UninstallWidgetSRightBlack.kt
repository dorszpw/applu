package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R

/**
 * Created by pw on 16/03/15.
 */
class UninstallWidgetSRightBlack : UninstallWidgetS() {

    override fun getRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_s_layout_right_theme_black)
    }

    companion object {
        private const val TAG = "UninstallWidgetSBlack"
    }
}