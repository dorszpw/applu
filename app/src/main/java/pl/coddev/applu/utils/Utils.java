package pl.coddev.applu.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.coddev.applu.AppluApplication;
import pl.coddev.applu.R;
import pl.coddev.applu.ui.RateDialog;

/**
 * Created by pw on 25/02/15.
 */
public final class Utils {
    public static final String TAG = "Utils";

    private Utils() {
    }

    public static String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static String formatDateTime(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String getCurrentDate() {
        Date today = Calendar.getInstance().getTime();
        return formatDate(today);
    }

    public static String getVersionName(Context context) {
        try {
            return "v" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "v1.0";
        }
    }

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static Bitmap strokeBitmap(Bitmap bitmap, Context context) {
        Bitmap bitmapStroked = null;
        if (bitmap != null) {
            bitmapStroked = bitmap.copy(bitmap.getConfig(), true);
            Canvas canvas = new Canvas(bitmapStroked);
            Paint paint = new Paint();
            paint.setColor(context.getResources().getColor(R.color.red));
            paint.setStrokeWidth(5);
            canvas.drawLine(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
            canvas.drawLine(bitmap.getWidth(), 0, 0, bitmap.getHeight(), paint);
        }
        return bitmapStroked;
    }

    public static void displayRateQuestionIfNeeded() {
        if (Prefs.getFeatureCount() < Constants.FEATURE_USAGE_MAX) {
            Context context = AppluApplication.get();

            int featureUsageCount = Prefs.incrementFeatureCount();

            if (featureUsageCount == Constants.FEATURE_USAGE_MAX) {
                Intent popUpIntent = new Intent(context, RateDialog.class);
                popUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(popUpIntent);
            }
        }
    }
}
