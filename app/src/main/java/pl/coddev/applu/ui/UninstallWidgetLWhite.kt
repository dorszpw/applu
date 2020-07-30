package pl.coddev.applu.ui

import android.content.Context
import android.widget.RemoteViews
import pl.coddev.applu.R
import pl.coddev.applu.enums.AppSelectorStatus

/**
 * Created by pw on 16/03/15.
 */
class UninstallWidgetLWhite : UninstallWidgetL() {
    public override fun switchSelectorStatus(views: RemoteViews) {
        when (appSelectorStatus) {
            AppSelectorStatus.ALL -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_white)
            AppSelectorStatus.USER -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_user_theme_white)
            AppSelectorStatus.SYSTEM -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_system_theme_white)
            else -> views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_white)
        }
    }

    public override fun getRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_l_layout_theme_white)
    }

    companion object {
        private const val TAG = "UninstallWidgetLWhite"
    }
}