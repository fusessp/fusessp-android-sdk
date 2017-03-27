package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.MediaView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import java.util.ArrayList;
import java.util.HashMap;

import mobi.android.adlibrary.R;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.AdErrorType;
import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.IAd;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.OnAdInnerLoadListener;
import mobi.android.adlibrary.internal.ad.OnAdLoadListener;
import mobi.android.adlibrary.internal.ad.OnCancelAdListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;
import mobi.android.adlibrary.internal.ad.bean.AdFlowLimiteInfo;
import mobi.android.adlibrary.internal.ad.bean.AdLimitAdInfo;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.ad.nativeview.FbNativeAdData;
import mobi.android.adlibrary.internal.ad.nativeview.NativeAdView;
import mobi.android.adlibrary.internal.ad.nativeview.NativeAdViewManager;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.AdUtils;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.SharePUtil;
import mobi.android.adlibrary.internal.utils.StringUtil;


/**
 *  Created by vincent on 16/5/4.
 */
public class FlowAdManager extends AdAdapter {
    private NativeAdData mNativeAd;
    private Flow mFlow;
    private NativeAdView mNativeAdView;
    private volatile boolean mIsCallBack = false;         //用来辨别当前的广告是否已经回调过
    private HashMap<String, AdLimitAdInfo> mTimeLimitAdGeneratorMap;

    private AdAdapter mAdAdapter = null;
    private OnAdLoadListener adLoadListener;
    private AdNode mAdNode;
    private ViewGroup mParentView;
    private ArrayList<ViewGroup> mParentsViewGroup;
    private int mViewGroupIndex;

    private String mAdPackageName;

    public FlowAdManager(Context context) {
        super(context);
        mTimeLimitAdGeneratorMap = new HashMap<String, AdLimitAdInfo>();
    }

    /**
     * According limit time ，judge load or not
     *
     * @param context
     * @param FlowID
     * @param mDayTimes
     * @return
     */
    private static boolean getAdFlowLimitByTime(Context context, String FlowID, float mDayTimes) {

        AdFlowLimiteInfo flowLimiteInfo = new AdFlowLimiteInfo();
        MyLog.d(MyLog.TAG, "FLow ID:" + FlowID + "-----mDayTimes" + mDayTimes);
        flowLimiteInfo.init(context, mDayTimes, FlowID);
        return flowLimiteInfo.getFlowAdSuccessedByLimit();
    }

    /**
     * load ad only from request
     *
     * @param node               当前广告位的信息
     * @param lowestRequestLevel 需要请求的最低级别
     * @param isCallBack         是否需要回调
     * @param parentView         回调的时候需要的父容器
     */
    public void loadAdOnlyRequest(final AdNode node, final int lowestRequestLevel, final boolean isCallBack, final ViewGroup parentView) {
        if (node == null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->传入的广告数据为null-->return");
            return;
        }
        if (!node.open_status) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->当前广告位的openStatus为关闭状态-->" + "nodeId:" + node.slot_id);
            return;
        }

        mParentView = parentView;
        mAdNode = node;
        loadFlow("",0,null,node, 0, !isCallBack,new OnFlowFinished() {
            @Override
            public void onFlowLoadSuccess(AdNode node, IAd iAd, int index) {
                if (iAd != null && iAd.getNativeAd() != null) {
                    MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_FLOW_SUCCESSED_BY_LEVEL + index +
                            AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "刷新缓存-->广告加载成功");
                    if (isCallBack) {
                        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->需要回调，通知用户加载成功" + "node_id" + node.slot_id);
                            saveAd(node,index,iAd.getNativeAd());
                            onLoadAdSuccess(true,node,iAd, index);
                    } else {
                        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->不回调，直接存储数据" + "node_id" + node.slot_id);
                            CacheManager.getInstance().saveCache(node.slot_id, index, iAd.getNativeAd());
                    }

                } else {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->回调成功，但是没有数据返回" + "node_id" + node.slot_id);
                }

            }

            @Override
            public void onFlowLoadFailed(AdNode node, int index, AdError adError,int platformType) {
                //TODO: 1. remove isCallBack 2. only onLoadAdFailed when all flow no return 3.timeout for each flow
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_FLOW_FAILED_BY_LEVEL + index +
                        AdConstants.LOGIC_LOG+"nodeId:"+node.slot_id);
//                onLoadAdFailed(true,node, adError);
                //当最低级别的上一级请求结束，就不进行请求。
                if (lowestRequestLevel - 1 <= index) {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->广告层级加载结束，没有加载到广告。" + "node_id" + node.slot_id);
                    return;
                }
                if (index >= node.flow.size() - 1 && isCallBack) {
                    onLoadAdFailed(true,node, new AdError(node.slot_id, AdConstants.ALL_AD_IS_NOT_FILL));
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->加载失败" + "node_id" + node.slot_id);
                    return;
                }
                loadFlow("",0,null,node, index + 1, !isCallBack,this);
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->加载失败,进行下一个广告位加载" + "node_id" + node.slot_id);
            }

        });

    }

    /**
     * load ad from cache (cache priority ;flow priority)
     *
     * @param node
     * @param parentView
     */
    @Override
    public void loadAdByCache(final AdNode node, final ViewGroup parentView) {
        if (node == null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "Cache加载-->广告数据为null-->return");
            return;
        }
        if (node != null && !node.open_status) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "Cache加载-->广告数据为null-->return" + "nodeId:" + node.slot_id);
            return;
        }
        mAdNode = node;
        mParentView = parentView;

        if (node.show_priority == 1) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "Cache加载-->show_priority == 1-->cache策略" + "node_id" + node.slot_id);
            doCachePriority(node);
        } else {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "Cache加载-->show_priority == 2-->flow策略" + "node_id" + node.slot_id);
            doFlowPriority(node, 0);
        }
    }

    /**
     *
     * @param packageName
     * @param adNums
     * @param node
     * @param parentView
     */
    public void loadAdsByFb(final String packageName, final int adNums, final AdNode node, final ViewGroup parentView) {
        if (node == null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "loadAdsByFb加载-->广告数据为null-->return");
            return;
        }
        if (node != null && !node.open_status) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "loadAdsByFb加载-->广告数据为null-->return" + "nodeId:" + node.slot_id);
            return;
        }
        mAdNode = node;
        mParentView = parentView;

        loadFlow(packageName,adNums,null,node, 0, true,new OnFlowFinished() {
            @Override
            public void onFlowLoadSuccess(AdNode node, IAd iAd, int index) {
                if (iAd != null && iAd.getNativeAd() != null) {
                    MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_AD_IN_GROUP_SUCCESS + index +
                            AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "刷新缓存-->广告加载成功");
                        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->需要回调，通知用户加载成功" + "node_id" + node.slot_id);
                            saveAd(node,index,iAd.getNativeAd());
                            onLoadAdSuccess(true,node,iAd, index);
                } else {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->回调成功，但是没有数据返回" + "node_id" + node.slot_id);
                }

            }

            @Override
            public void onFlowLoadFailed(AdNode node, int index, AdError adError,int platformType) {
                //TODO: 1. remove isCallBack 2. only onLoadAdFailed when all flow no return 3.timeout for each flow
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_AD_IN_GROUP_FAILED + index +
                        AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "刷新缓存-->广告加载成功");

                if (index >= node.flow.size() - 1 ) {
                    onLoadAdFailed(true,node, new AdError(node.slot_id, AdConstants.ALL_AD_IS_NOT_FILL));
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->加载失败" + "node_id" + node.slot_id);
                    return;
                }
                loadFlow(packageName,adNums,null,node, index + 1,true,this);
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "刷新缓存-->加载失败,进行下一个广告位加载" + "node_id" + node.slot_id);
            }

        });
    }

    /**
     * load ad by cache priority(load all cache -->load first flow -->load second flow...)
     * load cache is success still to load flow one time
     * if load flow is success return
     *
     * @param node
     */
    public void doCachePriority(AdNode node) {
        //load all cache
        NativeAdData adResult = null;
        int cacheIndex = -1;
        for (int index = 0; index < node.flow.size(); index++) {
            NativeAdData ad = CacheManager.getInstance().getCacheInFirstIndex(node, index);
            if (ad != null) {
                adResult = ad;
                cacheIndex = index;
                MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG + "nodeId:" + node.slot_id + "缓存:cache策略--> id ：" + node.slot_id + "第" + cacheIndex + "层flow");
                break;
            }
        }

        if (adResult != null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:cache策略-->拿到缓存了，进行成功的处理-->逻辑结束");
            onLoadAdSuccess(false, node, adResult, cacheIndex);
            return ;
        } else {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:cache策略-->当前没有缓存-->进行实时请求");
        }
        //load flow
        loadFlow("",0,null,node, 0, false,new OnFlowFinished() {

            @Override
            public void onFlowLoadSuccess(AdNode node, IAd iAd, int index) {
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_FLOW_SUCCESSED_BY_LEVEL + index +
                        "nodeId:" + node.slot_id + "缓存:cache策略-->实时请求成功-->进行成功处理");
                //native 广告
                    saveAd(node,index,iAd.getNativeAd());
                    onLoadAdSuccess(false,node,iAd, index);
            }

            @Override
            public void onFlowLoadFailed(AdNode node, int index, AdError adError,int platformType) {
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_FLOW_FAILED_BY_LEVEL + index +
                        AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:cache策略-->实时请求失败-->进行错误的处理");                onLoadAdFailed(false,node, adError);
                if (index >= node.flow.size() - 1) {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:cache策略-->实时请求失败-->广告加载结束");
                    onLoadAdFailed(false,node, new AdError(node.slot_id, AdConstants.ALL_AD_IS_NOT_FILL));
                    return;
                }
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:cache策略-->实时请求失败-->加载下一层Flow");
                loadFlow("",0,null,node, index + 1, false,this);
            }

        });
    }

    /**
     * load ad  by flow priority(load first cache-->load first flow -->load second cache
     * -->load second flow.....)
     * <p>
     * load cache is success still to load flow one time
     * if load flow is success return
     *
     * @param node
     * @param cacheIndex cache level index
     */
    public void doFlowPriority(AdNode node, int cacheIndex) {
        //load cache
        NativeAdData adResult = CacheManager.getInstance().getCacheInFirstIndex(node, cacheIndex);

        if (adResult != null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:flow策略-->拿到缓存-->进行成功处理-->逻辑加载结束");
            onLoadAdSuccess(false,node, adResult, cacheIndex);
            return;
        } else {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:flow策略-->没有缓存-->进行下一flow的实时加载");
        }

        //load flow
        loadFlow("",0,null,node, cacheIndex,false, new OnFlowFinished() {

            @Override
            public void onFlowLoadSuccess(AdNode node, IAd iAd, int index) {
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_FLOW_SUCCESSED_BY_LEVEL + index +
                        AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:flow策略-->实时加载成功-->进行成功处理");
                //native 广告
                    saveAd(node,index,iAd.getNativeAd());
                    onLoadAdSuccess(false,node,iAd, index);
            }

            @Override
            public void onFlowLoadFailed(AdNode node, int index, AdError adError,int platformType) {
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_FLOW_FAILED_BY_LEVEL + index +
                        AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:flow策略-->实时加载失败-->进行失败处理");
                onLoadAdFailed(false,node, adError);
                if (index >= node.flow.size() - 1) {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:flow策略-->实时加载失败-->广告加载结束");
                    onLoadAdFailed(false,node, new AdError(node.slot_id, AdConstants.ALL_AD_IS_NOT_FILL));
                    return;
                }
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "缓存:flow策略-->实时加载失败-->加载下一个缓存");
                doFlowPriority(node, index + 1);
            }

        });

    }

    /**
     * load flow data from net
     *
     * @param node
     * @param index
     * @param finishedListener
     */
    private void loadFlow(String packageName,int nums,ArrayList<ViewGroup> layouts, AdNode node, int index,boolean isRefreshCache, OnFlowFinished finishedListener) {
        if (node == null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "node == null --> return");
            return;
        }

        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "加载Flow-->nodeId" + node.slot_id);
        if (index >= node.flow.size()) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "flow == null--> return");
            return;
        }
        Flow flow = getFlowFromFlowSArray(node, index);
        if (flow == null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "nodeId:" + node.slot_id + "flow == null--> return");
            return;
        }

        switch (flow.platform) {
            case AdConstants.ADMOB:

                if (NATIVE.equals(flow.type)) {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "加载admob的高级广告" + "   Ad id:" + node.slot_id + " Ad name:" + node.slot_name);
                    //如果当前没有webView则不加载admob native 的广告，为了规避webView升级造成的一个系统bug.
                    try {
                        mContext.getPackageManager().getApplicationInfo("com.google.android.webview", 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "当前广告包不包含webView,加载下一层广告。" + "node_id" + node.slot_id);
                        loadFlow("", 0, layouts, node, index + 1, false, finishedListener);
                        return;
                    }
                    mAdAdapter = new AdmobNativeAdAdapter(mContext, node);
                } else if (NATIVE_EXPRESS.equals(flow.type) || BANNER.equals(flow.type) || INTERSTITIAL.equals(flow.type)) {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "加载非高级原生广告" + "   Ad id:" + node.slot_id + " Ad name:" + node.slot_name);
                    mAdAdapter = new AdmobAdAdapter(mContext, node);
                } else {
                    MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "admob的type类型配置有误！");
                }
                break;

            case AdConstants.MOPUB:
                mAdAdapter = new MopubAdAdapter(mContext, node);
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "加载mopub广告" + "   Ad id:" + node.slot_id + " Ad name:" + node.slot_name);
                break;
            case AdConstants.YAHOO:
                mAdAdapter = new FlurryNativeAdAdapter(mContext, node);
                break;
            case AdConstants.FB:
                if (StringUtil.isEmpty(packageName)) {
                    if (layouts != null) {
                        mAdAdapter = new FacebookAdAdapter(mContext, node, nums, layouts);
                    } else {
                        mAdAdapter = new FacebookAdAdapter(mContext, node);
                    }
                } else {
                    mAdAdapter = new FacebookAdAdapter(mContext, node, nums, packageName);
                }
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "加载Fb的广告" + "   Ad id:" + node.slot_id + " Ad name:" + node.slot_name);
                break;
            default:
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "没有对应的广告平台");
                return;
        }
        initFlowListener(mAdAdapter, node, finishedListener, index);
        try {
            mAdAdapter.loadAd(index, flow);
        } catch (Exception e) {
            MyLog.d(MyLog.TAG, "Error happen when load ad : " + flow.platform);
            MyLog.d(MyLog.TAG, e.toString());
            if (finishedListener != null && mAdAdapter != null) {
                finishedListener.onFlowLoadFailed(node, index, new AdError(node.slot_id, AdErrorType.INVALID_REQUEST), mAdAdapter.getAdType());
            }
        }
    }

    /**
     * 从当前的层级中获取一个随机权重的广告的数据
     *
     * @param adNode
     * @param index
     * @return
     */
    public Flow getFlowFromFlowSArray(AdNode adNode, int index) {
        if (index > adNode.flow.size() - 1) {
            return null;
        }
        ArrayList<Flow> flows = adNode.flow.get(index);
        if (flows == null) {
            return null;
        }
        if (flows.size() == 0) {
            return null;
        }

        Flow flow = null;
        int flowSize = flows.size();
        int maxRandomNum = 0;

        for (int i = 0; i < flowSize; i++) {
            maxRandomNum += flows.get(i).weight;
        }
        //如果所有的广告都没有配置weight，则默认改为10，进行取值。
        if (maxRandomNum == 0) {
            for (int i = 0; i < flowSize; i++) {
                flows.get(i).weight = 10;
                maxRandomNum += flows.get(i).weight;
            }
        }
        MyLog.d(MyLog.TAG, "random   maxRandomNum:" + maxRandomNum + "   index:" + index);
        int randomNum = AdUtils.getRandomNumberByMaxNum(maxRandomNum);
        MyLog.d(MyLog.TAG, "random   randomNum:" + randomNum + "   index:" + index);
        int currentWeight = 0;

        for (int j = 0; j < flowSize; j++) {
            currentWeight += flows.get(j).weight;
            if (randomNum <= currentWeight) {
                flow = flows.get(j);
                MyLog.d(MyLog.TAG, "random   权重选择了platform:" + flow.platform +
                        "---key:" + flow.key + "   index:" + index);
                return flow;
            }
        }

        return flow;
    }

    /**
     * init listener for Adapter
     *
     * @param adapter
     * @param node
     * @param flowFinshLinstener
     * @param index
     */
    private void initFlowListener(final AdAdapter adapter, final AdNode node, final OnFlowFinished
            flowFinshLinstener, final int index) {
        MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "初始化回调的接口");
        if(mAdAdapter == null){
            return;
        }
        adapter.setAdListener(new OnAdInnerLoadListener() {

            @Override
            public void onBannerLoad(IAd iAd) {

                //返回了banner广告
                if(iAd.getAdView() != null){
                    onBannerLoadAdSuccess(node,iAd,index);
                    return;
                }
            }

            @Override
            public void onLoad(IAd iAd) {
                if (iAd == null) {
                    MyLog.e(MyLog.TAG, "回调的Iad为空，请检查 " + "nodeid = " + node.slot_id);
                } else {
                    if (iAd.getNativeAd() == null) {
                        MyLog.e(MyLog.TAG, "回调的Iad不为空 NativeAd为空" + "nodeid = " + node.slot_id);
                    } else {
                        MyLog.e(MyLog.TAG, "回调的Iad不为空 NativeAd也不为空" + "nodeid = " + node.slot_id);
                    }
                }
                flowFinshLinstener.onFlowLoadSuccess(node, iAd, index);
            }

            @Override
            public void onLoadFailed(AdError error) {
                MyLog.e(MyLog.TAG, "回调的接口失败了" + "nodeid = " + node.slot_id);
                //如果错误码为“FB_NO_MATCH_ADS”则直接回调接口给上层，不做任何其他处理。
                if(AdConstants.FB_NO_MATCH_ADS.equals(error.adError)){
                    adLoadListener.onLoadFailed(error);
                    return;
                }
                flowFinshLinstener.onFlowLoadFailed(node, index, error,adapter.getAdType());

            }

            @Override
            public void onLoadInterstitialAd(WrapInterstitialAd ad) {
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_FILL_FULL_AD_FAILED +
                        "    Ad id:" + node.slot_id);
                if (adLoadListener != null) {
                    adLoadListener.onLoadInterstitialAd(ad);
                    releaseResource();
                }
            }
        });
    }

    //banner load success
    private void onBannerLoadAdSuccess(AdNode adNode, IAd iAd, int index) {
        MyLog.d(MyLog.TAG,"vincent-warn:onBanner--LoadAd--Success");
        if (mIsCallBack) {
            //Already callbacked
            return;
        }
        if (mParentView != null) {
            View adView = iAd.getAdView();
            if (adView != null) {
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_HAD_PARENT_AND_HAD_AD_VIEW_PUSH +
                        "    Ad id:" + adNode.slot_id);
                mParentView.removeAllViews();
                mParentView.addView(adView);
                MyLog.d(MyLog.TAG, "获取广告的View成功，广告已经填充到父容器，请User将父容器展示");
                dotFlowLoadSuccess(mNativeAd, adNode);
            } else {
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_HAD_PARENT_AND_NO_AD_VIEW +
                        "    Ad id:" + adNode.slot_id);
                MyLog.d(MyLog.TAG, "获取广告的View失败");
            }
        }
        if (adLoadListener != null) {
            adLoadListener.onLoad(iAd);
        }

    }

    //Callback and render view if need
    private void onLoadAdSuccess(boolean isPreload, AdNode adNode, Object ad, int index) {
        if (mIsCallBack) {
            //Already callbacked
            MyLog.d(MyLog.TAG, "Already callbacked");
            return;
        }
        if (ad instanceof IAd) {
            mNativeAd = ((IAd) ad).getNativeAd();
            mFlow = ((IAd) ad).getFlow();
            mAdPackageName = ((IAd) ad).getAdPackageName();
            if(isPreload){
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_GET_PRELOAD_RESOURCE_FROM_NET +
                        "    Ad id:" + adNode.slot_id);
            }else{
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_GET_RESOURCE_FROM_NET +
                        "    Ad id:" + adNode.slot_id);
            }
        } else if (ad instanceof NativeAdData) {
            mNativeAd = (NativeAdData) ad;
            mFlow = mNativeAd.mFlow;
            MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_GET_RESOURCE_FROM_CACHE +
                    "    Ad id:" + adNode.slot_id);
        } else {
            MyLog.d(MyLog.TAG, "Wrong arguments in onLoadAdSuccess");
            MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_GET_RESOURCE_FROM_NONE +
                    "    Ad id:" + adNode.slot_id);
            return;
        }
        ViewGroup viewGroup =  mParentView;

        if(mParentsViewGroup != null){
            viewGroup = mParentsViewGroup.get(mViewGroupIndex);
            mViewGroupIndex++;
        }
        if (viewGroup != null) {
            MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_HAD_PARENT_AND_PUSH +
                    "    Ad id:" + adNode.slot_id);
            IAd iAd = (ad instanceof IAd) ? (IAd) ad : null;
            View adView = getAdView(mFlow, iAd, mNativeAd, viewGroup);
            if (adView != null) {
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_HAD_PARENT_AND_HAD_AD_VIEW_PUSH +
                        "    Ad id:" + adNode.slot_id);
                viewGroup.removeAllViews();
                viewGroup.addView(adView);
                MyLog.d(MyLog.TAG, "获取广告的View成功，广告已经填充到父容器，请User将父容器展示");
                registerViewForInteraction(adView);
                addShowTime(adNode.slot_id, adNode.frequency, adNode.daily_times);
                MyLog.e(MyLog.TAG, "add show time  index:" + index);
                if (mNativeAd != null) {
                    mNativeAd.setIsShowed();
                } else {
                    MyLog.d(MyLog.TAG, "数据为空，没有修改数据状态");
                }
                dotFlowLoadSuccess(mNativeAd, adNode);
            } else {
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_HAD_PARENT_AND_NO_AD_VIEW +
                        "    Ad id:" + adNode.slot_id);
                MyLog.d(MyLog.TAG, "获取广告的View失败，请检查当前广告位配置的广告Style是否正确");
            }
        } else {
            MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_NO_PARENT_APP_PUSH +
                    "    Ad id:" + adNode.slot_id);
        }

        if (adLoadListener != null) {
            if(isPreload){
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_LOAD_AD_PRELOAD_SUCCESS_CALLBACK +
                        "    Ad id:" + adNode.slot_id);
            }else {
                MyLog.d(MyLog.TAG, adNode.slot_name + "_" + AdEventConstants.AD_LOAD_AD_SUCCESS_CALLBACK +
                        "    Ad id:" + adNode.slot_id);
            }
            adLoadListener.onLoad(this);
        }

        if(mParentView !=null&&mParentsViewGroup == null||
                mParentsViewGroup!=null&&mViewGroupIndex == mParentsViewGroup.size()){

            releaseResource();
            mIsCallBack = true;
        }


    }

    //Save to cache
    private void saveAd(AdNode adNode, int index, NativeAdData adData) {
        MyLog.e(MyLog.TAG, "save AD index: " + index);
        CacheManager.getInstance().saveCache(adNode.slot_id, index, adData);
        CacheManager.getInstance().sortTheCache(adNode.slot_id, adNode.cache_size);
    }

    /**
     * load ad failed,dot this.
     */
    private void onLoadAdFailed(boolean isPreload,AdNode node, AdError adError) {
        if (adError == null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "未知错误，无法进行广告失败的回调");
            return;
        }
        if (AdConstants.ALL_AD_IS_NOT_FILL.equals(adError.adError) && !mIsCallBack) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "进行广告失败的回调");
            if (adLoadListener != null) {
                if(isPreload){
                    MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_AD_PRELOAD_FAILED_NET_CALLBACK +
                            "    Ad id:" + node.slot_id);
                }else{
                    MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_LOAD_AD_FAILED_NET_CALLBACK +
                            "    Ad id:" + node.slot_id);
                }
                adLoadListener.onLoadFailed(adError);
            }
            releaseResource();
            mIsCallBack = true;
        } else {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "不符合回调条件，无法进行广告失败的回调");
        }
//        DotAdEventsManager.getInstance(mContext).sendEvent(AdEventConstants.AD_FILL_AD_FAILED + "_" + node.slot_name, "    Ad id:" + node.slot_id +"adError:"+adError.adError);
    }

    /**
     * @param flow
     * @param iAd          if add from flow use this
     * @param nativeAdData if add from cache use this
     * @param parent
     * @return native type get view by data,banner type get view from iAd
     */
    private View getAdView(Flow flow, IAd iAd, NativeAdData nativeAdData, ViewGroup parent) {
        if (flow == null) {
            MyLog.w(MyLog.TAG, "platform FlowAdManger back data is null");
            return null;
        }

        if (!SharePUtil.getString(mContext,mAdNode.slot_id+mAdNode.slot_name,"").equals("")){
            flow.native_style = Integer.valueOf(SharePUtil.getString(mContext,mAdNode.slot_id+mAdNode.slot_name,""));
        }


        View adView = null;

        switch (flow.platform) {
            case AdConstants.FB:
            case AdConstants.VK:
            case AdConstants.YAHOO:
                mNativeAdView = NativeAdViewManager.createAdView();
                adView = NativeAdViewManager.loadAdView(mContext, mNativeAdView, flow.native_style, nativeAdData, parent);
                break;
            case AdConstants.ADMOB:
                if (flow.admob_type == AdmobNativeAdAdapter.ADMOB_ADVANCE_APP_TYPE || flow.admob_type == AdmobNativeAdAdapter.ADMOB_ADVANCE_CONTENT_TYPE ||
                        flow.admob_type == AdmobNativeAdAdapter.ADMOB_ADVANCE_ECPM_FRIST) {
                    adView = NativeAdViewManager.loadAdView(mContext, null, flow.native_style, nativeAdData, parent);
                } else {
                    if (iAd != null) {
                        adView = iAd.getAdView();
                    }
                }
                break;
            case AdConstants.MOPUB:
                    adView = NativeAdViewManager.loadAdView(mContext, null, flow.native_style, nativeAdData, parent);
                break;
            default:
                MyLog.e(MyLog.TAG, "no this ad platform");
                break;

        }

        return adView;
    }

    /**
     * dot load success through platform
     *
     * @param nativeAdData native type
     * @param node
     */
    private void dotFlowLoadSuccess(NativeAdData nativeAdData, AdNode node) {
        if (mNativeAd == null) {
            MyLog.d(MyLog.TAG, "没有当前数据，nodeId:" + node.slot_id);
            return;
        }
        switch (nativeAdData.getAdType()) {
            case CacheManager.FB:
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_SHOW_FACEBOOK_AD_NATIVE_SHOW +
                        "_session:" + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() +
                        "    Ad id:" + node.slot_id);
                break;
            case CacheManager.ADMOB_APP:
                if (mNativeAd.getAdObject() instanceof NativeAppInstallAd){
                  if ( ((NativeAppInstallAd)mNativeAd.getAdObject()).getVideoController().hasVideoContent()){
                      MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_APP_CONTAINS_VIDEO_SHOW +
                              "_session:" + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() +
                              "    Ad id:" + node.slot_id);
                  }else {
                      MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_APP_SHOW + "_session:"
                              + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() + "    Ad id:" + node.slot_id);
                  }
                }
                break;
            case CacheManager.ADMOB_CONTENT:
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_CONTENT_SHOW + "_session:"
                        + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() + "    Ad id:" + node.slot_id);
                break;
            case CacheManager.MOPUB:
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_MOPUB_NATIVE_SHOW + "_session:"
                        + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() + "    Ad id:" + node.slot_id);
                break;
            case CacheManager.YH:
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.YAHOO_NATIVE_SHOW + "_session:"
                        + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() + "    Ad id:" + node.slot_id);
                break;
            default:
                MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_OTHER_IS_SHOWING + "_session:"
                        + mNativeAd.getSessionID() + "_title:" + mNativeAd.getTitle() + "    Ad id:" + node.slot_id);
                break;

        }


    }

    @Override
    public int getAdType() {
        if (mNativeAd != null) {
            return mNativeAd.getAdType();
        } else {
            return CacheManager.FB;
        }
    }

    public void setAdListener(OnAdLoadListener mOnAdLoadListener) {
        this.adLoadListener = mOnAdLoadListener;
    }

    @Override
    public View getAdView() {
        View backView = null;
        if (mNativeAd != null) {
            backView = getAdView(mFlow, null, mNativeAd, mParentView);
            registerViewForInteraction(backView);
            mNativeAd.setIsShowed();
        }
        if(backView!=null){
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_NO_PARENT_APP_PUSH_GET_VIEW +
                    "    Ad id:" + mAdNode.slot_id);
        }else{
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_NO_PARENT_APP_PUSH_GET_VIEW_NULL +
                    "    Ad id:" + mAdNode.slot_id);
        }
        return backView;
    }

    @Override
    public NativeAdData getNativeAd() {
        if(mNativeAd!=null){
            if(mNativeAd.mNode != null){
                MyLog.d(MyLog.TAG, mNativeAd.mNode.slot_name + "_" + AdEventConstants.AD_NO_PARENT_APP_PUSH_GET_DATA +
                        "    Ad id:" + mAdNode.slot_id);
            }else{
                MyLog.d(MyLog.TAG, AdEventConstants.AD_NO_PARENT_APP_PUSH_GET_DATA + "Ad id:is null");
            }
        }else{
            if(mAdNode != null){
                MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_NO_PARENT_APP_PUSH_GET_DATA_NULL +
                        "    Ad id:" + mAdNode.slot_id);
            }else{
                MyLog.d(MyLog.TAG, AdEventConstants.AD_NO_PARENT_APP_PUSH_GET_DATA_NULL + "Ad id:is null");
            }
        }
        return mNativeAd;
    }

    @Override
    public String getIconUrl() {
        if (mNativeAd != null) {
            return mNativeAd.getIconImageUrl();
        }
        return null;
    }

    public void setOnCancelAdListener(OnCancelAdListener listener) {
        if (mNativeAd != null) {
            mNativeAd.cancelListener = listener;
        }
        MyLog.d(MyLog.TAG, "setmOnCancelAdListener");
    }

    public void setOnAdClickListener(OnAdClickListener listener) {
        if (mNativeAd != null) {
            //缓存中添加的onAdClick事件
            MyLog.d("aalistener", "mNativeAd setmOnAdClickListener");
            mNativeAd.onAdClickListener = listener;
        }

    }

    @Override
    public void setOnAdTouchListener(View.OnTouchListener listener) {
        if (mNativeAd != null && getAdType() == CacheManager.FB) {
            mNativeAd.setAdTouchListener(listener);
            MyLog.d(MyLog.TAG, "setOnAdTouchListener");
        }
    }

    @Override
    public void setOnPrivacyIconClickListener(View.OnClickListener listener) {
        if (mNativeAd != null && getAdType() == CacheManager.FB) {
            mNativeAd.privacyIconClickListener = listener;
            MyLog.d(MyLog.TAG, "setonPrivacyIconClickListener");
        }
    }

    @Override
    public void registerViewForInteraction(View view) {
        if (view == null || mFlow == null || mNativeAd == null) {
            return;
        }
        if (getAdType() == CacheManager.FB) {
            if (mFlow.ad_clcik_enable == 2) {
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "FB 注册广告元素的监听");
                ((FbNativeAdData) mNativeAd).registerViewForInteraction(view, mNativeAdView.getAllAdElementView());
            } else if (mFlow.ad_clcik_enable == 3) {
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "FB 注册广告CallToAction的监听");
                mNativeAd.registerViewForInteraction(mNativeAdView.getActionAdElementView(), mNativeAdView.getActionAdElementView());
            } else {
                MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "FB 注册广告View的监听");
                mNativeAd.registerViewForInteraction(view, view);
            }
        } else if (getAdType() == CacheManager.YH) {
            mNativeAd.registerViewForInteraction(view, null);
        }
    }

    @Override
    public AdNode getAdNode() {
        return mAdNode;
    }

    /**
     * add ad show count
     *
     * @param slotId
     * @param mHoursOneTime
     * @param mDayTimes
     */
    private void addShowTime(String slotId, float mHoursOneTime, float mDayTimes) {
        AdLimitAdInfo adLimitAdInfo = mTimeLimitAdGeneratorMap.get(slotId);
        if (adLimitAdInfo == null) {
            adLimitAdInfo = new AdLimitAdInfo();
            mTimeLimitAdGeneratorMap.put(slotId, adLimitAdInfo);
            adLimitAdInfo.init(mContext, mHoursOneTime, mDayTimes, slotId);
        }
        adLimitAdInfo.addShowtime();
    }

    @Override
    public void showCustomAdView() {
        NativeAdData nativeAdData = getNativeAd();
        if (nativeAdData != null) {
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.SHOW_PLATFROM_THING[getNativeAd().getAdType()] +
                    "  Ad id:" + mAdNode.slot_id + "Ad title:" + getNativeAd().getTitle() +
                    " SessionId:" + getNativeAd().getSessionID());
            getNativeAd().setIsShowed();
        }
    }

    @Override
    public String getAdPackageName() {
        return mAdPackageName;
    }

    @Override
    public Flow getFlow() {
        if(mAdAdapter!=null){
            return mAdAdapter.getFlow();
        }
        return null;
    }

    public void releaseResource() {
        adLoadListener = null;
        mParentView = null;
        mAdAdapter = null;
        mNativeAdView = null;
        mNativeAd = null;
        CacheManager.getInstance().sortTheCache(getAdNode().slot_id,getAdNode().cache_size);
    }

    @Override
    public void release(ViewGroup viewGroup) {
        if (viewGroup != null && viewGroup.findViewById(R.id.ad_fb_cover_image) != null){
            MediaView mediaView = (MediaView) viewGroup.findViewById(R.id.ad_fb_cover_image);
            if (mediaView != null){
                mediaView.removeAllViews();
                MyLog.d(MyLog.TAG," release mediaview content");
            }
        }

        releaseResource();
        MyLog.d(MyLog.TAG,"FlowAdManager release 释放资源");
        //当ACTIVITY 退出的时候掉用这个方法的时候  实时请求的广告 还没返回，
        // 当放回的时候又会渲染view 此时的view 只会停留在内存里面 不会被回收···
        mIsCallBack=true;

    }

    interface OnFlowFinished {
        void onFlowLoadSuccess(AdNode node, IAd iAd, int index);

        void onFlowLoadFailed(AdNode node, int index, AdError adError, int platformType);

    }
}

