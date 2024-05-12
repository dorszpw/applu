package pl.coddev.applu.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.collection.LruCache
import pl.coddev.applu.utils.Log

/**
 * Created by Piotr Woszczek on 19/06/15.
 */
class Cache {
    private val mMemoryCache: LruCache<String, Bitmap?>
    fun getBitmapFromMemCache(key: String, pm: PackageManager): Bitmap? {
        var bitmap: Bitmap? = null
        Log.d(TAG, "Icon drawable key: $key")
        if (mMemoryCache[key] == null) {
            try {
                val drawable = pm.getApplicationIcon(key)
                Log.d(TAG, "Icon drawable is null: " + (drawable == null))
                Log.d(TAG, "Icon drawable class: " + drawable.javaClass)
                if (drawable is BitmapDrawable) {
                    bitmap = drawable.bitmap
                    Log.d(TAG, "Icon drawable bitmap: " + bitmap.byteCount)
                } else if (Build.VERSION.SDK_INT >= 21 && drawable is VectorDrawable ||
                        Build.VERSION.SDK_INT >= 26 && drawable is AdaptiveIconDrawable) {
                    bitmap = getBitmapFromDrawable(drawable)
                } else {
                    return null
                }
                mMemoryCache.put(key, bitmap!!)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        } else {
            bitmap = mMemoryCache[key]
        }
        return bitmap
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    companion object {
        private const val TAG = "Cache"
        var instance: Cache? = null
            get() {
                if (field == null) {
                    field = Cache()
                }
                return field
            }
            private set
    }

    init {

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8
        mMemoryCache = LruCache(cacheSize)
    }
}