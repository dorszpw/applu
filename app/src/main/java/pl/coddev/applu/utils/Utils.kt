package pl.coddev.applu.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.DisplayMetrics
import pl.coddev.applu.AppluApplication.Companion.get
import pl.coddev.applu.R
import pl.coddev.applu.ui.RateDialog
import pl.coddev.applu.utils.Prefs.getFeatureCount
import pl.coddev.applu.utils.Prefs.incrementFeatureCount
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by pw on 25/02/15.
 */
object Utils {
    const val TAG = "Utils"
    fun formatDate(date: Date?): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        return df.format(date)
    }

    fun formatDateTime(date: Date?): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return df.format(date)
    }

    val currentDate: String
        get() {
            val today = Calendar.getInstance().time
            return formatDate(today)
        }

    fun getVersionName(context: Context): String {
        return try {
            "v" + context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "v1.0"
        }
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun strokeBitmap(bitmap: Bitmap?, context: Context): Bitmap? {
        var bitmapStroked: Bitmap? = null
        if (bitmap != null) {
            bitmapStroked = bitmap.copy(bitmap.config, true)
            val canvas = Canvas(bitmapStroked)
            val paint = Paint()
            paint.color = context.resources.getColor(R.color.red)
            paint.strokeWidth = 5f
            canvas.drawLine(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
            canvas.drawLine(bitmap.width.toFloat(), 0f, 0f, bitmap.height.toFloat(), paint)
        }
        return bitmapStroked
    }

    fun displayRateQuestionIfNeeded() {
        if (getFeatureCount() < Constants.FEATURE_USAGE_MAX) {
            val context: Context = get()
            val featureUsageCount = incrementFeatureCount()
            if (featureUsageCount == Constants.FEATURE_USAGE_MAX) {
                val popUpIntent = Intent(context, RateDialog::class.java)
                popUpIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(popUpIntent)
            }
        }
    }
}