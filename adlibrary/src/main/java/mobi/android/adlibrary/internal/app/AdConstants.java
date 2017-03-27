package mobi.android.adlibrary.internal.app;

/**
 * Created by Jerry on 16/5/4.
 */
public class AdConstants {
    //电池充电电量的值对应的refresh缓存池的状态
    public static final String BATTERY_NORMAL = "battery_normal";
    public static final String BATTERY_LOW = "battery_low";
    public static final String BATTERY_OFF = "battery_off";
    //白天黑夜模式对应的refresh缓存池的状态
    public static final String DAY_NORMAL = "day_normal";
    public static final String DAY_LOW = "day_low";
    public static final String DAY_OFF = "day_off";

    //根据当前充电电量或者出广告位的次数决定referesh缓存池的状态。（减少不必要的广告请求）
    public static final String REFRESH_NORMAL = "normal";
    //当前状态下，缓存池已经填满，不需要进行全部一级的多次请求加载
    public static final String REFRESH_LOW = "low";
    public static final String REFRESH_OFF = "off";

    public static final String FB = "facebook";
    public static final String ADMOB = "admob";
    public static final String INMOBI = "inmobi";
    public static final String MOPUB = "mopub";
    public static final String YAHOO = "yahoo";
    public static final String VK = "vk";
    public static final String BAIDU = "baidu";

    public static final long FB_EXPIRE_TIME = 11*60*60*1000;
    public static final long ADMOB_EXPIRE_TIME = 45*60*1000;
    public static final long MOUPB_EXPIRE_TIME = 45*60*1000;
    public static final long YH_EXPIRE_TIME = 60*60*1000;
    public static final long VK_EXPIRE_TIME = 3*60*60*1000;
    public static final String ACTION_LOAD_CONFIG = "action_load_config";
    public static final String ACTION_REFRESH_CACHE = "action_refresh_cache";
    public static final String ACTION_ADMOB_INTER = "action_admob_inter";
    public static final String SDK = "sdk";
    public static final String CONFIG_FULL_PATH = "config_full_path";
    public static final String REFRESH_TIME_LIMITE = "refresh_time_limite";
    public static final String PREF_NAME = "mopub_ad_pref";

    public static final int NET_CONNECT_TIMEOUT = 30*1000;
    public static final int NET_READ_TIMEOUT = 30*1000;

    public static final String AD_NODES_LIST_KEY = "ADSolt_Config";

    public static final String ALL_AD_IS_NOT_FILL = "ad flow is add over ,but not get the data";

    public static final String CACHE_LOG  = "cache -->      ";
    public static final String LOGIC_LOG  = "logic -->      ";
    public static final String LISTENER_LOG  = "listener -->      ";

    public static final int MAX_FLOW_NUM = 20; //max flow number for one node

    public static final String PREF_BLACKLIST_FILE_VERSION = "black_list_file_version";
    public static final String PREF_REFRESH_CACHE_IS_WORK = "cache_work_";

    public static final String FB_NO_MATCH_ADS = "FB_NO_MATCH_ADS";

    public static final String LAST_CLICK_TIME = "LAST_CLICK_TIME";


    public static final String PREF_ADMOB_SHOW_TIMES = "admob_ad_show_times";
    public static final String PREF_ADMOB_SHOW_LAST_TIME = "admob_ad_show_last_time";

}
