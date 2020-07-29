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

import pl.coddev.applu.d.PInfo;
import pl.coddev.applu.enums.AppSelectorStatus;

/**
 * Created by Piotr Woszczek on 19/06/15.
 */
public final class PInfoHandler {
    private static final String TAG = "PInfoHandler";
    private static Map<Integer, Integer> appIndexMap = new HashMap<>();
    private static Map<Integer, ArrayList<PInfo>> filteredPInfosMap = new HashMap<>();
    private static Map<Integer, ArrayList<PInfo>> selectedPInfosMap = new HashMap<>();
    private static CopyOnWriteArrayList<PInfo> allPInfos = new CopyOnWriteArrayList<>();


    public static void setSelectedPInfosMap(int widgetId) {
        selectedPInfosMap.put(widgetId, new ArrayList<>());
    }

    public static void setFilteredPInfosMap(int widgetId) {
        filteredPInfosMap.put(widgetId, new ArrayList<>());
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

    public static boolean selectedPInfosNotExist(int widgetId) {
        return !selectedPInfosMap.containsKey(widgetId);
    }

    public static boolean filteredPInfosExists(int widgetId) {
        return filteredPInfosMap.containsKey(widgetId);
    }

    public static boolean PInfosNotExist() {
        return allPInfos == null || allPInfos.size() == 0;
    }

    public static Integer getAppIndex(int widgetId) {
        return appIndexMap.get(widgetId);
    }

    public static void setAppIndex(int widgetId, int appIndex) {
        PInfoHandler.appIndexMap.put(widgetId, appIndex);
    }

    public static void incrementAppIndex(int widgetId, int by) {
        Integer appIndex = appIndexMap.get(widgetId);
        if (appIndex != null) {
            appIndexMap.put(widgetId, appIndex + by);
        }
    }

    public static void rollIndex(int widgetId) {
        Integer appIndex = appIndexMap.get(widgetId);
        ArrayList<PInfo> filteredPInfos = filteredPInfosMap.get(widgetId);
        if (appIndex != null) {
            if (filteredPInfos == null || appIndex >= filteredPInfos.size()) {
                appIndexMap.put(widgetId, 0);
            }
            if (appIndex < 0 && filteredPInfos != null)
                appIndexMap.put(widgetId, filteredPInfos.size() - 1);
        }
    }

    public static PInfo getCurrentPInfo(int widgetId) {
        Integer appIndex = appIndexMap.get(widgetId);
        ArrayList<PInfo> filteredPInfos = filteredPInfosMap.get(widgetId);
        if (appIndex != null && filteredPInfos != null) {
            return filteredPInfos.get(appIndex);
        } else {
            return null;
        }
    }

    public static PInfo getPInfoFromSelected(int widgetId, int index) {
        ArrayList<PInfo> selectedPInfos = selectedPInfosMap.get(widgetId);
        if (selectedPInfos != null) {
            return selectedPInfos.get(index);
        } else {
            return null;
        }
    }

    public static int sizeOfFiltered(int widgetId) {
        ArrayList<PInfo> filteredPInfos = filteredPInfosMap.get(widgetId);
        if (filteredPInfos != null) {
            return filteredPInfos.size();
        } else {
            return 0;
        }
    }

    public static int sizeOfSelected(int widgetId) {
        ArrayList<PInfo> selectedPInfos = selectedPInfosMap.get(widgetId);
        if (selectedPInfos != null) {
            return selectedPInfos.size();
        } else {
            return 0;
        }
    }

    public static int sizeOfAll() {
        return allPInfos.size();
    }


    public static void addToFiltered(int widgetId, PInfo pInfo) {
        if (!filteredPInfosExists(widgetId)) {
            setFilteredPInfosMap(widgetId);
        }
        ArrayList<PInfo> filteredPInfos = filteredPInfosMap.get(widgetId);
        if (filteredPInfos != null && !filteredPInfos.contains(pInfo))
            filteredPInfos.add(pInfo);
    }

    public static void addToSelected(int widgetId, PInfo pInfo) {
        if (selectedPInfosNotExist(widgetId)) {
            setSelectedPInfosMap(widgetId);
        }
        ArrayList<PInfo> selectedPInfos = selectedPInfosMap.get(widgetId);
        if (selectedPInfos != null && !selectedPInfos.contains(pInfo)) {
            selectedPInfos.add(pInfo);
        }
    }

    public static void addToAll(PInfo pInfo) {
        if (PInfosNotExist()) {
            setAllPInfos(null);
        }
        if (!allPInfos.contains(pInfo)) {
            allPInfos.add(pInfo);
        }
    }


    public static void removeFromSelected(String packageName) {
        for (Map.Entry<Integer, ArrayList<PInfo>> entry : selectedPInfosMap.entrySet())
            if (entry.getValue() != null) {

                Iterator<PInfo> iter = entry.getValue().iterator();
                while (iter.hasNext()) {
                    if (iter.next().getPname().equals(packageName))
                        iter.remove();
                }
            }
    }

    public static void removeFromAll(String packageName) {
        for(PInfo info : getAllPInfos()) {
            if(info.getPname().equals(packageName))
                getAllPInfos().remove(info);
        }
    }

    public static void sortFilteredByMatch(int widgetId) {
        ArrayList<PInfo> filteredPInfos = filteredPInfosMap.get(widgetId);
        if (filteredPInfos != null) {
            Collections.sort(filteredPInfos, new PInfoComparator());
        }
    }

    public static boolean isSystemPackage(PackageInfo pi) {
        return (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    public static boolean fallsIntoSelector(PackageInfo pi, AppSelectorStatus selector, Context context) {

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

    public static boolean fallsIntoSelector(PInfo pi, AppSelectorStatus selector) {

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
