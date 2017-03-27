package mobi.android.adlibrary.internal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.facebook.ads.NativeAd;

import java.util.Calendar;
import java.util.Random;

import mobi.android.adlibrary.AdAgent;


/**
 * Created by liuyicheng on 16/7/11.
 */
public class AdUtils {
    public static final String PREF_NAME = "mopub_ad_pref";

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(AdUtils.PREF_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public static String getCurrentData(){
        Calendar c = Calendar.getInstance();
        return ""+c.get(Calendar.YEAR)+c.get(Calendar.MONTH)+c.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(AdUtils.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(AdUtils.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(AdUtils.PREF_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    /**
     *
     * @return 获取0~100的随机数
     */
    public static int getRandomNumber(){
        return new Random().nextInt(100)+1;
    }
    /**
     * 根据最大的随机数，获取0~maxNum中的随机数值
     * @param maxNum 最大的随机数
     * @return
     */
    public static int getRandomNumberByMaxNum(int maxNum){
        if(maxNum == 0){
            return getRandomNumber();
        }
        return new Random().nextInt(maxNum)+1;
    }

    /**
     * 根据最大的随机数，获取0~maxNum中的随机数值
     * @param maxNum 最大的随机数
     * @return
     */
    public static int getNumberByMaxNum(int maxNum){
        return new Random().nextInt(maxNum);
    }
}
