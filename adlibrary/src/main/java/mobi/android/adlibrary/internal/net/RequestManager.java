package mobi.android.adlibrary.internal.net;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.utils.DeviceUtil;
import mobi.android.adlibrary.internal.utils.FileUtil;
import mobi.android.adlibrary.internal.utils.HttpRequest;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.SharePUtil;
import mobi.android.adlibrary.internal.utils.StringUtil;

/**
 * Created by vincent on 2016/5/9.
 */
public class RequestManager {

    /**
     * get ad config form server
     *
     * @param context
     */
    public static void requestAdConfig(Context context, String urlPath, String channel, String installchannel, RequestAdOkListener okListener) {
        String adConfigInfo = SharePUtil.getAdConfigInfo(context);
        String fullPath = FileUtil.getAllConfigPath(context, urlPath, adConfigInfo, channel, installchannel,false);
        MyLog.d(MyLog.TAG, "request_config_path: " + fullPath);
        try {
            HttpRequest response = HttpRequest.get(fullPath).acceptJson().connectTimeout(AdConstants.NET_CONNECT_TIMEOUT)
                    .readTimeout(AdConstants.NET_READ_TIMEOUT);
            int responseCode = response.code();
            MyLog.d(MyLog.TAG, "response code:" + responseCode);
            //根据是否有缓存来做请求成功的打点事件
            long intervalTime = DeviceUtil.getTimeFromInstallTime(context);

            if (response.ok()) {
                String jsonStr = response.body();
                MyLog.d(MyLog.TAG, "loading config " + jsonStr);
                JSONObject json = new JSONObject(jsonStr);
                MyLog.d(MyLog.TAG, "data code:" + json.optInt("code"));
                int code = json.optInt("code");
                if (code != 0) {
                    MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_LOAD_empty_SUCCESSED + "");
                } else {
                    if (json.optBoolean("open_status")) {
                        String newFullPath = FileUtil.getAllConfigPath(context, urlPath, jsonStr, channel, installchannel,false);
                        MyLog.d(MyLog.TAG, "save newFullPath:" + newFullPath);
                        SharePUtil.putString(context, AdConstants.CONFIG_FULL_PATH, newFullPath);
                        SharePUtil.setAdConfigInfo(context, newFullPath, jsonStr);
                        long refreshTimeLimite = json.optLong("refresh_time_limite");
                        if (refreshTimeLimite != 0L) {
                            SharePUtil.putLong(context, AdConstants.REFRESH_TIME_LIMITE, refreshTimeLimite);
                            MyLog.d(MyLog.TAG, "get refreshTimeLimite : " + refreshTimeLimite);
                        }
                    } else {
                        MyLog.d(MyLog.TAG, "code:" + json.optInt("code"));
                    }
                    MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_LOAD_NEW_SUCCESSED + "");

                }


                if (okListener != null) {
                    if (AdConfigLoader.getInstance(context).getConfig() != null) {
                        okListener.getNewConfigRespond(AdConfigLoader.getInstance(context).getConfig().refresh_frequence, code);
                    } else {
                        okListener.getNewConfigRespond(60 * 60 * 1000, code);
                    }
                    MyLog.d(MyLog.TAG, "request finished and code=" + code);

                }
            } else {
                okListener.getNewConfigRespond(10 * 60 * 1000, 404);
            }
            MyLog.d(MyLog.TAG, "config load service finished");
        } catch (Exception e) {
            e.printStackTrace();
            String expection = e.getMessage();
            if(!StringUtil.isEmpty(expection)&&expection.length()>25){
                expection = expection.substring(0,25);
            }
            HashMap<String,String> dataInfoException = new HashMap<>();
            long intervalTimeException = DeviceUtil.getTimeFromInstallTime(context);
            dataInfoException.put("intervalTime",String.valueOf(intervalTimeException));
            dataInfoException.put("exception",expection);
//            if (StringUtil.isEmpty(adConfigInfo)) {
//                MyLog.d(MyLog.TAG,AdEventConstants.AD_CONFIG_LOAD_FAILED_EXPECTION_NO_CACHE +
//                        "expection:"+expection);
//            }else{
//                MyLog.d(MyLog.TAG,AdEventConstants.AD_CONFIG_LOAD_FAILED_EXPECTION +
//                        "expection:"+expection);
//            }
            if (okListener != null) {
                if (AdConfigLoader.getInstance(context).getConfig() != null) {
                    okListener.getNewConfigRespond(AdConfigLoader.getInstance(context).getConfig().refresh_frequence, 404);
                } else {
                    okListener.getNewConfigRespond(6 * 60 * 1000, 404);
                }
                MyLog.d(MyLog.TAG, "request finished and code=404");
            }
            MyLog.d(MyLog.TAG, "requestAdConfig" + e.getMessage());
        }
        //请求结束之后判断当前的数据是否成功。
        if (StringUtil.isEmpty(SharePUtil.getAdConfigInfo(context))) {
            MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_LOAD_LOCAL_AND_REQUEST_ALL_EMPTY);
        }
    }


}
