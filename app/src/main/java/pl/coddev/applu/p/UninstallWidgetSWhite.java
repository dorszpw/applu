package pl.coddev.applu.p;

import android.content.Context;
import android.widget.RemoteViews;

import pl.coddev.applu.R;


/**
 * Created by pw on 16/03/15.
 */
public class UninstallWidgetSWhite extends UninstallWidget {
    private static final String TAG = "UninstallWidgetSWhite";

    @Override
    void switchSelectorStatus(RemoteViews views) {

        switch (this.appSelectorStatus) {
            case ALL:
                views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_white);
                break;
            case USER:
                views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_user_theme_white);
                break;
            case SYSTEM:
                views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_system_theme_white);
                break;
        }
    }

    @Override
    RemoteViews getRemoteViews(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.widget_s_layout_theme_white);
    }

}
