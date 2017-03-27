package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.OnCancelAdListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.ad.nativeview.FbNativeAdData;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.AdUtils;
import mobi.android.adlibrary.internal.utils.DeviceUtil;
import mobi.android.adlibrary.internal.utils.FileUtil;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.StringUtil;

/**
 *  Created by vincent on 16/5/4.
 */
public class FacebookAdAdapter extends AdAdapter {
    public final static String TAG = FacebookAdAdapter.class.getName();
    private NativeAdsManager mNativeAdsManager;
    private InterstitialAd mInterstitialAd;
    private NativeAd mNativeAd;
    private Context mContext;
    private FbNativeAdData mFbNativeAdData;
    private String mNativeSession;
    private String mFullscreenSession;
    private Flow mFlow;
    private AdNode mNode;
    private OnCancelAdListener mOnCancelAdListener;
    private InterstitialAdListener mInterstitialAdListener;
    private AdListener mAdListener;
    private String mPackageName;
    private ArrayList<ViewGroup> mLayouts;
    private int mNums;
    private String mAdPackageName;

    public FacebookAdAdapter(Context context, final AdNode node) {
        super(context);
        this.mNode = node;
        this.mContext = context.getApplicationContext();
    }

    public FacebookAdAdapter(Context context, final AdNode node, int nums, String packageName) {
        super(context);
        this.mNode = node;
        this.mContext = context.getApplicationContext();
        mPackageName = packageName;
        mNums = nums;
        AdSettings.addTestDevice("4042bbf99cf72c157fa88865d9db1e1f");
    }


    public FacebookAdAdapter(Context context, final AdNode node, int nums, ArrayList<ViewGroup> layouts) {
        super(context);
        AdSettings.addTestDevice("4042bbf99cf72c157fa88865d9db1e1f");
        this.mNode = node;
        this.mContext = context.getApplicationContext();
        mLayouts = layouts;
        mNums = nums;
    }

    /**
     * load ad by flow from net
     *
     * @param index
     */
    public void loadAd(final int index, Flow flow) {
        MyLog.d(MyLog.TAG, "facebook ad start load " + "   Ad id:" + mNode.slot_id + " Ad name:" + mNode.slot_name);
        mFlow = flow;
        initAdListener(flow, index);
        if (flow.type.equals(INTERSTITIAL)) {
            MyLog.d(MyLog.TAG, "facebook ad start load INTERSTITIAL" + "   Ad id:" + mNode.slot_id + " Ad name:" + mNode.slot_name);
            if (mInterstitialAd == null) {
                mInterstitialAd = new InterstitialAd(mContext, flow.key);
            }
            mFullscreenSession = UUID.randomUUID().toString();
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_REQUEST_FULL_SCREEN + "  Ad id:" + mNode.slot_id + "sessionId" + mFullscreenSession);

            mInterstitialAd.setAdListener(mInterstitialAdListener);
            mInterstitialAd.loadAd();
        } else if (flow.type.equals(NATIVE)) {
            MyLog.d(MyLog.TAG, "facebook adapter start loadAd NATIVE" + "   Ad id:" + mNode.slot_id + " Ad name:" + mNode.slot_name);
            if (mNums == 0) {
                if (mNativeAd == null) {
                    mNativeAd = new NativeAd(mContext, flow.key);
                }
                mNativeSession = UUID.randomUUID().toString();
                MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_REQUEST_FACEBOOK_NATIVE_AD + "    Ad id:" + mNode.slot_id + "Ad title:" + "  SesseionId:" + mNativeSession);
                mNativeAd.setImpressionListener(new ImpressionListener() {
                    @Override
                    public void onLoggingImpression(Ad ad) {
                        MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_NATIVE_IMPRESSION + "  Ad id:" + mNode.slot_id + "sessionId" + mNativeSession);
                    }
                });
                mNativeAd.setAdListener(mAdListener);
                mNativeAd.loadAd();
            } else {
                MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_LOAD_AD_REQUEST_BY_PACKAGENAME + "    Ad id:" + mNode.slot_id + "Ad title:" + "  SesseionId:" + mNativeSession);
                mNativeAdsManager = new NativeAdsManager(mContext, flow.key, mNums);
                mNativeAdsManager.loadAds();
                mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
                    @Override
                    public void onAdsLoaded() {
                        if (!StringUtil.isEmpty(mPackageName)) {
                            getAdsByPackageName(mPackageName, index);
                        } else {
                            getAdsByNums(mLayouts, index);
                        }
                    }

                    @Override
                    public void onAdError(AdError adError) {
                        mobi.android.adlibrary.internal.ad.AdError error = new mobi.android.adlibrary.internal.ad.AdError();
                        error.adError = AdConstants.FB_NO_MATCH_ADS;
                        error.extraMessage = adError.getErrorMessage();
                        onAdLoadlistener.onLoadFailed(error);
                    }
                });
            }

        }
    }

    public void getAdsByNums(ArrayList<ViewGroup> layouts, int index) {
        NativeAd nativeAd = null;
        if (layouts == null) {
            return;
        }
        int i = 0;
        do {
            if (i >= layouts.size()) {
                return;
            }
            nativeAd = mNativeAdsManager.nextNativeAd();
            MyLog.d(MyLog.TAG, "vincent-ad" + nativeAd.getAdTitle());
            if (nativeAd != null) {
                createFbNativeDataAndCallBack(layouts.get(i), nativeAd, index);
            }
            i++;
        } while (nativeAd != null);
    }

    public void getAdsByPackageName(String adPackageName, int index) {
        NativeAd nativeAd = null;
        NativeAd noMatchRandomNative = null;
        int i = 0;
        int randomNum = AdUtils.getNumberByMaxNum(mNums);
        do {
            if (i >= mNums) {
                break;
            }
            nativeAd = mNativeAdsManager.nextNativeAd();
            if (nativeAd != null) {
                String packageName = FileUtil.getPackageName(nativeAd);
                if (randomNum == i) {
                    noMatchRandomNative = nativeAd;
                }
                MyLog.d(MyLog.TAG, "获取的广告的包名为：" + packageName);
                if (adPackageName.equals(packageName)) {
                    mAdPackageName = packageName;
                    createFbNativeDataAndCallBack(null, nativeAd, index);
                    MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.
                            AD_LOAD_AD_SUCCESS_BY_PACKAGENAME + "    Ad id:" + mNode.slot_id);
                    break;
                } else {
                    MyLog.d(MyLog.TAG, "没有碰到相同的广告包");
                    if (i == mNums - 1) {
                        mAdPackageName = packageName;
                        createFbNativeDataAndCallBack(null, noMatchRandomNative, index);
                        MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.
                                AD_LOAD_AD_FAILED_BY_PACKAGENAME + "    Ad id:" + mNode.slot_id);
                        break;
                    }
                }
            }
            i++;
        } while (nativeAd != null);
    }

    /**
     * 通过返回的NativeAd数据，创建对应的FbNativeAdData并完成成功的回调。
     *
     * @param nativeAd
     */
    public void createFbNativeDataAndCallBack(ViewGroup viewGroup, NativeAd nativeAd, int index) {
        if (nativeAd == null) {
            return;
        }
        long expireTime = AdConfigLoader.getInstance(mContext).getFbCacheExpireTimeFromConfig();
        if (expireTime == 0) {
            expireTime = AdConstants.FB_EXPIRE_TIME;
        }
        mNativeAd = nativeAd;
        mNativeAd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyLog.d(MyLog.TAG, "onTouch");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.
                            AD_CLICK_AD_BY_PACKAGENAME + "    Ad id:" + mNode.slot_id);
                }
                return false;
            }
        });
        mNativeAd.setImpressionListener(new ImpressionListener() {
            @Override
            public void onLoggingImpression(Ad ad) {
                MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.
                        AD_IMPRESSION_AD_BY_PACKAGENAME + "    Ad id:" + mNode.slot_id);
            }
        });
        mNativeSession = UUID.randomUUID().toString();
        MyLog.d(MyLog.TAG, "创建广告的回调");
        mFbNativeAdData = new FbNativeAdData(mFlow, mNativeAd, mNode, mNativeSession,
                CacheManager.FB, expireTime, index);
        MyLog.d(MyLog.TAG, "mFbNativeAdData");
        onAdLoadlistener.onLoad(FacebookAdAdapter.this);
        MyLog.d(MyLog.TAG, "onAdLoadlistener");
    }

    /**
     * init ad listener
     *
     * @param flow
     */
    private void initAdListener(Flow flow, final int index) {
        if (flow.type.equals(INTERSTITIAL)) {
            mInterstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    if (mOnCancelAdListener != null) {
                        mOnCancelAdListener.cancelAd();
                    }
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    onFbAdLoadError(adError);
                    MyLog.d(MyLog.TAG, "facebook request error:" + adError.getErrorMessage());
                    MyLog.d(MyLog.TAG, "facebook full error:" + adError.getErrorMessage() + "  Ad id:"
                            + mNode.slot_id + " Ad name:" + mNode.slot_name);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    WrapInterstitialAd wrapInterstitialAd = new WrapInterstitialAd(mInterstitialAd, mNode);
                    MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_FILL_FACEBOOK_FULL_SCREEN +
                            "  Ad id:" + mNode.slot_id + "sessionId" + mFullscreenSession);

                    if (onAdLoadlistener != null) {
                        onAdLoadlistener.onLoadInterstitialAd(wrapInterstitialAd);
                    } else {
                        MyLog.d(MyLog.TAG, "onAdLoadlistener is null, full ad no callback");
                    }

                }

                @Override
                public void onAdClicked(Ad ad) {
                    MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_FACEBOOK_FULL_SCREEN_AD_CLICK +
                            "  Ad id:" + mNode.slot_id + "sessionId" + mFullscreenSession);
                    if (mFbNativeAdData != null && mFbNativeAdData.onAdClickListener != null) {
                        mFbNativeAdData.onAdClickListener.onAdClicked();
                    } else {
                        MyLog.d(MyLog.TAG, "facebook adapter mOnAdClickListener == null ");
                    }
                }
            };
        } else {
            mAdListener = new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    MyLog.d(MyLog.TAG, "facebook request error:" + adError.getErrorMessage());
                    onFbAdLoadError(adError);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_FILL_FACEBOOK_AD_NATIVE_FILLED +
                            "    Ad id:" + mNode.slot_id + "Ad title:" + mNativeAd.getAdTitle() + "  SesseionId:" + mNativeSession);
                    long expireTime = AdConfigLoader.getInstance(mContext).getFbCacheExpireTimeFromConfig();
                    if (expireTime == 0) {
                        expireTime = AdConstants.FB_EXPIRE_TIME;
                    }
                    if (DeviceUtil.isWiFiActive(mContext)) {
                        downLoadFbImageIcon();
                    }
                    mFbNativeAdData = new FbNativeAdData(mFlow, mNativeAd, mNode, mNativeSession, CacheManager.FB, expireTime, index);
                    onAdLoadlistener.onLoad(FacebookAdAdapter.this);
                    MyLog.d(MyLog.TAG, "facebook adapter ad load view finish" + "   Ad id:" + mNode.slot_id + " Ad name:" + mNode.slot_name);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_FACEBOOK_AD_CLICK +
                            "    Ad id:" + mNode.slot_id + "Ad title:" + mNativeAd.getAdTitle() +
                            "  SesseionId:" + mNativeSession);
                    if (mFbNativeAdData != null && mFbNativeAdData.onAdClickListener != null) {
                        if (mFbNativeAdData == null) {
                            MyLog.d("aalistener", "facebook data is null");
                            return;
                        }
                        if (mFbNativeAdData.onAdClickListener == null) {
                            MyLog.d("aalistener", "facebook data is null");
                        }
                        MyLog.d(MyLog.TAG, "facebook adapter mOnAdClickListener != null from network ");
                        mFbNativeAdData.onAdClickListener.onAdClicked();
                    } else {
                        MyLog.d(MyLog.TAG, "facebook adapter mOnAdClickListener == null network onclick listener failed");
                    }
                }
            };
        }


    }

    public void downLoadFbImageIcon() {
        if (!StringUtil.isEmpty(mNativeAd.getAdCoverImage().getUrl())) {
            downloadImage(mNativeAd.getAdCoverImage().getUrl());
        }
        if (!StringUtil.isEmpty(mNativeAd.getAdIcon().getUrl())) {
            downloadImage(mNativeAd.getAdCoverImage().getUrl());
        }
        if (!StringUtil.isEmpty(mNativeAd.getAdChoicesIcon().getUrl())) {
            downloadImage(mNativeAd.getAdCoverImage().getUrl());
        }
    }

    /**
     * call back error type
     *
     * @param adError
     */
    private void onFbAdLoadError(AdError adError) {
        onAdLoadlistener.onLoadFailed(new mobi.android.adlibrary.internal.ad.AdError(mNode.slot_id, adError.getErrorMessage()));

        if (onAdLoadlistener == null) {
            MyLog.e(MyLog.TAG, "user not input param OnAdLoadListener");
            return;
        }
        HashMap<String, String> dataInfo = new HashMap<>();
        dataInfo.put(AdEventConstants.AD_REQUEST_FACEBOOK_AD_NATIVE_FIALED, String.valueOf(adError.getErrorCode()));
        if (mFlow.type.equals(INTERSTITIAL)) {
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_REQUEST_FAILED_FULL_SCREEN +
                    "  Ad id:" + mNode.slot_id + "error:" + adError.getErrorMessage());

        } else if (mFlow.type.equals(NATIVE)) {
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_REQUEST_FACEBOOK_AD_NATIVE_FIALED +
                    "  Ad id:" + mNode.slot_id + "error:" + adError.getErrorMessage() + "sessionId" + mNativeSession);

        }
    }

    @Override
    public View getAdView() {
        MyLog.w(MyLog.TAG, "platform FaceBookAdManger back data is null");
        return null;
    }

    @Override
    public NativeAdData getNativeAd() {
        return mFbNativeAdData;
    }

    @Override
    public String getIconUrl() {
        if (mNativeAd != null) {
            return mNativeAd.getAdIcon().getUrl();
        }
        return null;
    }

    @Override
    public void setOnCancelAdListener(OnCancelAdListener listener) {
        MyLog.d(MyLog.TAG, "setmOnCancelAdListener  faceBook");
        if (mFbNativeAdData != null) {
            mFbNativeAdData.cancelListener = listener;
        }
    }

    public void setOnAdClickListener(OnAdClickListener listener) {
        MyLog.d(MyLog.TAG, " faceBook set OnClick listener");
        if (mFbNativeAdData != null) {
            mFbNativeAdData.onAdClickListener = listener;
        }
    }

    @Override
    public void setOnAdTouchListener(View.OnTouchListener listener) {
        if (mNativeAd != null) {
            MyLog.d(MyLog.TAG, "setOnAdTouchListener");
            mNativeAd.setOnTouchListener(listener);
        }
    }

    @Override
    public void setOnPrivacyIconClickListener(View.OnClickListener listener) {
        if (mFbNativeAdData != null) {
            mFbNativeAdData.privacyIconClickListener = listener;
        }
    }

    @Override
    public void registerViewForInteraction(View view) {
        MyLog.d(MyLog.TAG, AdEventConstants.AD_FB_REGISTERVIEWINTERACTION);
    }

    @Override
    public int getAdType() {
        return CacheManager.FB;
    }

    @Override
    public Flow getFlow() {
        return mFlow;
    }

    @Override
    public AdNode getAdNode() {
        if (mNode != null) {
            return mNode;
        }
        return new AdNode();
    }

    @Override
    public void showCustomAdView() {
        if (getNativeAd() != null) {
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.SHOW_PLATFROM_THING[getNativeAd().getAdType()] +
                    "  Ad id:" + mNode.slot_id + "Ad title:" + mFbNativeAdData.getTitle() +
                    "  SessionId:" + mFbNativeAdData.getSessionID());
            getNativeAd().setIsShowed();
        }
    }

    @Override
    public String getAdPackageName() {
        return mAdPackageName;
    }

    @Override
    public void release(ViewGroup viewGroup) {

    }
}
