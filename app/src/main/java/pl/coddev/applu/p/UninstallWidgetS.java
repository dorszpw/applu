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
abstract public class UninstallWidgetS extends UninstallWidget {
    private static final String TAG = "UninstallWidgetS";

    void setupLastAppsButtons(RemoteViews views, Context context, int[] ids){};
    void handleLastApps(String newPackage, int widgetId){};
    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids){};
    abstract void switchSelectorStatus(RemoteViews views);
    abstract RemoteViews getRemoteViews(Context context);
}