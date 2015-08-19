package pl.coddev.applu.p;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;
import pl.coddev.applu.MyApplication;
import pl.coddev.applu.R;
import pl.coddev.applu.b.Cache;
import pl.coddev.applu.b.PInfoHandler;
import pl.coddev.applu.c.Constants;
import pl.coddev.applu.c.Log;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.i.DataService;


/**
 * Created by pw on 16/03/15.
 */
abstract public class UninstallWidget extends AppWidgetProvider {
    private static final String TAG = "UninstallWidget";
    private static int widgetId = 0;
    protected AppSelectorStatus appSelectorStatus = AppSelectorStatus.USER;
    static SharedPreferences prefs = null;
    private MyApplication mApp;
    private DataService mDataService;

    public enum WidgetActions {
        TEXTFIELD_BUTTON, BUTTON1, BUTTON2, BUTTON3, BUTTON4, BUTTON5, BUTTON6, BUTTON7, BUTTON8,
        BUTTON_CLEAR, BUTTON_CLEAR_ALL, BUTTON_UNINSTALL, BUTTON_LAUNCH, OTHER, BUTTON_SELECTOR, ADDED_NEW_APP,
        BUTTON_LASTAPP1, BUTTON_LASTAPP2, BUTTON_LASTAPP3, BUTTON_LASTAPP4, BUTTON_LASTAPP5,
        BUTTON_LASTAPP6, BUTTON_LASTAPP7, BUTTON_LASTAPP8, NO_ACTION;

        public WidgetActions next() {
            int ordinal = this.ordinal() + 1 >= values().length
                    ? 0
                    : this.ordinal() + 1;
            return values()[ordinal];
        }
    }

    public enum AppSelectorStatus {
        ALL, SYSTEM, USER;

        public AppSelectorStatus next() {
            int ordinal = this.ordinal() + 1 >= values().length
                    ? 0
                    : this.ordinal() + 1;
            return values()[ordinal];
        }
    }

    private WidgetActions action;
    PackageManager pm;

    public void setAction(WidgetActions action) {
        this.action = action;
    }


    abstract void setupLastAppsButtons(RemoteViews views, Context context, int[] ids);
    abstract void handleLastApps(String newPackage, int widgetId);
    abstract void setupRemoveAllButton(RemoteViews views, Context context, int[] ids);
    abstract void switchSelectorStatus(RemoteViews views);
    abstract RemoteViews getRemoteViews(Context context);

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d(TAG, "onUpdate" + appWidgetIds.length);
        this.pm = context.getPackageManager();


        for (int i = 0; i < appWidgetIds.length; i++) {
            Log.d(TAG, "widget no " + i + ": " + appWidgetIds[i]);

            widgetId = appWidgetIds[i];
            int[] ids = {widgetId};
            prefs = context.getSharedPreferences(Constants.PREF_FILE + widgetId, Context.MODE_PRIVATE);
            appSelectorStatus = AppSelectorStatus.valueOf(prefs.getString(Constants.APP_SELECTOR_STATUS,
                    AppSelectorStatus.USER.name()));

            RemoteViews views = null;
            // Get the layout for the App Widget and attach an on-click listener
            views = getRemoteViews(context);

            views.setOnClickPendingIntent(R.id.searchText, buildPendingIntent(context, WidgetActions.TEXTFIELD_BUTTON.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton1, buildPendingIntent(context, WidgetActions.BUTTON1.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton2, buildPendingIntent(context, WidgetActions.BUTTON2.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton3, buildPendingIntent(context, WidgetActions.BUTTON3.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton4, buildPendingIntent(context, WidgetActions.BUTTON4.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton5, buildPendingIntent(context, WidgetActions.BUTTON5.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton6, buildPendingIntent(context, WidgetActions.BUTTON6.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton7, buildPendingIntent(context, WidgetActions.BUTTON7.name(), ids));
            views.setOnClickPendingIntent(R.id.searchButton8, buildPendingIntent(context, WidgetActions.BUTTON8.name(), ids));
            views.setOnClickPendingIntent(R.id.clearButton, buildPendingIntent(context, WidgetActions.BUTTON_CLEAR.name(), ids));
            views.setOnClickPendingIntent(R.id.appSelectorButton, buildPendingIntent(context, WidgetActions.BUTTON_SELECTOR.name(), ids));
            setupLastAppsButtons(views, context, ids);
            setupRemoveAllButton(views, context, ids);
            switchSelectorStatus(views);


            PInfoHandler.setAppIndex(widgetId, prefs.getInt(Constants.APP_INDEX, 0));
            getInstalledApps(widgetId, action, context, appSelectorStatus, this.pm);
            PInfoHandler.rollIndex(widgetId);

            String packageName = "";
            String showMore = "";


            if (PInfoHandler.sizeOfFiltered(widgetId) > 0) {

                PInfo pinfo = PInfoHandler.getCurrentPInfo(widgetId);
                String appName = pinfo.getAppname();
                packageName = pinfo.getPname();

                views.setOnClickPendingIntent(R.id.uninstallButton, buildPendingIntentForActionButtons(context, packageName, WidgetActions.BUTTON_UNINSTALL.name(), ids));
                views.setOnClickPendingIntent(R.id.launchButton, buildPendingIntentForActionButtons(context, packageName, WidgetActions.BUTTON_LAUNCH.name(), ids));

                Bitmap iconBitmap = Cache.getInstance().getBitmapFromMemCache(pinfo.getPname(), this.pm);
                views.setImageViewBitmap(R.id.launchButton, iconBitmap);
                views.setTextViewText(R.id.searchText, getSpannableForField(context, pinfo));

            } else {
                views.setTextViewText(R.id.searchText, context.getString(R.string.no_matches));
                views.setImageViewResource(R.id.launchButton, R.drawable.search_problem_128);
            }

            boolean appindexSaved = prefs.edit().putInt(Constants.APP_INDEX, PInfoHandler.getAppIndex(widgetId)).commit();
            boolean packageSaved = prefs.edit().putString(Constants.CURRENT_APP, packageName).commit();
            Log.d(TAG, "onUpdate prefs, class:  " + this.getClass().getName() + ", saved: " + appindexSaved + "/" + packageSaved);
            views.setViewVisibility(R.id.progressBar, View.GONE);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }


    private CharSequence getSpannableForField(Context context, PInfo pInfo) {
        SpannableString spannableShowMore = new SpannableString(""),
                spannableAppName = new SpannableString("");
        String showMore = "";

        if (PInfoHandler.sizeOfFiltered(widgetId) > 1) {
            StringBuilder sb = new StringBuilder(7);
            sb.append(Constants.SHOW_MORE_STRING)
                    .append(PInfoHandler.getAppIndex(widgetId) + 1)
                    .append("/")
                    .append(PInfoHandler.sizeOfFiltered(widgetId))
                    .append(" ");
            showMore = sb.toString();
        } else if (PInfoHandler.sizeOfFiltered(widgetId) == 1) {
        }

        if (!pInfo.isRemoved()) {
            spannableShowMore = new SpannableString(showMore);
            spannableShowMore.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue_nasty)), 0, showMore.length(), 0);
            spannableAppName = new SpannableString(pInfo.getAppname());
            spannableAppName.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue_nasty)),
                    pInfo.getMatch(), pInfo.getMatch() + pInfo.getMatcherGroup().length(), 0);
        } else {
            spannableShowMore = new SpannableString(showMore);
            spannableShowMore.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue_nasty)), 0, showMore.length(), 0);
            spannableAppName = new SpannableString(pInfo.getAppname());
            spannableAppName.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, pInfo.getAppname().length(), 0);
            spannableAppName.setSpan(new StrikethroughSpan(), 0, pInfo.getAppname().length(), 0);
        }

        return TextUtils.concat(spannableShowMore, spannableAppName);
    }

    PendingIntent buildPendingIntent(Context context, String actionName, int ids[]) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(actionName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    // separate method to add packageName, just to be sure that what user uninstalls is right
    PendingIntent buildPendingIntentForActionButtons(Context context, String packageName, String actionName, int ids[]) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(actionName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(Constants.CURRENT_APP, packageName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    public static void getInstalledApps(int widgetId, WidgetActions button, Context context,
                                        AppSelectorStatus appSelectorStatus, PackageManager pm) {

        long start = Calendar.getInstance().getTimeInMillis();
        Log.d(TAG, "getInstalledApps xstart " + start);
        Pattern ptn;
        Matcher matcher;
        prefs = context.getSharedPreferences(Constants.PREF_FILE + widgetId, Context.MODE_PRIVATE);

        String filter = prefs.getString(Constants.PREFS_FILTER_LIST, "");

        Log.d(TAG, "getInstalledApps, last filter: " + filter);
        String commonChars = "[^a-zA-Z]*";
        String filterExpansion = "";
        switch (button) {
            case BUTTON1:
                filterExpansion = "[abc]" + commonChars;
                break;
            case BUTTON2:
                filterExpansion = "[def]" + commonChars;
                break;
            case BUTTON3:
                filterExpansion = "[ghi]" + commonChars;
                break;
            case BUTTON4:
                filterExpansion = "[jkl]" + commonChars;
                break;
            case BUTTON5:
                filterExpansion = "[mno]" + commonChars;
                break;
            case BUTTON6:
                filterExpansion = "[pqrs]" + commonChars;
                break;
            case BUTTON7:
                filterExpansion = "[tuv]" + commonChars;
                break;
            case BUTTON8:
                filterExpansion = "[wxyz]" + commonChars;
                break;
            case BUTTON_CLEAR:
                filter = filter.replaceFirst("\\[\\w+\\]\\[\\^a\\-z" +
                        "A\\-Z\\]\\*$", "");
                break;
            case BUTTON_CLEAR_ALL:
                filter = "";
            default:
                break;
        }
        if ((PInfoHandler.filteredPInfosExists(widgetId) &&
                PInfoHandler.sizeOfFiltered(widgetId) > 0) || !PInfoHandler.allPInfosExists(widgetId)) {
            filter += filterExpansion;
        }
        Log.d(TAG, "getInstalledApps, new filter: " + filter);

        if (!button.equals(WidgetActions.TEXTFIELD_BUTTON) || !PInfoHandler.filteredPInfosExists(widgetId)) {

            switch (button) {
                case TEXTFIELD_BUTTON:
                    PInfoHandler.incrementAppIndex(widgetId, 1);
                    break;
                case ADDED_NEW_APP:
                    break;
                default:
                    PInfoHandler.setAppIndex(widgetId, 0);
            }
            prefs.edit().putString(Constants.PREFS_FILTER_LIST, filter).commit();

            ptn = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

            if (!PInfoHandler.allPInfosExists(widgetId) || button.equals(WidgetActions.BUTTON_SELECTOR)) {
                ArrayList<PackageInfo> packs = (ArrayList) pm.getInstalledPackages(0);
                PInfoHandler.setAllPInfos(widgetId);

                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo pi = packs.get(i);

                    if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus, context))
                        continue;

                    PInfo newInfo = new PInfo();
                    newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                    //newInfo.setAppname("applu");
                    newInfo.setPname(pi.packageName);
                    PInfoHandler.addToAll(widgetId, newInfo);
                }
                Log.d(TAG, "Loaded new ALL list. Size: " + PInfoHandler.sizeOfAll(widgetId));
            } else {
                Log.d(TAG, "Using cached ALL list. Size: " + PInfoHandler.sizeOfAll(widgetId));
            }

            PInfoHandler.setFilteredPInfos(widgetId);
            //PInfoHandler.clearFiltered(widgetId);
            for (int i = 0; i < PInfoHandler.sizeOfAll(widgetId); i++) {
                PInfo newInfo = PInfoHandler.getPInfoFromAll(widgetId, i);
                matcher = ptn.matcher(newInfo.getAppname());
                if (matcher.find()) {
                    newInfo.setMatch(matcher.start());
                    newInfo.setMatcherGroup(matcher.group());
                    PInfoHandler.addToFiltered(widgetId, newInfo);
                }
            }

            PInfoHandler.sortFilteredByMatch(widgetId);
        } else {
            PInfoHandler.incrementAppIndex(widgetId, 1);
        }

        Log.d(TAG, "getInstalledApps xend " + Calendar.getInstance().getTimeInMillis() + ", " + (Calendar.getInstance().getTimeInMillis() - start));
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Fabric.with(context, new Crashlytics());
        Log.d(TAG, "---onReceive received");

//        this.mApp = (MyApplication) context.getApplicationContext();
//        this.mDataService = this.mApp.mDataService;

        int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        if (appWidgetIds != null) {
            int widgetId = appWidgetIds[0];
            prefs = context.getSharedPreferences(Constants.PREF_FILE + widgetId, Context.MODE_PRIVATE);

            //String currentApp = prefs.getString(Constants.CURRENT_APP, "");
            String currentApp;
            //Log.d(TAG, "onUpdate prefs, class:  " + this.getClass().getName() + ", saved: " + currentApp);

            appSelectorStatus = AppSelectorStatus.valueOf(prefs.getString(Constants.APP_SELECTOR_STATUS,
                    AppSelectorStatus.USER.name()));

            String actionString = intent.getAction();
            Log.d(TAG, "Intent action: " + actionString);
            try {
                action = WidgetActions.valueOf(actionString);
            } catch (IllegalArgumentException e) {
                action = WidgetActions.OTHER;
            }

            if (action.equals(WidgetActions.BUTTON_UNINSTALL)) {
                currentApp = intent.getStringExtra(Constants.CURRENT_APP);
                if (currentApp != "") {
                    Uri packageUri = Uri.parse("package:" + currentApp);
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_DELETE, packageUri);
                    uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    uninstallIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", true);
                    context.startActivity(uninstallIntent);
                }
            } else if (action.equals(WidgetActions.BUTTON_LAUNCH)) {
                currentApp = intent.getStringExtra(Constants.CURRENT_APP);
                if (currentApp != "") {
                    try {
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(currentApp);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        handleLastApps(currentApp, widgetId);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (actionString.contains("LASTAPP")) {

                currentApp = getLastApp(actionString, widgetId);
                try {

                    if (currentApp != null && currentApp != "") {
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(currentApp);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        handleLastApps(currentApp, widgetId);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show();
                }

            } else if (actionString.contains("BUTTON")) {
                if (action.equals((WidgetActions.BUTTON_SELECTOR))) {
                    appSelectorStatus = appSelectorStatus.next();
                    prefs.edit().putString(Constants.APP_SELECTOR_STATUS, appSelectorStatus.name()).commit();
                    Log.d(TAG, "Widget/selector: " + widgetId + "/" + appSelectorStatus.name());
                }
                RemoteViews views = getRemoteViews(context);
                //views.setProgressBar(R.id.progressBar, 0, 0, true);
                views.setViewVisibility(R.id.progressBar, View.VISIBLE);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                // every widget is separate!!!

                appWidgetManager.partiallyUpdateAppWidget(widgetId, views);
                onUpdate(context, appWidgetManager, appWidgetIds);


            } else {
                super.onReceive(context, intent);
            }
        } else {
            super.onReceive(context, intent);
        }

    }


    String getLastApp(String action, int widgetId) {
        return null;
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted");

        int widgetID = appWidgetIds[0];

    }

}
