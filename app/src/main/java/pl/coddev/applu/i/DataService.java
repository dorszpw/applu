package pl.coddev.applu.i;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.coddev.applu.b.PInfoHandler;
import pl.coddev.applu.c.Constants;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.p.UninstallWidget;


public class DataService extends Service {
    private static final String TAG = "DataService";


    // DataService
    private final IBinder mBinder = new MyBinder();
    SharedPreferences prefs;


    //==============================================================================================
    // ===== DataService
    //==============================================================================================
    public class MyBinder extends Binder {
        public DataService getService() {
            Log.i(TAG, "getService()");
            return DataService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

    }

    @Override
    public void onCreate() {
        //Log.i(TAG, "onCreate()");
        super.onCreate();
        // TODO: 19/08/15 data
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = getApplicationContext().getPackageManager();
                ArrayList<PackageInfo> packs = (ArrayList) pm.getInstalledPackages(0);
                PInfoHandler.setAllPInfos(1);

                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo pi = packs.get(i);

//            if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus, context))
//                continue;

                    PInfo newInfo = new PInfo();
                    newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                    //newInfo.setAppname("applu");
                    newInfo.setPname(pi.packageName);
                    PInfoHandler.addToAll(1, newInfo);
                }
                Log.d(TAG, "onCreate " + packs.size());
            }
        });


    }

    public void getInstalledApps(int widgetId, UninstallWidget.WidgetActions button, Context context,
                                        UninstallWidget.AppSelectorStatus appSelectorStatus, PackageManager pm) {

        Pattern ptn;
        Matcher matcher;
        prefs = context.getSharedPreferences(Constants.PREF_FILE + widgetId, Context.MODE_PRIVATE);

        String filter = prefs.getString(Constants.PREFS_FILTER_LIST, "");

        pl.coddev.applu.c.Log.d(TAG, "getInstalledApps, last filter: " + filter);
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
        pl.coddev.applu.c.Log.d(TAG, "getInstalledApps, new filter: " + filter);

        if (!button.equals(UninstallWidget.WidgetActions.TEXTFIELD_BUTTON) ||
                !PInfoHandler.filteredPInfosExists(widgetId)) {

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

            if (!PInfoHandler.allPInfosExists(widgetId) || button.equals(UninstallWidget.WidgetActions.BUTTON_SELECTOR)) {
                ArrayList<PackageInfo> packs = (ArrayList) pm.getInstalledPackages(0);
                PInfoHandler.setAllPInfos(widgetId);

                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo pi = packs.get(i);

                    if (!PInfoHandler.fallsIntoSelector(pi, appSelectorStatus, context))
                        continue;

                    PInfo newInfo = new PInfo();
                    newInfo.setAppname(pi.applicationInfo.loadLabel(pm).toString());
                    newInfo.setPname(pi.packageName);
                    PInfoHandler.addToAll(widgetId, newInfo);
                }
                pl.coddev.applu.c.Log.d(TAG, "Loaded new ALL list. Size: " + PInfoHandler.sizeOfAll(widgetId));
            } else {
                pl.coddev.applu.c.Log.d(TAG, "Using cached ALL list. Size: " + PInfoHandler.sizeOfAll(widgetId));
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
    }

}
