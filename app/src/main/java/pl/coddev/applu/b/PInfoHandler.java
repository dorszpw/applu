package pl.coddev.applu.b;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.coddev.applu.c.Log;
import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.p.UninstallWidget;

/**
 * Created by Piotr Woszczek on 19/06/15.
 */
public final class PInfoHandler {
    private static final String TAG = "PInfoHandler";
    private static Map<Integer, Integer> appIndex = new HashMap<>();
    private static Map<Integer, ArrayList<PInfo>> filteredPInfos = new HashMap<>();
    private static Map<Integer, ArrayList<PInfo>> selectedPInfos = new HashMap<>();
    private static CopyOnWriteArrayList<PInfo> allPInfos = new CopyOnWriteArrayList<>();


    public static void setSelectedPInfos(int widgetId) {
        PInfoHandler.selectedPInfos.put(widgetId, new ArrayList<PInfo>());
    }

    public static void setFilteredPInfos(int widgetId) {
        PInfoHandler.filteredPInfos.put(widgetId, new ArrayList<PInfo>());
    }

    public static void setAllPInfos(ArrayList<PInfo> list) {
        if (allPInfos != null) {
            allPInfos.clear();
        } else {
            allPInfos = new CopyOnWriteArrayList<>();
        }
        if (list != null)
            allPInfos.addAll(list);
    }

    public static List<PInfo> getAllPInfos() {
        return allPInfos;
    }

    public static boolean selectedPInfosExists(int widgetId) {
        return selectedPInfos.containsKey(widgetId);
    }

    public static boolean filteredPInfosExists(int widgetId) {
        return filteredPInfos.containsKey(widgetId);
    }

    public static boolean allPInfosExists() {
        return (allPInfos == null || allPInfos.size() == 0) ? false : true;
    }

    public static int getAppIndex(int widgetId) {
        return appIndex.get(widgetId);
    }

    public static void setAppIndex(int widgetId, int appIndex) {
        PInfoHandler.appIndex.put(widgetId, appIndex);
    }

    public static int incrementAppIndex(int widgetId, int by) {
        int newIndex = appIndex.get(widgetId) + by;
        appIndex.put(widgetId, newIndex);
        return newIndex;
    }

    public static void rollIndex(int widgetId) {
        if (appIndex.get(widgetId) >= filteredPInfos.get(widgetId).size()) {
            appIndex.put(widgetId, 0);
        }
        if (appIndex.get(widgetId) < 0)
            appIndex.put(widgetId, filteredPInfos.get(widgetId).size() - 1);
    }

    public static PInfo getCurrentPInfo(int widgetId) {
        return filteredPInfos.get(widgetId).get(appIndex.get(widgetId));
    }

    public static PInfo getPInfoFromSelected(int widgetId, int index) {
        return selectedPInfos.get(widgetId).get(index);
    }

    public static int sizeOfFiltered(int widgetId) {
        return filteredPInfos.get(widgetId).size();
    }

    public static int sizeOfSelected(int widgetId) {
        return selectedPInfos.get(widgetId).size();
    }

    public static int sizeOfAll() {
        return allPInfos.size();
    }


    public static void addToFiltered(int widgetId, PInfo pInfo) {
        if (!filteredPInfosExists(widgetId)) {
            setFilteredPInfos(widgetId);
        }
        if (!filteredPInfos.get(widgetId).contains(pInfo))
            filteredPInfos.get(widgetId).add(pInfo);
    }

    public static void addToSelected(int widgetId, PInfo pInfo) {
        if (!selectedPInfosExists(widgetId)) {
            setSelectedPInfos(widgetId);
        }
        if (!selectedPInfos.get(widgetId).contains(pInfo)) {
            selectedPInfos.get(widgetId).add(pInfo);
        }
    }

    public static void addToAll(PInfo pInfo) {
        if (!allPInfosExists()) {
            setAllPInfos(null);
        }
        if (!allPInfos.contains(pInfo)) {
            allPInfos.add(pInfo);
        }
    }


    public static void removeFromSelected(String packageName) {
        for (Map.Entry<Integer, ArrayList<PInfo>> entry : selectedPInfos.entrySet())
            if (entry.getValue() != null) {

                Iterator<PInfo> iter = entry.getValue().iterator();
                while (iter.hasNext()) {
                    if (iter.next().getPname().equals(packageName))
                        iter.remove();
                }
            }
    }

    public static void removeFromAll(PInfo pinfo) {
        getAllPInfos().remove(pinfo);
    }

    public static void sortFilteredByMatch(int widgetId) {
        Collections.sort(PInfoHandler.filteredPInfos.get(widgetId), new PInfoComparator());
    }

    public static boolean isSystemPackage(PackageInfo pi) {
        return (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    public static boolean fallsIntoSelector(PackageInfo pi, UninstallWidget.AppSelectorStatus selector, Context context) {

        if (context.getPackageManager().getLaunchIntentForPackage(pi.packageName) == null) {
            return false;
        }
        switch (selector) {
            case ALL:
                return true;
            case USER:
                return !isSystemPackage(pi);
            case SYSTEM:
                return isSystemPackage(pi);
            default:
                return false;
        }
    }

    public static boolean fallsIntoSelector(PInfo pi, UninstallWidget.AppSelectorStatus selector) {

        switch (selector) {
            case ALL:
                return true;
            case USER:
                return !pi.isSystemPackage();
            case SYSTEM:
                return pi.isSystemPackage();
            default:
                return false;
        }
    }


    public class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    public static class PInfoComparator implements Comparator<PInfo> {
        @Override
        public int compare(PInfo o1, PInfo o2) {

            int match1 = o1.getMatch(), match2 = o2.getMatch();
            if (match1 != match2) {
                return match1 - match2;
            } else {
                return o1.getAppname().compareToIgnoreCase(o2.getAppname());
            }
        }
    }

}
