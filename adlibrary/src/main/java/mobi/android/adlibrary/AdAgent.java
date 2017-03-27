package mobi.android.adlibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import mobi.android.adlibrary.internal.ad.Ad;
import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdLoadListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.ad.nativeview.NativeAdViewManager;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.app.AdPreloadService;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.net.RequestAdOkListener;
import mobi.android.adlibrary.internal.net.RequestManager;
import mobi.android.adlibrary.internal.utils.FileUtil;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.StringUtil;

/**
 * Created by vincent on 16/5/3.
 */
public class AdAgent {
    public static final String APP_CONFIG_URL = "AppConfigUrl";
    //广告配置拉取的默认间隙时间
    private final static long DEFAULT_CONFIG_LEAST_INTERVAL_TIME = 60 * 60 * 1000;
    private static AdAgent mInstance;
    public Drawable mActionBackground = null;
    private boolean mIsInit;
    private volatile boolean mOn;
    private Context mContext;
    private String mConfigUrl;
    private String mChannel;
    private String mInstallChannel;
    //电池充电的状态决定的refresh缓存池的状态
    private String mBatteryStatus = AdConstants.BATTERY_NORMAL;
    //当前白天还是黑夜对应的refresh缓存池的状态
    private String mAdMod = AdConstants.DAY_NORMAL;

    private AdAgent() {
        mIsInit = true;
        mOn = true;
    }

    public static AdAgent getInstance() {
        if (mInstance == null) {
            synchronized (AdAgent.class) {
                if (mInstance == null) {
                    mInstance = new AdAgent();
                }
            }
        }
        return mInstance;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setActionBackground(Drawable background) {
        mActionBackground = background;
    }


    /**
     * ad SDK access point ,init info
     *
     */
    public void init(Context context, String configUrl) {
        init(context, configUrl, null, null);
    }

    /**
     * ad SDK access point ,init info
     *
     * @param channel        app channel
     * @param installChannel app installChannel
     */
    public void init(Context context, String configUrl, String channel, String installChannel) {
        if (context == null) {
            throw new IllegalArgumentException("init params Context is NULL");
        }
        if(!mOn){
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "广告关闭");
            return;
        }
        mChannel = channel;
        mInstallChannel = installChannel;
        mContext = context.getApplicationContext();
        mConfigUrl = configUrl;
        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "广告进行初始化");

        if (mIsInit) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "广告第一次初始化");
            if (mConfigUrl == null) {
                MyLog.e(MyLog.TAG, "配置文件的url为null,请检查传入的配置！");
                return;
            }
            MyLog.d(MyLog.TAG, AdEventConstants.AD_EVENT_INIT_BEGIN + "ProcessID:" + android.os.Process.myPid());
            startLoadConfig(AdConstants.ACTION_LOAD_CONFIG, mConfigUrl, mIsInit, AdPreloadService.LOAD_SERVICE_STATIC);
            initImageLoader(context);
            NativeAdViewManager.initLayout();
            setTestDeveice();
            mIsInit = false;
        } else {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "广告第二次始化");
        }
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * load ad config from net or file
     *
     * @param action
     * @param configUrl ad config url path
     */
    public void startLoadConfig(String action, String configUrl, boolean isInit, int type) {
        MyLog.d(MyLog.TAG, AdEventConstants.AD_START_LOAD_CONFIG);

        if (mContext != null) {
            Intent intent = new Intent();
            intent.setClass(mContext, AdPreloadService.class);
            intent.setAction(action);
            mContext.startService(intent);
            if (type == AdPreloadService.LOAD_SERVICE_STATIC) {
                AdPreloadService.startPreloadService(mContext);
            }
        } else {
            MyLog.e(MyLog.TAG, "上下文对象为空，请检查传入的Context");
        }
    }


    /**
     * load adInfo by Ad object
     *
     * @param context
     * @param ad
     * @param listener
     */
    public void loadAd(Context context, Ad ad, OnAdLoadListener listener) {
        if(!mOn){
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "广告关闭");
            return;
        }
        if (ad != null) {
            ad = new Ad.Builder(context, ad).setOnAdLoadListener(listener).build();
            ad.loadAd();
        }
    }

    /**
     * 获取当前缓存中的数据
     * @param context
     * @param slotId
     * @return
     */
    public NativeAdData getNativeAdData(Context context,String slotId){
        return CacheManager.getInstance().getNativeCache(getAdNodeBySlotId(context,slotId));
    }


    /**
     * get Ad Node By SlotId
     *
     * @param slotId
     * @return
     */
    public AdNode getAdNodeBySlotId(Context context, String slotId) {
        AdNode adNode = AdConfigLoader.getInstance(context).getAdNodeByAdId(slotId);
        return adNode;
    }


    private void setTestDeveice() {
        if (!MyLog.DEBUG_MODE) {
            return;
        }
    }

    public void loadAdConfig() {
        if (mIsInit) {
            MyLog.d(MyLog.TAG, AdEventConstants.AD_EVENT_INIT_EDN + "     ProcessID:" + android.os.Process.myPid());
        }
        if (!StringUtil.isEmpty(mConfigUrl)) {
            String sdCardTestUrl = FileUtil.getSdCardTestUrl();
            if (!TextUtils.isEmpty(sdCardTestUrl)) {
                mConfigUrl = sdCardTestUrl;
            }

            MyLog.d(MyLog.TAG, "mConfigUrl load url:" + mConfigUrl);
            RequestManager.requestAdConfig(mContext, mConfigUrl, mChannel, mInstallChannel, new RequestAdOkListener() {
                @Override
                public void getNewConfigRespond(float intervalLoadTime, int code) {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "下次加载时间为：" + intervalLoadTime + ";code=" + code);
                    scheduleLoadConfig((long) intervalLoadTime);

                }
            });
        }
        MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_LOAD_FINISH);
    }

    private void scheduleLoadConfig(long intervalLoadTime) {
        long loadConfigTime = DEFAULT_CONFIG_LEAST_INTERVAL_TIME ;
        if (loadConfigTime < intervalLoadTime){
            loadConfigTime = intervalLoadTime;
        }
        MyLog.d(MyLog.TAG,"finally loadConfig time :"+ loadConfigTime);
        Intent alarmIntent = new Intent(mContext, AdPreloadService.class);
        alarmIntent.setAction(AdConstants.ACTION_LOAD_CONFIG);
        PendingIntent pintent = PendingIntent.getService(mContext, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + loadConfigTime, pintent);
    }
}


