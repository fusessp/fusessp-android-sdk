package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;

import java.util.UUID;

import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.AdErrorType;
import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.OnCancelAdListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.ad.nativeview.FlurryNativeAdData;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by guojia on 2016/9/7.
 */
public class FlurryNativeAdAdapter extends AdAdapter {

    final private static String TAG = "FlurryNativeAdAdapter";
    public static boolean TEST_MODE = false;
    //only need to initialize once
    private static boolean isFlurryInited;
    private AdNode mNode;
    private Context mContext;
    private Flow mFlow;
    private FlurryNativeAdData mNativeAdData;

    public FlurryNativeAdAdapter(Context context,final AdNode node) {
        super(context);
        mContext = context;
        mNode = node;
    }

    public static void initFlurry( Context context, String appKey) {
        //Init twice should be ok, no lock
        if (!isFlurryInited && context != null && appKey != null) {
            FlurryAgent.Builder builder = new FlurryAgent.Builder();
            if (TEST_MODE) {
                builder.withLogEnabled(true);
            }
            builder.withListener(new FlurryAgentListener() {
                        @Override
                        public void onSessionStarted() {

                        }
                    }).build(context, appKey);
            isFlurryInited = true;
            MyLog.d(TAG, "initFlurry");
        }

    }

    private FlurryNativeAdData createNativeAdData(FlurryAdNative rawAd) {
        String sessionId = UUID.randomUUID().toString();
        FlurryNativeAdData adData = new FlurryNativeAdData(mFlow, rawAd, sessionId, mNode, AdConfigLoader.getInstance(mContext).getYhCacheExpireTimeFromConfig());
        return adData;
    }

    @Override
    public void loadAd(int num,Flow flow) {
        mFlow = flow;
        if ( mFlow.key == null) {
            postError(AdErrorType.INVALID_REQUEST);
            return;
        }
        String appKey = AdConfigLoader.getInstance(mContext).getYhAppKey();
        if (!TextUtils.isEmpty(appKey)) {
            initFlurry(mContext, appKey);
        } else {
            postError(AdErrorType.INVALID_REQUEST);
            return;
        }

        //start loading ad
        //call destroy when prune it
        FlurryAdNative nativeAd = new FlurryAdNative(mContext, mFlow.key);
        nativeAd.setListener(new FlurryLoadListener());

        //for test mode
        if(TEST_MODE) {
            FlurryAdTargeting adTargeting = new FlurryAdTargeting();
            adTargeting.setEnableTestAds(true);
            nativeAd.setTargeting(adTargeting);
        }

        MyLog.d(TAG, "loadAd");
        MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.YAHOO_NATIVE_REQUEST +
                "  Ad id:" + mNode.slot_id );
        nativeAd.fetchAd();
    }

    private void postError(String reason) {
        if (mNode == null || onAdLoadlistener == null) {
            return;
        }
        MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.YAHOO_NATIVE_FAIL +
                "  Ad id:" + mNode.slot_id );
        onAdLoadlistener.onLoadFailed(new AdError(mNode.slot_id, reason));
    }

    @Override
    public int getAdType() {
        return CacheManager.YH;
    }

    @Override
    public View getAdView() {
        return null;
    }

    @Override
    public NativeAdData getNativeAd() {
        return mNativeAdData;
    }

    @Override
    public String getIconUrl() {
        return mNativeAdData != null ? mNativeAdData.getIconImageUrl() : null;
    }

    @Override
    public void setOnCancelAdListener(OnCancelAdListener listener) {
        if (mNativeAdData != null) {
            mNativeAdData.cancelListener = listener;
        }
    }

    @Override
    public void setOnAdClickListener(OnAdClickListener listener) {
        if (mNativeAdData != null) {
            mNativeAdData.onAdClickListener = listener;
        }
    }

    @Override
    public void setOnAdTouchListener(View.OnTouchListener listener) {
        //empty;
    }

    @Override
    public void setOnPrivacyIconClickListener(View.OnClickListener listener) {
        if (mNativeAdData != null) {
            mNativeAdData.privacyIconClickListener = listener;
        }
    }

    @Override
    public void registerViewForInteraction(View view) {
        if (mNativeAdData != null) {
            mNativeAdData.registerViewForInteraction(view, null);
        }
    }

    @Override
    public AdNode getAdNode() {
        return mNode;
    }

    @Override
    public void showCustomAdView() {
        if (mNativeAdData != null) {
            mNativeAdData.setIsShowed();
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.SHOW_PLATFROM_THING[getNativeAd().getAdType()] +
                    "  Ad id:" + mNode.slot_id + "Ad title:" + mNativeAdData.getTitle() + " SessionId:" + mNativeAdData.getSessionID());
        }
    }

    @Override
    public String getAdPackageName() {
        return null;
    }

    @Override
    public Flow getFlow() {
        return null;
    }

    @Override
    public void release(ViewGroup viewGroup) {

    }

    private class FlurryLoadListener implements FlurryAdNativeListener {
        FlurryLoadListener() {

        }

        @Override
        public void onFetched(FlurryAdNative flurryAdNative) {
            MyLog.d(TAG, "onFetched");
            mNativeAdData = createNativeAdData(flurryAdNative);
            if (onAdLoadlistener != null) {
                onAdLoadlistener.onLoad(FlurryNativeAdAdapter.this);
            }
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.YAHOO_NATIVE_FILLED +
                    "  Ad id:" + mNode.slot_id);
        }

        @Override
        public void onShowFullscreen(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onCloseFullscreen(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onAppExit(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onClicked(FlurryAdNative flurryAdNative) {
            MyLog.d(TAG, "onClick");
            if (mNativeAdData != null && mNativeAdData.onAdClickListener != null) {
                mNativeAdData.onAdClickListener.onAdClicked();
            }
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.YAHOO_NATIVE_CLICK +
                    "  Ad id:" + mNode.slot_id);
        }

        @Override
        public void onImpressionLogged(FlurryAdNative flurryAdNative) {
            MyLog.d(TAG, "onImpression");

        }

        @Override
        public void onExpanded(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onCollapsed(FlurryAdNative flurryAdNative) {

        }

        @Override
        public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int i) {
            switch (flurryAdErrorType) {
                case FETCH:
                    MyLog.e(TAG, "Fetch error, code: " + i);
                    postError(AdErrorType.NO_FILL);
                    break;
                case RENDER:
                    MyLog.e(TAG, "RENDER error, code: " + i);
                    break;
                case CLICK:
                    MyLog.e(TAG, "CLICK error, code: " + i);
                    break;
                default:
                    MyLog.e(TAG, "other error, code: " + i);
                    break;
            }
        }
    }
}
