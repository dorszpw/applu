package pl.coddev.applu.ui;

import android.content.Context;
import android.widget.RemoteViews;

import pl.coddev.applu.R;

/**
 * Created by Piotr Woszczek on 23/07/15.
 */
abstract public class UninstallWidgetL extends UninstallWidgetM {
    private static final String TAG = "UninstallWidgetL";
    
    @Override
    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids) {
        views.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name(), ids));
    }
}
