package pl.coddev.applu.service

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import pl.coddev.applu.data.PInfo
import pl.coddev.applu.enums.AppSelectorStatus
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Piotr Woszczek on 19/06/15.
 */
object PInfoHandler {
    private const val TAG = "PInfoHandler"
    private val appIndexMap: MutableMap<Int, Int> = HashMap()
    private val filteredPInfosMap: MutableMap<Int, ArrayList<PInfo>?> = HashMap()
    private val selectedPInfosMap: MutableMap<Int, ArrayList<PInfo>?> = HashMap()
    private var allPInfos: CopyOnWriteArrayList<PInfo> = CopyOnWriteArrayList()
    fun setSelectedPInfosMap(widgetId: Int) {
        selectedPInfosMap[widgetId] = ArrayList()
    }

    fun setFilteredPInfosMap(widgetId: Int) {
        filteredPInfosMap[widgetId] = ArrayList()
    }

    fun setAllPInfos(list: ArrayList<PInfo>?) {
        allPInfos.clear()
        if (list != null) allPInfos.addAll(list)
    }

    fun getAllPInfos(): List<PInfo> {
        return allPInfos
    }

    @JvmStatic
    fun selectedPInfosNotExist(widgetId: Int): Boolean {
        return !selectedPInfosMap.containsKey(widgetId)
    }

    @JvmStatic
    fun filteredPInfosExists(widgetId: Int): Boolean {
        return filteredPInfosMap.containsKey(widgetId)
    }

    fun pInfosNotExist(): Boolean {
        return allPInfos == null || allPInfos!!.size == 0
    }

    fun getAppIndex(widgetId: Int): Int {
        return if (appIndexMap[widgetId] == null) 0 else appIndexMap[widgetId]!!
    }

    fun setAppIndex(widgetId: Int, appIndex: Int) {
        appIndexMap[widgetId] = appIndex
    }

    fun incrementAppIndex(widgetId: Int, by: Int) {
        val appIndex = appIndexMap[widgetId]
        if (appIndex != null) {
            appIndexMap[widgetId] = appIndex + by
        }
    }

    fun rollIndex(widgetId: Int) {
        val appIndex = appIndexMap[widgetId]
        val filteredPInfos = filteredPInfosMap[widgetId]
        if (appIndex != null) {
            if (filteredPInfos == null || appIndex >= filteredPInfos.size) {
                appIndexMap[widgetId] = 0
            }
            if (appIndex < 0 && filteredPInfos != null) appIndexMap[widgetId] = filteredPInfos.size - 1
        }
    }

    fun getCurrentPInfo(widgetId: Int): PInfo? {
        val appIndex = appIndexMap[widgetId]
        val filteredPInfos = filteredPInfosMap[widgetId]
        return if (appIndex != null && filteredPInfos != null) {
            filteredPInfos[appIndex]
        } else {
            null
        }
    }

    fun getPInfoFromSelected(widgetId: Int, index: Int): PInfo? {
        val selectedPInfos = selectedPInfosMap[widgetId]
        return selectedPInfos?.get(index)
    }

    fun sizeOfFiltered(widgetId: Int): Int {
        val filteredPInfos = filteredPInfosMap[widgetId]
        return filteredPInfos?.size ?: 0
    }

    fun sizeOfSelected(widgetId: Int): Int {
        val selectedPInfos = selectedPInfosMap[widgetId]
        return selectedPInfos?.size ?: 0
    }

    fun sizeOfAll(): Int {
        return allPInfos!!.size
    }

    fun addToFiltered(widgetId: Int, pInfo: PInfo) {
        if (!filteredPInfosExists(widgetId)) {
            setFilteredPInfosMap(widgetId)
        }
        val filteredPInfos = filteredPInfosMap[widgetId]
        if (filteredPInfos != null && !filteredPInfos.contains(pInfo)) filteredPInfos.add(pInfo)
    }

    @JvmStatic
    fun addToSelected(widgetId: Int, pInfo: PInfo) {
        if (selectedPInfosNotExist(widgetId)) {
            setSelectedPInfosMap(widgetId)
        }
        val selectedPInfos = selectedPInfosMap[widgetId]
        if (selectedPInfos != null && !selectedPInfos.contains(pInfo)) {
            selectedPInfos.add(pInfo)
        }
    }

    @JvmStatic
    fun addToAll(pInfo: PInfo) {
        if (pInfosNotExist()) {
            setAllPInfos(null)
        }
        if (!allPInfos!!.contains(pInfo)) {
            allPInfos!!.add(pInfo)
        }
    }

    @JvmStatic
    fun removeFromSelected(packageName: String) {
        for ((_, value) in selectedPInfosMap) if (value != null) {
            val iter = value.iterator()
            while (iter.hasNext()) {
                if (iter.next().pname == packageName) iter.remove()
            }
        }
    }

    @JvmStatic
    fun removeFromAll(packageName: String) {
        for (info in allPInfos!!) {
            if (info.pname == packageName) allPInfos!!.remove(info)
        }
    }

    fun sortFilteredByMatch(widgetId: Int) {
        val filteredPInfos = filteredPInfosMap[widgetId]
        if (filteredPInfos != null) {
            Collections.sort(filteredPInfos, PInfoComparator())
        }
    }

    fun isSystemPackage(pi: PackageInfo): Boolean {
        return pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
    }

    @JvmStatic
    fun fallsIntoSelector(pi: PackageInfo, selector: AppSelectorStatus?, context: Context): Boolean {
        return if (context.packageManager.getLaunchIntentForPackage(pi.packageName) == null) {
            false
        } else when (selector) {
            AppSelectorStatus.ALL -> true
            AppSelectorStatus.USER -> !isSystemPackage(pi)
            AppSelectorStatus.SYSTEM -> isSystemPackage(pi)
            else -> false
        }
    }

    @JvmStatic
    fun fallsIntoSelector(pi: PInfo, selector: AppSelectorStatus?): Boolean {
        return when (selector) {
            AppSelectorStatus.ALL -> true
            AppSelectorStatus.USER -> !pi.isSystemPackage
            AppSelectorStatus.SYSTEM -> pi.isSystemPackage
            else -> false
        }
    }

    class PInfoComparator : Comparator<PInfo> {
        override fun compare(o1: PInfo, o2: PInfo): Int {
            val match1 = o1.match
            val match2 = o2.match
            return if (match1 != match2) {
                match1 - match2
            } else {
                o1.appname.compareTo(o2.appname, ignoreCase = true)
            }
        }
    }
}