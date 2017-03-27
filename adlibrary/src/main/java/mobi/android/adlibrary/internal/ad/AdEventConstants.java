package mobi.android.adlibrary.internal.ad;

/**
 * ad event names
 * Created by vincent on 2016/5/9.
 */
public class AdEventConstants {

    public static final String AD_EVENT_INIT_BEGIN = "AD_SDK_INIT_BEGIN";
    public static final String AD_EVENT_INIT_EDN = "AD_SDK_INIT_END";
    public static final String AD_START_LOAD_CONFIG="startLoadConfig"; //开始拉配置
    public static final String AD_OUTSIDE_FIRST_IS_SHOW="FIRST_IS_SHOW";//外部广告分流事件打点
    public static final String AD_OUTSIDE_IS_CHANGE="CHANGE_IS_SHOW";//外部广告分流事件改变打点
    public static final String AD_CONFIG_IS_NULL="REQUEST_ADNODE_DATA_IS_NULL_";//请求广告配置为空
    //1.2.6新增
    public static final String AD_CONFIG_LOAD_DEFAULT="LOAD_COFIG_DEFAUTL";        //使用默认配置
    public static final String AD_CONFIG_LOAD_FROM_PREF="LOAD_CONFIG_FROM_PREF";   //从本地sp里获得配置

    public static final String AD_CONFIG_LOAD_REQUEST = "AD_CONFIG_LOAD_REQUEST";
    public static final String AD_CONFIG_LOAD_REQUEST_NO_CACHE = "AD_CONFIG_LOAD_REQUEST_NO_CACHE";
    public static final String AD_CONFIG_LOAD_SUCCESSED = "AD_CONFIG_LOAD_SUCCESSED";
    public static final String AD_CONFIG_LOAD_SUCCESSED_AND_NO_CACHE = "AD_CONFIG_LOAD_SUCCESSED_AND_NO_CACHE";
    public static final String AD_CONFIG_LOAD_LOCAL_AND_REQUEST_ALL_EMPTY = "AD_CONFIG_LOAD_LOCAL_AND_REQUEST_ALL_EMPTY";
    public static final String AD_CONFIG_LOAD_FAILED_EXPECTION = "AD_CONFIG_LOAD_FAILED_EXPECTION";
    public static final String AD_CONFIG_LOAD_FAILED_EXPECTION_NO_CACHE = "AD_CONFIG_LOAD_FAILED_EXPECTION_NO_CACHE";
    public static final String AD_CONFIG_LOAD_NEW_SUCCESSED = "CONFIG_LOAD_SUCCESS_AND_NEW";
    public static final String AD_CONFIG_LOAD_empty_SUCCESSED = "CONFIG_LOAD_SUCCESS_BUT_NOT_NEW";
    public static final String AD_CONFIG_LOAD_FAILED = "CONFIG_LOAD_FAILED";
    public static final String AD_CONFIG_LOAD_FAILED_NO_CACHE = "AD_CONFIG_LOAD_FAILED_NO_CACHE";
    public static final String AD_CONFIG_LOAD_FINISH = "CONFIG_FINISH_LOADING";
    public static final String AD_FILL_ADMOB_AD_OTHER_FILLED ="FILL_ADMOB_AD_OTHER_FILLED";
    public static final String AD_FILL_FULL_AD_FAILED = "FILL_FULL_SCREEN_AD_SUCCESS";
    public static final String AD_OTHER_IS_SHOWING = "SHOW_OTHER_AD_IS_SUCCESS";
    public static final String AD_REQUEST_ADMOB_OTHER_AD = "REQUEST_ADMOB_OTHER_AD";
    public static final String AD_ADMOB_AD_CLICK = "CLICK_ADMOB_AD";
    public static final String AD_LOAD_CONFIG_FROM_PRES_SUCCESS = "LOAD_CONFIG_FROM_PREF_SUCCESSFUL";
    public static final String AD_LOAD_CONFIG_FROM_PRES_NO_DATA = "LOAD_CONFIG_FROM_PRE_NULL_DATA";
    //facebook native
    public static final String AD_REQUEST_FACEBOOK_NATIVE_AD = "FB_NATIVE_REQUEST";
    public static final String AD_FACEBOOK_AD_CLICK = "FB_NATIVE_CLICK";
    public static final String AD_FILL_FACEBOOK_AD_NATIVE_FILLED = "FB_NATIVE_FILLED";
    public static final String AD_SHOW_FACEBOOK_AD_NATIVE_SHOW = "FB_NATIVE_SHOW";
    public static final String AD_REQUEST_FACEBOOK_AD_NATIVE_FIALED = "FB_NATIVE_FAIL";
    public static final String AD_NATIVE_IMPRESSION  = "FB_NATIVE_IMPRESSION";
    public static final String AD_SHOW_FACEBOOK_NATIVE_PARAMS = "SHOW_FB_NATIVE_AD_PARAMS";
    public static final String AD_CLICKED_FACEBOOK_NATIVE_PARAMS = "CLICK_FB_NATIVE_AD_PARAMS";
    //facebook fullscreen
    public static final String AD_REQUEST_FULL_SCREEN = "FB_FULL_REQUEST";
    public static final String AD_FACEBOOK_FULL_SCREEN_AD_CLICK = "FB_FULL_CLICK";
    public static final String AD_IS_SHOWING_FACEBOOK_FULL_SCREEN = "FB_FULL_SHOW";
    public static final String AD_FILL_FACEBOOK_FULL_SCREEN = "FB_FULL_FILLED";
    public static final String AD_REQUEST_FAILED_FULL_SCREEN = "FB_FULL_FAIL";
    public static final String AD_FB_REGISTERVIEWINTERACTION="facebook_Adapter_registerViewForInteraction";
    //admob fullscreen
    public static final String AD_FILL_ADMOB_FULL_SCREEN_AD = "ADMOB_FULL_FILLED";
    public static final String AD_SHOWING_ADMOB_FULL_SCREEN_AD = "ADMOB_FULL_SHOW";
    public static final String AD_REQUEST_ADMOB_FULL_SCREEN_AD = "ADMOB_FULL_REQUEST";
    public static final String AD_CLICK_ADMOB_FULL_SCREEN_AD = "ADMOB_FULL_CLICK";
    public static final String AD_REQUEST_FAILED_ADMOB_FULL_SCREEN_AD = "ADMOB_FULL_FAIL";
    //admon banner
    public static final String AD_ADMOB_BANNER_FILLED = "ADMOB_BANNER_FILLED";
    public static final String AD_ADMOB_BANNER_REQUEST = "ADMOB_BANNER_REQUEST";
    public static final String AD_ADMOB_BANNER_SHOW = "ADMOB_BANNER_SHOW";
    public static final String AD_ADMOB_BANNER_CLICK = "ADMOB_BANNER_CLICK";
    public static final String AD_ADMOB_BANNER_REQUEST_FAILED = "ADMOB_BANNER_FAIL";

    //admon native express
    public static final String AD_ADMOB_NATIVE_EXPRESS_FILLED = "ADMOB_NATIVE_EXPRESS_FILLED";
    public static final String AD_ADMOB_NATIVE_EXPRESS_REQUEST = "ADMOB_NATIVE_EXPRESS_REQUEST";
    public static final String AD_ADMOB_NATIVE_EXPRESS_SHOW = "ADMOB_NATIVE_EXPRESS_SHOW";
    public static final String AD_ADMOB_NATIVE_EXPRESS_CLICK = "ADMOB_NATIVE_EXPRESS_CLICK";
    public static final String AD_ADMOB_NATIVE_EXPRESS_REQUEST_FAILED = "ADMOB_NATIVE_EXPRESS_FAIL";

    //mopub banner
    public static final String AD_MOPUB_BANNER_FILLED = "MOPUB_BANNER_FILLED";
    public static final String AD_MOPUB_BANNER_REQUEST = "MOPUB_BANNER_REQUEST";
    public static final String AD_MOPUB_BANNER_SHOW = "MOPUB_BANNER_SHOW";
    public static final String AD_MOPUB_BANNER_CLICK = "MOPUB_BANNER_CLICK";
    public static final String AD_MOPUB_BANNER_REQUEST_FAILED = "MOPUB_BANNER_FAIL";

    //mopub native
    public static final String AD_MOPUB_NATIVE_FILLED = "MOPUB_NATIVE_FILLED";
    public static final String AD_MOPUB_NATIVE_REQUEST = "MOPUB_NATIVE_REQUEST";
    public static final String AD_MOPUB_NATIVE_SHOW = "MOPUB_NATIVE_SHOW";
    public static final String AD_MOPUB_NATIVE_CLICK = "MOPUB_NATIVE_CLICK";
    public static final String AD_MOPUB_NATIVE_REQUEST_FAILED = "MOPUB_NATIVE_FAIL";

    //admob native content
    public static final String AD_ADMOB_NATIVE_CONTENT_FILLED = "ADMOB_NATIVE_CONTENT_FILLED";
    public static final String AD_ADMOB_NATIVE_CONTENT_REQUEST = "ADMOB_NATIVE_CONTENT_REQUEST";
    public static final String AD_ADMOB_NATIVE_CONTENT_SHOW = "ADMOB_NATIVE_CONTENT_SHOW";
    public static final String AD_ADMOB_NATIVE_CONTENT_CLICK = "ADMOB_NATIVE_CONTENT_CLICK";
    public static final String AD_ADMOB_NATIVE_CONTENT_REQUEST_FAILED = "ADMOB_NATIVE_CONTENT_FAIL";
    public static final String AD_SHOW_AOMOB_CONTENT_NATIVE_PARAMS = "SHOW_ADMOB_CONTENT_NATIVE_AD_PARAMS";
    public static final String AD_CLICK_AOMOB_CONTENT_NATIVE_PARAMS = "CLICK_ADMOB_CONTENT_NATIVE_AD_PARAMS";

    //admob native app
    public static final String AD_ADMOB_NATIVE_APP_FILLED = "ADMOB_NATIVE_APP_FILLED";
    public static final String AD_ADMOB_NATIVE_APP_CONTAINS_VIDEO_FILLED = "ADMOB_NATIVE_APP_CONTAINS_VIDEO_FILLED";
    public static final String AD_ADMOB_NATIVE_APP_REQUEST = "ADMOB_NATIVE_APP_REQUEST";
    public static final String AD_ADMOB_NATIVE_APP_SHOW = "ADMOB_NATIVE_APP_SHOW";
    public static final String AD_ADMOB_NATIVE_APP_CONTAINS_VIDEO_SHOW = "ADMOB_NATIVE_APP_CONTAINS_VIDEO_SHOW";
    public static final String AD_ADMOB_NATIVE_APP_CLICK = "ADMOB_NATIVE_APP_CLICK";
    public static final String AD_ADMOB_NATIVE_APP_CONTAINS_VIDEOP_CLICK = "ADMOB_NATIVE_APP_CONTAINS_VIDEO_CLICK";
    public static final String AD_ADMOB_NATIVE_APP_REQUEST_FAILED = "ADMOB_NATIVE_APP_FAIL";
    public static final String AD_SHOW_AOMOB_APP_NATIVE_PARAMS = "SHOW_ADMOB_APP_NATIVE_AD_PARAMS";
    public static final String AD_CLICK_AOMOB_APP_NATIVE_PARAMS = "CLICK_ADMOB_APP_NATIVE_AD_PARAMS";

    //admob native request type 3
    public static final String AD_ADMOB_NATIVE_REQUEST = "ADMOB_NATIVE_REQUEST";
    public static final String AD_ADMOB_NATIVE_REQUEST_FAILED = "ADMOB_NATIVE_REQUEST_FAIL";

    //Yahoo
    public static final String YAHOO_NATIVE_REQUEST = "YAHOO_NATIVE_REQUEST";
    public static final String YAHOO_NATIVE_FAIL = "YAHOO_NATIVE_FAIL";
    public static final String YAHOO_NATIVE_FILLED = "YAHOO_NATIVE_FILLED";
    public static final String YAHOO_NATIVE_SHOW = "YAHOO_NATIVE_SHOW";
    public static final String YAHOO_NATIVE_CLICK = "YAHOO_NATIVE_CLICK";
    //VK
    public static final String VK_NATIVE_REQUEST = "VK_NATIVE_REQUEST";
    public static final String VK_NATIVE_FAIL = "VK_NATIVE_FAIL";
    public static final String VK_NATIVE_FILLED = "VK_NATIVE_FILLED";
    public static final String VK_NATIVE_SHOW = "VK_NATIVE_SHOW";
    public static final String VK_NATIVE_CLICK = "VK_NATIVE_CLICK";
    //Error
    public static final String AD_CONFIG_STYLE_IS_WRONG = "AD_CONFIG_STYLE_IS_WRONG";

    //Baidu
    public static final String BAIDU_NATIVE_REQUEST = "BAIDU_NATIVE_REQUEST";
    public static final String BAIDU_NATIVE_FAIL = "BAIDU_NATIVE_FAIL";
    public static final String BAIDU_NATIVE_FILLED = "BAIDU_NATIVE_FILLED";
    public static final String BAIDU_NATIVE_SHOW = "BAIDU_NATIVE_SHOW";
    public static final String BAIDU_NATIVE_CLICK = "BAIDU_NATIVE_CLICK";


    public static final String[] SHOW_PLATFROM_THING = {
            AD_SHOW_FACEBOOK_AD_NATIVE_SHOW,
            AD_ADMOB_BANNER_SHOW,
            AD_OTHER_IS_SHOWING,
            AD_ADMOB_NATIVE_APP_SHOW,
            AD_ADMOB_NATIVE_CONTENT_SHOW,
            AD_MOPUB_NATIVE_SHOW,
            AD_MOPUB_BANNER_SHOW,
            YAHOO_NATIVE_SHOW,
            BAIDU_NATIVE_SHOW};

    //所有广告请求成功的回调
    public static final String AD_LOAD_AD_SUCCESS_CALLBACK = "LOAD_AD_SUCCESS_CALLBACK";
    //所有广告预加载请求成功的回调
    public static final String AD_LOAD_AD_PRELOAD_SUCCESS_CALLBACK = "LOAD_AD_PRELOAD_SUCCESS_CALLBACK";
    //广告实时请求失败的回调
    public static final String AD_LOAD_AD_FAILED_NET_CALLBACK = "LOAD_AD_FAILED_NET_CALLBACK";
    //预加载实时请求失败的回调
    public static final String AD_LOAD_AD_PRELOAD_FAILED_NET_CALLBACK = "LOAD_AD_PRELOAD_FAILED_NET_CALLBACK";

    //广告请求成功的回调--展示的广告数据来源
    public static final String AD_GET_RESOURCE_FROM_CACHE = "GET_RESOURCE_FROM_CACHE";
    public static final String AD_GET_RESOURCE_FROM_NET = "GET_RESOURCE_FROM_NET";
    public static final String AD_GET_RESOURCE_FROM_NONE = "GET_RESOURCE_FROM_NONE";

    //预加载实时请求展示广告成功
    public static final String AD_GET_PRELOAD_RESOURCE_FROM_NET = "GET_PRELOAD_RESOURCE_FROM_NET";
    //填充广告的方法(有父类容器直接填充，否则由应用自己获取资源)
    public static final String AD_HAD_PARENT_AND_PUSH = "HAD_PARENT_AND_PUSH";
    public static final String AD_NO_PARENT_APP_PUSH = "NO_PARENT_APP_PUSH";
    //填充广告的时候，提供了父类容器，但是广告的adview是否可以生成
    public static final String AD_HAD_PARENT_AND_HAD_AD_VIEW_PUSH = "HAD_PARENT_AND_HAVE_VIEW_PUSH";
    public static final String AD_HAD_PARENT_AND_NO_AD_VIEW = "HAD_PARENT_BUT_NO_AD_VIEW";
    //AD_NO_PARENT_APP_PUSH(没有父容器的情况下，app自己获取广告的view或者data去处理)
    public static final String AD_NO_PARENT_APP_PUSH_GET_VIEW = "NO_PARENT_APP_PUSH_GET_VIEW";
    public static final String AD_NO_PARENT_APP_PUSH_GET_VIEW_NULL = "NO_PARENT_APP_PUSH_GET_VIEW_NULL";
    public static final String AD_NO_PARENT_APP_PUSH_GET_DATA = "NO_PARENT_APP_PUSH_GET_DATA";
    public static final String AD_NO_PARENT_APP_PUSH_GET_DATA_NULL = "NO_PARENT_APP_PUSH_GET_DATA_NULL";
    //ad request logic
    public static final String AD_LOAD_AD_NO_PASS_LIMIT_CONDITION = "LOAD_AD_NO_PASS_LIMIT_CONDITION";
    public static final String AD_LOAD_AD_BY_PRELOAD = "LOAD_AD_BY_PRELOAD";
    public static final String AD_LOAD_AD_BY_CACHE_LOGIC = "LOAD_AD_BY_CACHE_LOGIC";

    public static final String AD_LOAD_ADS_BY_FB= "LOAD_ADS_BY_FB";

    //load for ad cache(刷新缓存池)
    //当天广告的展示次数超标或者没有开启is_aloud，不需要继续缓存广告
    public static final String AD_OVER_SHOW_TIME_NOT_REFRESH = "OVER_SHOW_TIME_NOT_REFRESH";
    //缓存池中的缓存个数没有满，继续缓存广告
    public static final String AD_CACHE_NO_FULL_REFRESH_AD_CACHE = "CACHE_NO_FULL_REFRESH";
    //缓存已经满了，但是有的数据不为一级广告的缓存，继续刷新一级广告
    public static final String AD_CACHE_FULL_NOT_ONE_LEVEL_REFRESH_AD_CACHE = "CACHE_FULL_NOT_ONE_LEVEL_REFRESH";
    //所有的缓存都为第一级的广告，不需要再进行缓存
    public static final String AD_CACHE_FULL_ALL_ONE_LEVEL_NOT_REFRESH = "CACHE_FULL_ALL_REFRESH";
    //实时加载成功是在第几层才请求到
    public static final String AD_LOAD_FLOW_SUCCESSED_BY_LEVEL = "LOAD_FLOW_SUCCESSED_BY_LEVEL_";
    //实时加载失败是在第几层
    public static final String AD_LOAD_FLOW_FAILED_BY_LEVEL = "LOAD_FLOW_FAILED_BY_LEVEL_";

    public static final String BLACKLIST_LOAD_EMPTY_SUCCESSED = "BLACKLIST_SUCCESS_BUT_NOT_NEW";
    public static final String BLACKLIST_LOAD_NEW_SUCCESSED = "BLACKLIST_SUCCESS_AND_NEW";
    public static final String BLACKLIST_LOAD_FAILED = "BLACKLIST_LOAD_FAILED";

    public static final String AD_IN_BLACKLIST_IS_USED = "AD_IN_BLACKLIST_IS_USED";
    public static final String AD_IN_BLACKLIST_IS_UNUSED = "AD_IN_BLACKLIST_IS_UNUSED";

    public static final String AD_LOAD_AD_IN_GROUP="LOAD_AD_IN_GROUP";
    public static final String AD_LOAD_AD_IN_GROUP_BY_NODE="AD_LOAD_AD_IN_GROUP_BY_NODE";
    public static final String AD_LOAD_AD_IN_GROUP_SUCCESS = "LOAD_AD_IN_GROUP_SUCCESS";
    public static final String AD_LOAD_AD_IN_GROUP_FAILED = "LOAD_AD_IN_GROUP_FAILED";
    public static final String AD_LOAD_AD_REQUEST_BY_PACKAGENAME = "REQUEST_AD_BY_PACKAGENAME";
    public static final String AD_LOAD_AD_SUCCESS_BY_PACKAGENAME = "LOAD_AD_SUCCESS_BY_PACKAGENAME";
    public static final String AD_LOAD_AD_FAILED_BY_PACKAGENAME = "LOAD_AD_FAILED_BY_PACKAGENAME";
    public static final String AD_CLICK_AD_BY_PACKAGENAME = "CLICK_AD_BY_PACKAGENAME";
    public static final String AD_IMPRESSION_AD_BY_PACKAGENAME = "IMPRESSION_AD_BY_PACKAGENAME";

    //admob inter ad
    public static final String AD_ADMOB_INTERSTITIAL_AD_SHOW = "ADMOB_INTERSTITIAL_AD_SHOW";
    public static final String AD_ADMOB_INTERSTITIAL_AD_CLICK = "ADMOB_INTERSTITIAL_AD_CLICK";
    public static final String AD_ADMOB_INTERSTITIAL_AD_REQUEST_FAIL = "ADMOB_INTERSTITIAL_AD_REQUEST_FAIL";
    public static final String AD_ADMOB_INTERSTITIAL_AD_REQUEST = "ADMOB_INTERSTITIAL_AD_REQUEST";

    //FaceBook inter ad
    public static final String AD_FACEBOOK_INTERSTITIAL_AD_SHOW = "FACEBOOK_INTERSTITIAL_AD_SHOW";
    public static final String AD_FACEBOOK_INTERSTITIAL_AD_CLICK = "FACEBOOK_INTERSTITIAL_AD_CLICK";
    public static final String AD_FACEBOOK_INTERSTITIAL_AD_REQUEST_FAIL = "FACEBOOK_INTERSTITIAL_AD_REQUEST_FAIL";
    public static final String AD_FACEBOOK_INTERSTITIAL_AD_REQUEST = "FACEBOOK_INTERSTITIAL_AD_REQUEST";

    public static final String AD_IN_WHITELIST_IS_USED = "AD_IN_WHITELIST_IS_USED";
    public static final String AD_IN_WHITELIST_IS_UNUSED = "AD_IN_WHITELIST_IS_UNUSED";
}
