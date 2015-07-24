package pl.coddev.applu.b;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.util.LruCache;


/**
 * Created by Piotr Woszczek on 19/06/15.
 */
public final class Cache {

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
        if (mMemoryCache.get(key) == null){
            try {
                Drawable drawable = pm.getApplicationIcon(key);
                if(drawable instanceof BitmapDrawable){
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                } else if(drawable instanceof VectorDrawable){
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
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
}
