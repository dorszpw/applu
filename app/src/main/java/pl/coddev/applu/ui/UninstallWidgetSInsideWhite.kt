package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R
import pl.coddev.applu.enums.AppSelectorStatus

/**
 * Created by pw on 16/03/15.
 */
class UninstallWidgetSInsideWhite : UninstallWidgetS() {
    override fun switchSelectorStatus(views: RemoteViews) {
        when (appSelectorStatus) {
            AppSelectorStatus.ALL -> views!!.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_white)
            AppSelectorStatus.USER -> views!!.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_user_theme_white)
            AppSelectorStatus.SYSTEM -> views!!.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_system_theme_white)
        }
    }

    override fun getRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context!!.packageName, R.layout.widget_s_layout_inside_theme_white)
    }

    companion object {
        private const val TAG = "WidgetSInsideBlack"
    }
}