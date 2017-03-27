package mobi.android.adlibrary.internal.ad.config;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.adapter.FlurryNativeAdAdapter;
import mobi.android.adlibrary.internal.ad.bean.AdPlacementConfig;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.utils.FileUtil;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.SharePUtil;
import mobi.android.adlibrary.internal.utils.StringUtil;

/**
 * Created by vincent on 2016/5/12.
 */
    public class AdConfigLoader {
    private static final String TAG = "AdConfigLoader";
    private static volatile AdConfigLoader instance;
    private static Gson gson = new Gson();
    private final AtomicBoolean mDoorOpened = new AtomicBoolean(false);
    private String mConfig;
    private Context mContext;
    private volatile AdPlacementConfig mConfigModel;
    private String jsonStr;

    private AdConfigLoader(Context context){
        this.mContext = context.getApplicationContext();
        MyLog.d(MyLog.TAG,"new AdConfigLoader ");
    }

    public synchronized static AdConfigLoader getInstance(Context context) {
        if (instance == null) {
            AdConfigLoader tmp =new AdConfigLoader(context);
            if(tmp.init()){
                instance=tmp;
            }else {
                return tmp;
            }
        }
        return instance;
    }

    private boolean init() {
        boolean status = false;
        if (getConfigFormPref() != null) {
            status = true;
        }
        if (!TextUtils.isEmpty(getYhAppKey())) {
            FlurryNativeAdAdapter.initFlurry(mContext, getYhAppKey());
        }
        return status;
    }

    private void loadConfig(){
        try {
            InputStream is = mContext.getAssets().open("default_ad_config.json");
            mConfig= FileUtil.loadContent(is);
        } catch (IOException e) {
            MyLog.e(MyLog.TAG,"load config exception:"+e.getMessage());
            e.printStackTrace();
        }
    }

    public AdPlacementConfig getConfig(){
        return mConfigModel;
    }

    public AdPlacementConfig getConfigFormPref() {
            jsonStr = SharePUtil.getAdConfigInfo(mContext);
            if(!TextUtils.isEmpty(jsonStr)) {
                mConfigModel = parse(jsonStr);
                if(mConfigModel!=null){
                    //note use sendEventNoAdConfig in case endless loop
                    MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_LOAD_FROM_PREF);
                }
            }
          if(mConfigModel==null){
            loadConfig();
            if(mConfig!=null){
                mConfigModel = parse(mConfig);
            }
            //note use sendEventNoAdConfig in case endless loop
              MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_LOAD_DEFAULT);
         }

        return mConfigModel;
    }


    /**
     * get Platform status
     * @return
     */
    public boolean getPlatformStatusFromConfig(String platform) {
        boolean isWork = true;
        if (StringUtil.isEmpty(platform) || mConfigModel == null) {
            return false;
        }
        if (mConfigModel.SDK_Config == null) {
            return true;
        }
        if (AdConstants.FB.equals(platform)) {
            isWork = mConfigModel.SDK_Config.facebook_status;
        } else if (AdConstants.ADMOB.equals(platform)) {
            isWork = mConfigModel.SDK_Config.admob_status;
        } else if (AdConstants.MOPUB.equals(platform)) {
            isWork = mConfigModel.SDK_Config.mopub_status;
        }

        if(!isWork){
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "当前平台被内部广告位主动关闭");
        }

        MyLog.d(MyLog.TAG,AdConstants.LOGIC_LOG +"PlatForm:"+platform+"当前的广告平台广告功能是否打开"+isWork);

        return isWork;

    }


    /**
     * get giftUrl from config
     * @return
     */
    public AdPlacementConfig.InterAd getAdmobInterAdConfig (){

        AdPlacementConfig.InterAd admobInterAd  = null;
        if(mConfigModel!=null){
            admobInterAd = mConfigModel.InterAd_Config;
        }
        return admobInterAd;
    }


    /**
     * get giftUrl from config
     * @return
     */
    public float getSegmentFromConfig (){
        float segmentId = 0f;
        if(mConfigModel!=null){
            segmentId = mConfigModel.segment_id;
        }
        return segmentId;
    }
    /**
     * get Fb Cache Expire Time FromConfig
     * @return
     */
    public long getFbCacheExpireTimeFromConfig (){
        long facebook_lifetime = 0;
        if(mConfigModel != null&&mConfigModel.SDK_Config != null){
            facebook_lifetime = mConfigModel.SDK_Config.facebook_lifetime;
        }
        MyLog.d(MyLog.TAG,"facebook_lifetime 配置过期时间 "+ facebook_lifetime);
        return facebook_lifetime;
    }

    public long getYhCacheExpireTimeFromConfig() {
        long yahoo_lifetime = 0;
        if(mConfigModel != null&&mConfigModel.SDK_Config != null){
            yahoo_lifetime = mConfigModel.SDK_Config.yahoo_lifetime;
        }
        MyLog.d(MyLog.TAG,"yahoo_lifetime 配置过期时间 "+ yahoo_lifetime);
        return yahoo_lifetime == 0? AdConstants.YH_EXPIRE_TIME:yahoo_lifetime;
    }

    public long getVkCacheExpireTimeFromConfig() {
        long vk_lifetime = 0;
        if(mConfigModel != null&&mConfigModel.SDK_Config != null){
            vk_lifetime = mConfigModel.SDK_Config.vk_lifetime;
        }
        MyLog.d(MyLog.TAG,"vk_lifetime 配置过期时间 "+ vk_lifetime);
        return vk_lifetime == 0? AdConstants.VK_EXPIRE_TIME:vk_lifetime;
    }

    public String getYhAppKey() {
        if(mConfigModel == null){
            return null;
        }
        MyLog.d(MyLog.TAG,"yahoo_appkey  : "+ mConfigModel.yahoo_appkey);
        return mConfigModel.yahoo_appkey;
    }
    /**
     * get Admob Cache Expire Time FromConfig
     * @return
     */
    public long getAdmobCacheExpireTimeFromConfig (){
        long admob_lifetime = 0;
        if(mConfigModel != null&&mConfigModel.SDK_Config != null){
            admob_lifetime = mConfigModel.SDK_Config.admob_lifetime;
        }
        MyLog.d(MyLog.TAG,"admob_lifetime 配置过期时间 "+ admob_lifetime);
        return admob_lifetime;
    }

    /**
     * get baidu cache expire time from config
     */
    public long getBaiduCacheExpireTimeFromConfig(){
        long baidu_lifetime = 0;
        if(mConfigModel != null&&mConfigModel.SDK_Config != null){
            baidu_lifetime = mConfigModel.SDK_Config.baidu_lifetime;
        }
        MyLog.d(MyLog.TAG,"admob_lifetime 配置过期时间 "+ baidu_lifetime);
        return baidu_lifetime;
    }
    /**
     * get Ad Cache Expire Time From Config
     * @return
     */
    public long getAdCacheExpireTimeFromConfig (){
        long facebook_lifetime = 0;
        if(mConfigModel!=null){
            facebook_lifetime = mConfigModel.SDK_Config.admob_lifetime;
        }
        return facebook_lifetime;
    }

    /*
    * get Admob Cache Expire Time FromConfig
    * @return
            */
    public long getMopubCacheExpireTimeFromConfig (){
        long mopub_lifetime = 0;
        if(mConfigModel != null&&mConfigModel.SDK_Config != null){
            mopub_lifetime = mConfigModel.SDK_Config.mopub_lifetime;
        }
        MyLog.d(MyLog.TAG,"mopub_lifetime 配置过期时间 "+ mopub_lifetime);
        return mopub_lifetime;
    }

    public ArrayList<AdNode> getAllListNodes(){
        ArrayList<AdNode> allNodesList = null;
        if(mConfigModel!=null){
            allNodesList = (ArrayList<AdNode>) mConfigModel.ADSlot_Config;
            if(allNodesList!=null){
                MyLog.d(MyLog.TAG,"allNodesList:size"+allNodesList.size());
            }
        }else{
           MyLog.d(MyLog.TAG,"mConfigModel:null");
        }
        return allNodesList;
    }

//    public ArrayList<AdNode> getAllNativeListNodes(){
//        ArrayList<AdNode> allNativeNodesList = null;
//        ArrayList<AdNode> allNodesList = getAllListNodes();
//        if(mConfigModel!=null&&allNodesList!=null){
//            int size = allNodesList.size();
//            allNativeNodesList = new  ArrayList<AdNode>();
//            for(int i = 0; i < size ; i++){
//                AdNode adNode = allNodesList.get(i);
//                if(adNode.is_auload){
//                    allNativeNodesList.add(adNode);
//                }
//            }
//            MyLog.d(MyLog.TAG,"all list Nodes size: " + allNativeNodesList.size());
//        }else{
//            MyLog.d(MyLog.TAG,"mConfigModel list is null");
//        }
//
//        return allNativeNodesList;
//    }

    public HashMap<String,AdNode> getAllMapNodes(){
        HashMap<String,AdNode> allNodesMap = null;
        ArrayList<AdNode> allNodesList = getAllListNodes();
        if(mConfigModel!=null&&allNodesList!=null){
            int size = allNodesList.size();
            allNodesMap = new HashMap<String,AdNode>();
            for(int i = 0; i < size ; i++){
                allNodesMap.put(allNodesList.get(i).slot_id,allNodesList.get(i));
            }
            MyLog.d(MyLog.TAG,"all map Nodes size: " + allNodesMap.size());
        }else{
            MyLog.d(MyLog.TAG,"mConfigModel Map is null");
        }
        return allNodesMap;
    }

    public AdNode getAdNodeByAdId(String slotId){

        AdNode ad = null;
        if(mConfigModel!=null){
            HashMap<String,AdNode> allNodesMap = getAllMapNodes();
            if(allNodesMap!=null&&allNodesMap.size()>0){
                ad = allNodesMap.get(slotId);
                if(ad!=null){
                    MyLog.d(MyLog.TAG,"get node success");
                }else{
                    MyLog.d(MyLog.TAG,"get node failed");
                }
            }else{
                MyLog.d(MyLog.TAG,"get node:null");
            }
        }else{
            MyLog.d(MyLog.TAG," getAdNode mConfigModel:null");
        }

        return ad;
    }

    public String getAdNoadeNameById(String slotId){
        String nodeName = "";
        AdNode node= getAdNodeByAdId(slotId);
        if(node!=null){
            nodeName = node.slot_name;
        }
        return nodeName;
    }



    private AdPlacementConfig parse(String json){
        AdPlacementConfig result = null;
        try{
            result  =  gson.fromJson(json, AdPlacementConfig.class);
        }catch (Exception exception){
            MyLog.e(MyLog.TAG,"ad config format is wrong");
        }
        return result;
    }

    public void openDoor() {
        mDoorOpened.set(true);
//        loadConfig();
    }

    public void closeDoor() {
        mDoorOpened.set(false);
//        loadConfig();
    }
}
