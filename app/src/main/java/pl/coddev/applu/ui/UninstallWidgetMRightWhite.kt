package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R

/**
 * Created by pw on 16/03/15.
 */
class UninstallWidgetMRightWhite : UninstallWidgetM() {


    override fun getRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_m_layout_right_theme_white)
    }

    companion object {
        private const val TAG = "UninstallWidgetMWhite"
    }
}