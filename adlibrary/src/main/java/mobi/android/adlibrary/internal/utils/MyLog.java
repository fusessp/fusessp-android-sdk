package mobi.android.adlibrary.internal.utils;

import android.util.Log;



public class MyLog {

    public static boolean DEBUG_MODE = false;

    public static String TAG = "AD_SDK";

    public static void i(String tag,String msg) {
        if (DEBUG_MODE) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag,String msg) {
        if (DEBUG_MODE) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag,String msg) {
        if (DEBUG_MODE) {
            Log.v(tag, msg);
        }
    }

    public static void e(String tag,String msg) {
        if (DEBUG_MODE) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag,String msg) {
        if (DEBUG_MODE) {
            Log.w(tag, msg);
        }
    }
}
