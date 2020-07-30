package pl.coddev.applu.broadcastreceiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import pl.coddev.applu.data.PInfo;
import pl.coddev.applu.enums.AppSelectorStatus;
import pl.coddev.applu.service.PInfoHandler;
import pl.coddev.applu.ui.UninstallWidget;
import pl.coddev.applu.utils.Log;
import pl.coddev.applu.utils.Prefs;

public class PackageModifiedReceiver extends BroadcastReceiver {
    static final String TAG = "PackageModifiedReceiver";
    private PackageManager pm;
    private boolean listRefreshed = false;

    public PackageModifiedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        android.util.Log.d(TAG, "onReceive: ");
        Uri data = intent.getData();

        this.pm = context.getPackageManager();
        String packageName = data.getEncodedSchemeSpecificPart();
        if (packageName == null)
            packageName = "no package";
        String action = intent.getAction();

        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        List<AppWidgetProviderInfo> awpi = appWidgetManager.getInstalledProviders();

        PackageInfo pi = null;
        PInfo modifiedInfo = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
            modifiedInfo = new PInfo();
            modifiedInfo.setAppname(pi.applicationInfo.loadLabel(this.pm).toString());
            modifiedInfo.setPname(pi.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        if (!replacing && (Intent.ACTION_PACKAGE_REMOVED.equals(action) ||
                Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action))) {
            Log.d(TAG, "Package removed: " + packageName);
            PInfoHandler.removeFromSelected(packageName);
            PInfoHandler.removeFromAll(packageName);
        } else if (replacing && Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            Log.d(TAG, "Package replaced: " + packageName);
        } else {
            Log.d(TAG, "Package added: " + packageName);
        }

        for (int i = 0; i < awpi.size(); i++) {

            String widgetPackage = awpi.get(i).provider.getPackageName();
            if (widgetPackage.equals(context.getPackageName())) {

                int[] appWidgetIds;

                try {

                    appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                            Class.forName(awpi.get(i).provider.getClassName())));
                    Log.d(TAG, "appWIdgetIds length: " + appWidgetIds.length);

                    for (int appWidgetId : appWidgetIds) {

                        AppSelectorStatus selector = Prefs.get().getAppSelectorStatus(appWidgetId);

                        Log.d(TAG, "onReceive PMR, class: " + Class.forName(awpi.get(i).provider.getClassName()) +
                                " selector: " + selector);

                        // add to ALL lists, if falls into selector
                        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                            assert pi != null;
                            if (PInfoHandler.fallsIntoSelector(pi, selector, context)) {
                                PInfoHandler.addToSelected(appWidgetId, modifiedInfo);
                            }
                            PInfoHandler.addToAll(modifiedInfo);
                        }

                        if (PInfoHandler.selectedPInfosNotExist(appWidgetId) || !PInfoHandler.filteredPInfosExists(appWidgetId)) {
                            UninstallWidget.getInstalledApps(appWidgetId, UninstallWidget.WidgetActions.BUTTON_SELECTOR,
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
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                        IllegalAccessException | InstantiationException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        }
    }
}
