package pl.coddev.applu.b;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import pl.coddev.applu.c.Constants;
import pl.coddev.applu.c.Log;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.p.UninstallWidget;

public class PackageModifiedReceiver extends BroadcastReceiver {
    static final String TAG = "PackageRemovedReceiver";
    private PackageManager pm;
    private boolean listRefreshed = false;

    public PackageModifiedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Fabric.with(context, new Crashlytics());
        Uri data = intent.getData();

        this.pm = context.getPackageManager();
        String packageName = data.getEncodedSchemeSpecificPart();
        if (packageName == null)
            packageName = "no package";
        String action = intent.getAction();

        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

        // refreshing application lists
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        List<AppWidgetProviderInfo> awpi = appWidgetManager.getInstalledProviders();

        if (!replacing && action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            Log.d(TAG, "Package removed: " + packageName);
            PInfoHandler.removeFromAllAll(packageName);
        } else if (replacing && action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            Log.d(TAG, "Package replaced: " + packageName);
        } else {
            Log.d(TAG, "Package added: " + packageName);
        }

        PackageInfo pi = null;
        PInfo newInfo = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
            newInfo = new PInfo();
            newInfo.setAppname(pi.applicationInfo.loadLabel(this.pm).toString());
            newInfo.setPname(pi.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < awpi.size(); i++) {

            String widgetPackage = awpi.get(i).provider.getPackageName();
            if (widgetPackage.equals(context.getPackageName())) {

                int[] appWidgetIds = new int[0];

                try {

                    appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                            Class.forName(awpi.get(i).provider.getClassName())));
                    Log.d(TAG, "appWIdgetIds length: " + appWidgetIds.length);

                    for (int j = 0; j < appWidgetIds.length; j++) {

                        SharedPreferences prefs = context.getSharedPreferences(
                                Constants.PREF_FILE + appWidgetIds[j], Context.MODE_PRIVATE);
                        UninstallWidget.AppSelectorStatus selector = UninstallWidget.AppSelectorStatus.valueOf(
                                prefs.getString(Constants.APP_SELECTOR_STATUS,
                                        UninstallWidget.AppSelectorStatus.USER.name()));

                        // add to ALL lists, if falls into selector
                        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                            if (PInfoHandler.fallsIntoSelector(pi, selector, context)) {
                                PInfoHandler.addToAll(appWidgetIds[j], newInfo);
                            }
                        }

                        if (!PInfoHandler.allPInfosExists(appWidgetIds[j]) || !PInfoHandler.filteredPInfosExists(appWidgetIds[j])) {
                            UninstallWidget.getInstalledApps(appWidgetIds[j], UninstallWidget.WidgetActions.BUTTON_SELECTOR,
                                    context, selector, this.pm);
                        }
                    }

                    // update widget by reflection
                    Class[] cArg = new Class[3];
                    cArg[0] = Context.class;
                    cArg[1] = AppWidgetManager.class;
                    cArg[2] = int[].class;
                    Class widgetClass = Class.forName(awpi.get(i).provider.getClassName());
                    Method onUpdate = widgetClass.getMethod("onUpdate", cArg);
                    Method setAction = widgetClass.getMethod("setAction", new Class[]{UninstallWidget.WidgetActions.class});
                    Object widget = widgetClass.newInstance();
                    setAction.invoke(widget, UninstallWidget.WidgetActions.ADDED_NEW_APP);
                    onUpdate.invoke(widget, context, appWidgetManager, appWidgetIds);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
