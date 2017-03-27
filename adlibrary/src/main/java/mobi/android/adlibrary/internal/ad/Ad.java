package mobi.android.adlibrary.internal.ad;

import android.content.Context;
import android.view.ViewGroup;

import mobi.android.adlibrary.internal.ad.adapter.FlowAdManager;
import mobi.android.adlibrary.internal.ad.bean.AdLimitAdInfo;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.ad.nativeview.NativeAdViewManager;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.SharePUtil;

/**
 * Created by vincent on 16/5/4.
 */
public class Ad {

    public String adId;
    private FlowAdManager mAdAdapterManager;
    private AdNode mAdNode;
    private Context mContext;
    private ViewGroup viewGroup;
    private boolean mIsPreLoad;
    private int mLayoutIDKey;
    private int mLayoutID;

    public Ad(Context context, String adId) {
        this.mContext = context.getApplicationContext();
        this.adId = adId;
        mAdNode = AdConfigLoader.getInstance(mContext).getAdNodeByAdId(adId);
        mAdAdapterManager = new FlowAdManager(mContext);
        if (mAdNode != null) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "初始化广告的数据node-id:" + mAdNode.slot_id + "   hashCode:" +
                    mAdAdapterManager.hashCode());
        }
    }

    /**
     * 判断当前广告的展示是否超过了对应的要求（一天展示几次广告，多少时间展示一次广告）
     *
     * @return true 可以正常展示
     */
    public static boolean canShowByLimitTime(Context context, AdNode node) {
        boolean isLoad = true;
        float hoursOneTime = node.frequency;
        float oneDayTimes = node.daily_times;
        if (hoursOneTime > 0 && oneDayTimes > 0) {
            isLoad = getAdLimitByTime(context, node.slot_id, hoursOneTime, oneDayTimes);
        }
        if (isLoad) {
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "满足limit的请求条件。");
        } else {
            MyLog.e(MyLog.TAG, AdConstants.LOGIC_LOG + "不满足limit的请求条件，展示次数过多。");
        }
        return isLoad;
    }

    /**
     * According limit time ，judge load or not
     *
     * @param slotId
     * @param mHoursOneTime
     * @param mDayTimes
     * @return
     */
    private static boolean getAdLimitByTime(Context context, String slotId, float mHoursOneTime, float mDayTimes) {

        AdLimitAdInfo adLimitAdInfo = new AdLimitAdInfo();
        adLimitAdInfo.init(context, mHoursOneTime, mDayTimes, slotId);
        return adLimitAdInfo.getAdSuccessedByLimit();
    }

    public void loadAd() {
        if (mAdNode != null) {
            MyLog.d(MyLog.TAG, "getAdNodeByAdId:" + mAdNode.slot_id);
            MyLog.d(MyLog.TAG, AdConstants.LOGIC_LOG + "广告数据不为空，请求广告:" + "nodeId:" +
                    mAdNode.slot_id + "slotName:" + mAdNode.slot_name);
            //第一次请求flow的时候做几率请求
            if (!canShowByLimitTime(mContext, mAdNode)) {
                MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_LOAD_AD_NO_PASS_LIMIT_CONDITION +
                        "限制条件没有通过，此次不请求广告！" + "    Ad id:" + mAdNode.slot_id +
                        "  slotName:" + mAdNode.slot_name);
                return;
            }
            if (mIsPreLoad) {
                MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_LOAD_AD_BY_PRELOAD +
                        "用户预加载，进行网络直接请求" + "    Ad id:" + mAdNode.slot_id +
                        "  slotName:" + mAdNode.slot_name);
                //如果用户是预加载则直接调用request的请求，并提供回调
                boolean isHadCache = CacheManager.getInstance().isHaveNativeCache(mAdNode);
                if (!isHadCache) {
                    mAdAdapterManager.loadAdOnlyRequest(mAdNode, mAdNode.flow.size(), true, viewGroup);
                }
            } else {
                //如果用户不是预加载，则从缓存读取
                MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_LOAD_AD_BY_CACHE_LOGIC +
                        "请求缓存数据，按照缓存策略执行" + "    Ad id:" + mAdNode.slot_id + " slotName:" + mAdNode.slot_name);
                mAdAdapterManager.loadAdByCache(mAdNode, viewGroup);
            }

        } else {
            MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_IS_NULL + AdConstants.LOGIC_LOG +
                    "广告的数据为空，没有检测到当前的slotId!");
        }
    }

    public AdNode getAdNode() {
        return mAdNode;
    }

    public static class Builder {
        private final Context mContext;
        private Ad mAd;

        public Builder(Context context, String adId) {
            this.mContext = context.getApplicationContext();
            mAd = new Ad(mContext, adId);
        }

        public Builder(Context context, Ad mAd) {
            this.mContext = context.getApplicationContext();
            this.mAd = mAd;
        }

        //TODO note this params must safe,it's not safe now,modify it!
        public Builder setAppSelfLayout(int LayoutID) {
            mAd.mLayoutID = LayoutID;
            mAd.mLayoutIDKey = LayoutID;
            NativeAdViewManager.addLayout(LayoutID, LayoutID);
            if (mAd.mAdNode != null && mAd.mAdNode.flow != null) {
                for (int i = 0; i < mAd.mAdNode.flow.size(); i++) {
                    for (int j = 0; j < mAd.mAdNode.flow.get(i).size(); j++) {
                        MyLog.w(MyLog.TAG, " if you use this method,  you'll take very special care of native ad style !!!!" +
                                "set every flow native " + LayoutID);
                        if (LayoutID > 100) {
                            mAd.mAdNode.flow.get(i).get(j).native_style = LayoutID;
                            SharePUtil.putString(mContext, mAd.mAdNode.slot_id + mAd.mAdNode.slot_name, String.valueOf(LayoutID));
                        } else {
                            MyLog.e(MyLog.TAG, "AdSDK：   LayoutID   error");
                        }

                    }
                }
            }
            return this;
        }

        public Builder setParentViewGroup(ViewGroup viewGroup) {
            mAd.viewGroup = viewGroup;
            return this;
        }

        public Builder setAdPlacementConfig(AdNode adNode) {
            mAd.mAdNode = adNode;
            return this;
        }

        public Builder setWidth(int width) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.width = width;
            }
            return this;
        }

        public Builder isPreLoad(boolean isPreLoad) {
            if (mAd != null && mAd.mAdAdapterManager != null) {
                mAd.mIsPreLoad = isPreLoad;
            }
            return this;
        }

//        public Builder isNotCacheNextAd(boolean isNotCache) {
//            if (mAd != null && mAd.mAdNode != null) {
//                mAd.mAdNode.isNotCache = isNotCache;
//            }
//            return this;
//        }

        public Builder setTitleColor(int titleColor) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.mTilteColor = titleColor;
            }
            return this;
        }

        public Builder setSubTitleColor(int subTitleColor) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.mSubTitleColor = subTitleColor;
            }
            return this;
        }

        public Builder setCtaTextColor(int buttonColor) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.mButtonColor = buttonColor;
            }
            return this;
        }

        public Builder setHight(int hight) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.height = hight;
            }
            return this;
        }

        public Builder setCtaBackground(int buttonBackground) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.ctaBackground = buttonBackground;
            }
            return this;
        }

        public Builder setTransparent(boolean transparent) {
            if (mAd != null && mAd.mAdNode != null) {
                mAd.mAdNode.transparent = transparent;
            }
            return this;
        }


        public Builder setOnAdLoadListener(OnAdLoadListener onAdLoadListener) {
            mAd.mAdAdapterManager.setAdListener(onAdLoadListener);
            return this;
        }

        public Ad build() {
            return mAd;
        }
    }
}
