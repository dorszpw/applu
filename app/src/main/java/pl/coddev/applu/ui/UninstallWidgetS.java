package pl.coddev.applu.ui;

import android.content.Context;
import android.widget.RemoteViews;

import pl.coddev.applu.R;

/**
 * Created by Piotr Woszczek on 23/07/15.
 */
abstract public class UninstallWidgetS extends UninstallWidget {
    private static final String TAG = "UninstallWidgetS";

    void setupLastAppsButtons(int widgetId, RemoteViews views, Context context, int[] ids) {
    }

    ;

    void handleLastApps(String newPackage, int widgetId) {
    }

    ;

    //    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids){};
    @Override
    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids) {
        views.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name(), ids));
    }

    abstract void switchSelectorStatus(RemoteViews views);

    abstract RemoteViews getRemoteViews(Context context);


}
