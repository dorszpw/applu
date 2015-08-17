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
public class UninstallWidgetL extends UninstallWidgetM {
    private static final String TAG = "UninstallWidgetL";


    @Override
    void setupRemoveAllButton(RemoteViews views, Context context, int[] ids) {
        super.setupRemoveAllButton(views, context, ids);
        views.setOnClickPendingIntent(R.id.clearAllButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR_ALL.name(), ids));
    }

}
