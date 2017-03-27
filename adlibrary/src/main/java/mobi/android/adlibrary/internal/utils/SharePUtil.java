package mobi.android.adlibrary.internal.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.app.AdConstants;

/**
 * Created by vincent on 2016/5/9.
 */
public class SharePUtil {
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 获取是否刷新缓存池的值，默认值为0，表示true,1为false
     * @param context
     * @param slotId
     * @return
     */
    public static boolean getAdSlotIdIsWork(Context context, String slotId) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        boolean work = settings.getBoolean(AdConstants.PREF_REFRESH_CACHE_IS_WORK + slotId, true);
        return work;
    }
    /**
     * 提供给app，控制广告缓存的功能是否关闭。
     * 区别于配置（可能在app处功能已经被手动关闭，配置状态还是刷新，则浪费流量）
     * 默认的请求是功能正常，可以设置关闭。
     * @param context
     * @param slotId
     * @param isWork
     */
    public static void setAdSlotIdIsWork(Context context, String slotId, boolean isWork) {
        SharePUtil.putBoolean(context, AdConstants.PREF_REFRESH_CACHE_IS_WORK + slotId, isWork);
    }


    /**
     *
     * @param context
     * @param slotId
     * @param platform AdConstants.FB
     * @return
     */
    public static boolean getSlotIdPlatformRefreshIsWork(Context context, String slotId,String platform) {
        SharedPreferences settings = context.getSharedPreferences(AdConstants.PREF_NAME, Context.MODE_PRIVATE);
        boolean work = settings.getBoolean(AdConstants.PREF_REFRESH_CACHE_IS_WORK + slotId+platform, true);
        if(!work){
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + slotId + "当前平台被内部广告位主动关闭");
        }
        return work;
    }

    /**
     *
     * @param context
     * @param slotId
     * @param isWork
     * @param platforms AdConstants.FB
     */
    public static void setSlotIdPlatformRefreshIsWork(Context context, String slotId, boolean isWork,ArrayList<String> platforms)  {
        if(platforms == null){
            return;
        }
        if(platforms.size() == 0){
            return;
        }
        for(String platform:platforms){
            SharePUtil.putBoolean(context, AdConstants.PREF_REFRESH_CACHE_IS_WORK + slotId+platform, isWork);
        }
    }

    /**
     * get ad info form dish
     * @param context
     * @return
     */
    public static String getAdConfigInfo(Context context){
        String fullPath =  getString(context,AdConstants.CONFIG_FULL_PATH,"");
//        MyLog.d(MyLog.TAG, "get newFullPath:" + fullPath );
        return getString(context,fullPath,"");
    }

    /**
     * get ad List
     * @param context
     * @return
     */
    public static List<AdNode> getListFormAdConfig(Context context){
        String adConfig = getAdConfigInfo(context);
        ArrayList<AdNode> configs = new ArrayList<AdNode>();
        try {
            if(!StringUtil.isEmpty(adConfig)){
                JSONObject json = new JSONObject(adConfig);
                JSONArray jsonArray = json.getJSONArray(AdConstants.AD_NODES_LIST_KEY);
                for (int i=0; i<jsonArray.length(); i++) {
                    //FIXME data wrong
                    configs.add((AdNode) jsonArray.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return configs;
    }

    public static void setAdConfigInfo(Context context,String key,String value){
        putString(context,key,value);
        AdConfigLoader.getInstance(context).getConfigFormPref();
    }

}
