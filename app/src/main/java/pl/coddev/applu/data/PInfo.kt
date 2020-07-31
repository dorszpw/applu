package pl.coddev.applu.data

/**
 * Created by Piotr Woszczek on 20/05/15.
 */
class PInfo {
    var appname = ""
    var pname = ""
    var match = 0
    var matcherGroup = ""

    @set:Synchronized
    var isRemoved = false

    var isSystemPackage = false

    companion object {
        private const val TAG = "PInfo"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PInfo

        if (pname != other.pname) return false

        return true
    }

    override fun hashCode(): Int {
        return pname.hashCode()
    }
}