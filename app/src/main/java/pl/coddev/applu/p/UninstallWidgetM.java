package pl.coddev.applu.p;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Arrays;
import java.util.LinkedList;

import pl.coddev.applu.R;
import pl.coddev.applu.b.Cache;
import pl.coddev.applu.c.Constants;
import pl.coddev.applu.c.Log;

/**
 * Created by Piotr Woszczek on 23/07/15.
 */
abstract public class UninstallWidgetM extends UninstallWidget {
    private static final String TAG = "UninstallWidgetM";


    @Override
    abstract RemoteViews getRemoteViews(Context context);

    @Override
    String getLastApp(String action, int widgetId) {

        String lastApp = "";
        int buttonNumber = Integer.parseInt(action.substring(action.length() - 1));
        // get from "display" list
        String[] lastApps = prefs.getString(Constants.LAST_APPS, "").split("\\|");
        if (lastApps != null && lastApps.length >= buttonNumber) {
            lastApp = lastApps[buttonNumber - 1];
        }

        return lastApp;
    }

    @Override
    void setupLastAppsButtons(RemoteViews views, Context context, int[] ids) {


        String lastAppsString = prefs.getString(Constants.LAST_APPS_SYNC, "");
        String[] lastApps = lastAppsString.split("\\|");
        views.setOnClickPendingIntent(R.id.lastApp1, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP1.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp2, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP2.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp3, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP3.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp4, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP4.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp5, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP5.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp6, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP6.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp7, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP7.name(), ids));
        views.setOnClickPendingIntent(R.id.lastApp8, buildPendingIntent(context, WidgetActions.BUTTON_LASTAPP8.name(), ids));
        boolean hideLabel = false;
        if (lastApps != null) {
            WidgetActions actions = WidgetActions.ADDED_NEW_APP;
            for (int i = 0; i < lastApps.length; i++) {
                if (lastApps[i] != "") {
                    int id = context.getResources().getIdentifier("lastApp" + (i + 1), "id", context.getPackageName());
                    

                    hideLabel = true;
                    Bitmap iconBitmap = Cache.getInstance().getBitmapFromMemCache(lastApps[i], this.pm);
                    views.setImageViewBitmap(id, iconBitmap);
                }
            }
        }
        if (hideLabel)
            views.setViewVisibility(R.id.lastAppsLabel, View.GONE);

        // this is a "display" list that tracks the displayed last apps
        prefs.edit().putString(Constants.LAST_APPS, lastAppsString).commit();
    }

    @Override
    void handleLastApps(String newPackage, int widgetId) {
        LinkedList<String> lastAppsList = new LinkedList<>();
        // this is a sync list, to track last launched apps
        String[] lastApps = prefs.getString(Constants.LAST_APPS_SYNC, "").split("\\|");

        lastAppsList.addAll(Arrays.asList(lastApps));

        if (lastAppsList.size() > 6)
            lastAppsList.removeLast();
        if (lastAppsList.contains(newPackage))
            lastAppsList.remove(newPackage);
        lastAppsList.addFirst(newPackage);

        String lastAppsString = "";
        for (String app : lastAppsList
                ) {
            lastAppsString += (app + "|");
        }
        Log.d(TAG, "handleLastApps " + lastAppsString);
        prefs.edit().putString(Constants.LAST_APPS_SYNC, lastAppsString).commit();
    }

//    @Override
//    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids){};

    @Override
    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids) {
        views.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name(), ids));
    }

    @Override
    abstract void switchSelectorStatus(RemoteViews views);
}
