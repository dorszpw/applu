package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R
import pl.coddev.applu.enums.AppSelectorStatus

/**
 * Created by pw on 16/03/15.
 */
class UninstallWidgetLBlack : UninstallWidgetL() {
    public override fun switchSelectorStatus(views: RemoteViews) {
        when (appSelectorStatus) {
            AppSelectorStatus.ALL -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_black)
            AppSelectorStatus.USER -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_user_theme_black)
            AppSelectorStatus.SYSTEM -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_system_theme_black)
            else -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_black)
        }
    }

    public override fun getRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_l_layout_theme_black)
    }

    companion object {
        private const val TAG = "UninstallWidgetLBlack"
    }
}