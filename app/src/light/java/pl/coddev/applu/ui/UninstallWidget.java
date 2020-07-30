package pl.coddev.applu.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.coddev.applu.R;
import pl.coddev.applu.data.PInfo;
import pl.coddev.applu.enums.AppSelectorStatus;
import pl.coddev.applu.service.PInfoHandler;
import pl.coddev.applu.utils.Constants;
import pl.coddev.applu.utils.Log;
import pl.coddev.applu.utils.Prefs;
import pl.coddev.applu.utils.Utils;

/**
 * Created by pw on 16/03/15.
 */
abstract public class UninstallWidget extends AppWidgetProvider {
    private static final String TAG = "UninstallWidget";
    private static int widgetId = 0;
    protected AppSelectorStatus appSelectorStatus = AppSelectorStatus.USER;

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

    private WidgetActions action;
    PackageManager pm;

    public void setAction(WidgetActions action) {
        this.action = action;
    }

    abstract void setupLastAppsButtons(int widgetId, RemoteViews views, Context context, int[] ids);

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

            appSelectorStatus = Prefs.getAppSelectorStatus(widgetId);

            RemoteViews views;
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
            setupLastAppsButtons(widgetId, views, context, ids);
            setupRemoveAllButton(views, context, ids);
            switchSelectorStatus(views);


            PInfoHandler.setAppIndex(widgetId, Prefs.getAppIndex(widgetId));
            getInstalledApps(widgetId, action, context, appSelectorStatus, this.pm);
            PInfoHandler.rollIndex(widgetId);

            String packageName = "";

            if (PInfoHandler.sizeOfFiltered(widgetId) > 0) {
                PInfo pinfo = PInfoHandler.getCurrentPInfo(widgetId);
                if (pinfo != null) {
                    packageName = pinfo.getPname();

                    views.setOnClickPendingIntent(R.id.uninstallButton,
                            buildPendingIntentForActionButtons(context, packageName,
                                    WidgetActions.BUTTON_UNINSTALL.name(), ids));
                    views.setOnClickPendingIntent(R.id.launchButton,
                            buildPendingIntentForActionButtons(context, packageName,
                                    WidgetActions.BUTTON_LAUNCH.name(), ids));

                    Log.d(TAG, "App package name: " + pinfo.getPname());
                    Bitmap iconBitmap = Cache.getInstance().getBitmapFromMemCache(pinfo.getPname(), this.pm);
                    views.setImageViewBitmap(R.id.launchButton, iconBitmap);
                    views.setTextViewText(R.id.searchText, getSpannableForField(context, pinfo));
                }
            } else {
                views.setTextViewText(R.id.searchText, context.getString(R.string.no_matches));
                views.setImageViewResource(R.id.launchButton, R.drawable.search_problem_128);
                views.setOnClickPendingIntent(R.id.uninstallButton,
                        buildPendingIntentForActionButtons(context, null,
                                WidgetActions.NO_ACTION.name(), ids));
                views.setOnClickPendingIntent(R.id.launchButton,
                        buildPendingIntentForActionButtons(context, null,
                                WidgetActions.NO_ACTION.name(), ids));
            }

            Prefs.setAppIndex(PInfoHandler.getAppIndex(widgetId), widgetId);
            Prefs.setCurrentApp(packageName, widgetId);
            Log.d(TAG, "onUpdate prefs, class:  " + this.getClass().getName() +
                    ", saved: " + PInfoHandler.getAppIndex(widgetId) + "/" + packageName);
            views.setViewVisibility(R.id.progressBar, View.GONE);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }


    private CharSequence getSpannableForField(Context context, PInfo pInfo) {
        SpannableString spannableShowMore, spannableAppName;
        String showMore = "";
        Resources res = context.getResources();

        if (PInfoHandler.sizeOfFiltered(widgetId) > 1) {
            @SuppressWarnings("StringBufferReplaceableByString")
            StringBuilder sb = new StringBuilder(7);
            sb.append(Constants.SHOW_MORE_STRING)
                    .append(PInfoHandler.getAppIndex(widgetId) + 1)
                    .append("/")
                    .append(PInfoHandler.sizeOfFiltered(widgetId))
                    .append(" ");
            showMore = sb.toString();
        }

        if (!pInfo.isRemoved()) {
            spannableShowMore = new SpannableString(showMore);
            spannableShowMore.setSpan(new ForegroundColorSpan(res.getColor(R.color.blue_nasty)),
                    0, showMore.length(), 0);
            spannableAppName = new SpannableString(pInfo.getAppname());
            spannableAppName.setSpan(new ForegroundColorSpan(res.getColor(R.color.blue_nasty)),
                    pInfo.getMatch(), pInfo.getMatch() + pInfo.getMatcherGroup().length(), 0);
        } else {
            spannableShowMore = new SpannableString(showMore);
            spannableShowMore.setSpan(new ForegroundColorSpan(res.getColor(R.color.blue_nasty)),
                    0, showMore.length(), 0);
            spannableAppName = new SpannableString(pInfo.getAppname());
            spannableAppName.setSpan(new ForegroundColorSpan(res.getColor(R.color.red)),
                    0, pInfo.getAppname().length(), 0);
            spannableAppName.setSpan(new StrikethroughSpan(), 0, pInfo.getAppname().length(), 0);
        }

        return TextUtils.concat(spannableShowMore, spannableAppName);
    }

    PendingIntent buildPendingIntent(Context context, String actionName, int[] ids) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(actionName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // separate method to add packageName, just to be sure that what user uninstalls is right
    PendingIntent buildPendingIntentForActionButtons(Context context, String packageName,
                                                     String actionName, int[] ids) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(actionName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(Constants.CURRENT_APP, packageName);
        return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static void getInstalledApps(int widgetId, WidgetActions button, Context context,
                                        AppSelectorStatus appSelectorStatus, PackageManager pm) {

        long start = Calendar.getInstance().getTimeInMillis();
        Log.d(TAG, "getInstalledApps xstart " + start);
        Pattern ptn;
        Matcher matcher;

        String filter = Prefs.getFilterList(widgetId);

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
                PInfoHandler.sizeOfFiltered(widgetId) > 0) || PInfoHandler.selectedPInfosNotExist(widgetId)) {
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
            Prefs.setFilterList(filter, widgetId);

            ptn = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

            if (PInfoHandler.selectedPInfosNotExist(widgetId) || button.equals(WidgetActions.BUTTON_SELECTOR)) {
                if (PInfoHandler.PInfosNotExist()) {
                    ArrayList<PackageInfo> packs = (ArrayList<PackageInfo>) pm.getInstalledPackages(0);
                    PInfoHandler.setSelectedPInfosMap(widgetId);

                    for (int i = 0; i < packs.size(); i++) {
                        PackageInfo pi = packs.get(i);

                        if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus, context))
                            continue;

                        PInfo newInfo = new PInfo();
                        newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                        //newInfo.setAppname("applu");
                        newInfo.setPname(pi.packageName);
                        newInfo.setIsSystemPackage(PInfoHandler.isSystemPackage(pi));
                        PInfoHandler.addToSelected(widgetId, newInfo);
                    }
                    Log.d(TAG, "Loaded new ALL list. Size: " + PInfoHandler.sizeOfAll());
                } else {
                    PInfoHandler.setSelectedPInfosMap(widgetId);
                    for (PInfo pi : PInfoHandler.getAllPInfos()) {
                        if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus))
                            continue;
                        PInfoHandler.addToSelected(widgetId, pi);
                    }
                }
            } else {
                Log.d(TAG, "Using cached SELECTED list. Size: " + PInfoHandler.sizeOfSelected(widgetId));
            }


            PInfoHandler.setFilteredPInfosMap(widgetId);
            //PInfoHandler.clearFiltered(widgetId);
            for (int i = 0; i < PInfoHandler.sizeOfSelected(widgetId); i++) {
                PInfo newInfo = PInfoHandler.getPInfoFromSelected(widgetId, i);
                if (newInfo == null) continue;
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

        Log.d(TAG, "getInstalledApps xend " + Calendar.getInstance().getTimeInMillis() + ", " +
                (Calendar.getInstance().getTimeInMillis() - start));
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "---onReceive received");
        int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        if (appWidgetIds != null) {
            int widgetId = appWidgetIds[0];

            String currentApp;
            String actionString = intent.getAction();
            Log.d(TAG, "Intent action: " + actionString);
            try {
                action = WidgetActions.valueOf(actionString);
            } catch (IllegalArgumentException e) {
                action = WidgetActions.OTHER;
            }

            if (action.equals(WidgetActions.BUTTON_UNINSTALL)) {

                currentApp = intent.getStringExtra(Constants.CURRENT_APP);
                if (currentApp != null && !currentApp.equals("")) {
                    Uri packageUri = Uri.parse("package:" + currentApp);
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_DELETE, packageUri);
                    uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    uninstallIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", true);
                    context.startActivity(uninstallIntent);
                }
            } else if (action.equals(WidgetActions.BUTTON_LAUNCH)) {

                currentApp = intent.getStringExtra(Constants.CURRENT_APP);
                if (currentApp != null && !currentApp.equals("")) {
                    try {
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(currentApp);
                        if (i != null) {
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            Prefs.addToLastApps(currentApp, widgetId);
                            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (actionString != null && actionString.contains("LASTAPP")) {

                currentApp = getLastApp(actionString, widgetId);
                try {

                    if (currentApp != null && !currentApp.equals("")) {
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(currentApp);
                        if (i != null) {
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            Prefs.addToLastApps(currentApp, widgetId);
                            onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(context, R.string.cannot_run_app, Toast.LENGTH_LONG).show();
                }

            } else if (actionString != null && actionString.contains("BUTTON")) {
                Utils.displayRateQuestionIfNeeded();
                if (action.equals((WidgetActions.BUTTON_SELECTOR))) {
                    buttonSelectorAction(context);
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

    void buttonSelectorAction(final Context context) {
//            Intent popUpIntent = new Intent(context, RedirectDialog.class);
//            popUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(popUpIntent);
        appSelectorStatus = appSelectorStatus.next();
        Prefs.setAppSelectorStatus(appSelectorStatus, widgetId);
        Log.d(TAG, "Widget/selector: " + widgetId + "/" + appSelectorStatus.name());
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
