package pl.coddev.applu.b;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

import pl.coddev.applu.c.Log;


/**
 * Created by Piotr Woszczek on 19/06/15.
 */
public final class Cache {

    private static final String TAG = "Cache";
    private LruCache<String, Bitmap> mMemoryCache;
    private static Cache instance;

    public Cache(){

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize);
    }

    public static Cache getInstance(){
        if(instance == null){
            instance = new Cache();
        }
        return instance;
    }

    public Bitmap getBitmapFromMemCache(String key, PackageManager pm) {
        Bitmap bitmap=null;
        Log.d(TAG, "Icon drawable key: " + key);
        if (mMemoryCache.get(key) == null){
            try {
                Drawable drawable = pm.getApplicationIcon(key);

                Log.d(TAG, "Icon drawable is null: " + (drawable == null));
                Log.d(TAG, "Icon drawable class: " + drawable.getClass());
                if(drawable instanceof BitmapDrawable){
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Log.d(TAG, "Icon drawable bitmap: " + bitmap.getByteCount());
                } else if((Build.VERSION.SDK_INT >= 21 && drawable instanceof VectorDrawable) || (
                        Build.VERSION.SDK_INT >= 26 && drawable instanceof AdaptiveIconDrawable)){
                    bitmap = getBitmapFromDrawable(drawable);
                } else {
                    return null;
                }
                mMemoryCache.put(key, bitmap);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = mMemoryCache.get(key);

        }
        return bitmap;
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
}
