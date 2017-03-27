package mobi.android.adlibrary.internal.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;

/**
 * Created by vincent on 2016/5/9.
 */
public class DeviceUtil {

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, int w, int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        //make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


        //draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }

    public static int getPixelFromDp(Context context, int pixel) {
        Context ctx = context.getApplicationContext();
        int pixels = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, getDisplayMetrics(ctx)));
        return pixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    public static int getScreenWidthInDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dpScreenWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return dpScreenWidth;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getAppVersion(Context context){
        int version = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionCode;
        }catch(PackageManager.NameNotFoundException ignore){}
        return version;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return versionName;
    }

    public static String getAppPackName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        String applicationName = "";
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        if(applicationInfo!=null){
            applicationName = applicationInfo.packageName;
        }


        return applicationName;
    }

    /**
     *
     * @param context
     * @return 1：为新用户0:老用户
     */
    public static long getFristInstallTime(Context context){
        PackageManager packageManager = context.getPackageManager() ;
        PackageInfo packageInfo = null ;
        try{
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0) ;
        }catch (PackageManager.NameNotFoundException e){
            MyLog.d("steve","没有找到包名:" + context.getPackageName());
        }
        if (packageInfo != null ) {
            return packageInfo.firstInstallTime;
        }
        return 0;
    }

    public static long getTimeFromInstallTime(Context context){
        long time = System.currentTimeMillis() - getFristInstallTime(context);
        time = time/1000;
        return time;
    }

    /**
     *
     * @param context
     */
    public static int getIsNewUser(Context context){
        PackageManager packageManager = context.getPackageManager() ;
        PackageInfo packageInfo = null ;
        try{
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0) ;
        }catch (PackageManager.NameNotFoundException e){
            MyLog.d("steve","no package name:" + context.getPackageName());
        }
        if (packageInfo != null&&packageInfo.firstInstallTime == packageInfo.lastUpdateTime ) {
            return 1;
        }
        return 0;
    }

    public static String getDeviceId(Context context) {
        String androidId = "" ;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();
            if(!StringUtil.isEmpty(deviceId)&&!StringUtil.isEmpty(androidId)){
                return deviceId + androidId ;
            }else if(!StringUtil.isEmpty(deviceId)&&StringUtil.isEmpty(androidId)){
                return deviceId ;
            }else {
                return androidId ;
            }
        }catch( Exception e ) {
            return androidId ;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static File getExternalStroageDirectory() {
        File directory = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        }
        return directory;
    }

    /**
     * 判断当前网络是否为wifi
     * @param inContext
     * @return
     */
    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }


    public static Bitmap createCircleImage(Bitmap source)
    {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap bit = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bit);

        canvas.drawCircle(width / 2, height / 2, height / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(source, 0, 0, paint);
        return bit;
    }
    
    public static Bitmap getAdmobIconRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(128,
                128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        MyLog.e(MyLog.TAG,"kuan :"+bitmap.getWidth());
        MyLog.e(MyLog.TAG,"gao :"+bitmap.getHeight());

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, 128,128);
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth()/100;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 12, 12, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
