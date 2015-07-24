package pl.coddev.applu.p;

import android.content.Context;
import android.widget.RemoteViews;

import pl.coddev.applu.R;


/**
 * Created by pw on 16/03/15.
 */
public class UninstallWidgetLBlack extends UninstallWidgetM {
    private static final String TAG = "UninstallWidgetMBlack";

    @Override
    void switchSelectorStatus(RemoteViews views) {
        switch (this.appSelectorStatus) {
            case ALL:
                views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_all_theme_black);
                break;
            case USER:
                views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_user_theme_black);
                break;
            case SYSTEM:
                views.setImageViewResource(R.id.appSelectorButton, R.drawable.filter_system_theme_black);
                break;
        }

    }

    @Override
    RemoteViews getRemoteViews(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.widget_l_layout_theme_black);
    }

}