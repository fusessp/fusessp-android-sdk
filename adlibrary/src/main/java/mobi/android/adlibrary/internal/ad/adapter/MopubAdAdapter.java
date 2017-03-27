package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

import java.util.EnumSet;
import java.util.UUID;

import mobi.android.adlibrary.R;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.AdErrorType;
import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.OnAdInnerLoadListener;
import mobi.android.adlibrary.internal.ad.OnCancelAdListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.ad.nativeview.MopubNativeAdData;
import mobi.android.adlibrary.internal.ad.nativeview.NativeAdViewManager;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by liuyicheng on 16/8/5.
 * modify by sun on 16/09/04
 */
public class MopubAdAdapter extends AdAdapter {
    private OnCancelAdListener mOnCancelAdListener;
    private Context mContext;
    private ViewBinder mViewBinder;
    private NativeAdData mopubNativeAdData;
    private String mContentSessionID;
    private AdNode mAdNode;
    private Flow mFlow;
    private View vMopubAdView;
    private MoPubView.BannerAdListener mBannerAdListener;
    private MoPubNative.MoPubNativeNetworkListener mMoPubNativeNetworkListener;

    public MopubAdAdapter(Context context, AdNode adNode) {
        super(context);
        this.mContext = context;
        this.mAdNode = adNode;
    }

    public void loadAd(int index, Flow flow) {
        mFlow = flow;
        MyLog.d(MyLog.TAG, "flow.mopub_type:" + flow.mopub_type + "----MOPUB_BANNER_AD:"  +
                "----flow.key:" + flow.key);
        initAdListener(flow, index);

        if (flow.type.equals(BANNER)) {
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_BANNER_REQUEST +
                    "    Ad id:" + mAdNode.slot_id + " sessionID:" + mContentSessionID);

            LayoutInflater inflater = LayoutInflater.from(mContext);
            vMopubAdView = inflater.inflate(R.layout.layout_ad_view_mopub, null);
            MoPubView mopubView = (MoPubView) vMopubAdView.findViewById(R.id.banner_mopubview);
            mopubView.setAdUnitId(flow.key);
            mopubView.setAutorefreshEnabled(false);
            mopubView.loadAd();

            mopubView.setBannerAdListener(mBannerAdListener);

        } else if (flow.type.equals(NATIVE)) {

            MyLog.d(MyLog.TAG, "key:" + flow.key);
            MoPubNative moPubNative = new MoPubNative(mContext, flow.key, mMoPubNativeNetworkListener);

            mViewBinder = new ViewBinder.Builder(NativeAdViewManager.getLayoutID(flow.native_style))
                    .mainImageId(R.id.ad_cover_image)
                    .iconImageId(R.id.icon_image_native)
                    .titleId(R.id.ad_title_text)
                    .textId(R.id.ad_subtitle_Text)
                    .callToActionId(R.id.calltoaction_text)
                    .privacyInformationIconImageId(R.id.native_ad_choices_image)
                    .build();

            MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(mViewBinder);
            moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
            EnumSet<RequestParameters.NativeAdAsset> assetsSet = EnumSet.of(RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT, RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.STAR_RATING);
            RequestParameters requestParameters = new RequestParameters.Builder()
                    .desiredAssets(assetsSet)
                    .build();

            mContentSessionID = UUID.randomUUID().toString();
            MyLog.d(MyLog.TAG, "Mopub ad start load AD" + "   Ad id:" + mAdNode.slot_id + " Ad name:" + mAdNode.slot_name);
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_NATIVE_REQUEST +
                    "    Ad id:" + mAdNode.slot_id + "sessionID " + mContentSessionID);

            moPubNative.makeRequest(requestParameters);
        } else {
            MyLog.d(MyLog.TAG, "no Mopub ad type");
        }
    }

    /**
     * init ad listener
     *
     * @param flow
     */
    public void initAdListener(final Flow flow, final int index) {
        if (flow.type.equals(BANNER)) {
            mBannerAdListener = new MoPubView.BannerAdListener() {
                @Override
                public void onBannerLoaded(MoPubView moPubView) {
                    MyLog.d(MyLog.TAG, "MOPUB_BANNER_AD request success ");
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_BANNER_FILLED +
                            "    Ad id:" + mAdNode.slot_id + " sessionID:" + mContentSessionID);
                    onAdLoadlistener.onBannerLoad(MopubAdAdapter.this);
                }

                @Override
                public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_BANNER_REQUEST_FAILED +
                            "    Ad id:" + mAdNode.slot_id + " sessionID:" + mContentSessionID);
                    AdError adError = new AdError(mAdNode.slot_id, AdErrorType.NETWORK_FAILD);
                    onAdLoadlistener.onLoadFailed(adError);

                }

                @Override
                public void onBannerClicked(MoPubView moPubView) {
                    if (moPubView != null && mopubNativeAdData != null && mopubNativeAdData.onAdClickListener != null) {
                        mopubNativeAdData.onAdClickListener.onAdClicked();
                        MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_BANNER_CLICK +
                                "    Ad id:" + mAdNode.slot_id);
                    }
                }

                @Override
                public void onBannerExpanded(MoPubView moPubView) {
                    MyLog.d(MyLog.TAG, "Mopub ad onBannerExpanded");
                }

                @Override
                public void onBannerCollapsed(MoPubView moPubView) {
                    MyLog.d(MyLog.TAG, "Mopub ad onBannerCollapsed");
                }
            };
        } else {
            mMoPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {
                @Override
                public void onNativeLoad(NativeAd nativeAd) {
                    if (nativeAd != null) {
                        nativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                            @Override
                            public void onImpression(View view) {
                            }

                            @Override
                            public void onClick(View view) {
                                MyLog.d(MyLog.TAG, AdConstants.LISTENER_LOG + "onAdCLICKED inner callback");
                                if (mopubNativeAdData != null && mopubNativeAdData.onAdClickListener != null) {
                                    mopubNativeAdData.onAdClickListener.onAdClicked();
                                    MyLog.d(MyLog.TAG, "onAdCLICKED inner callback");
                                } else {
                                    MyLog.d(MyLog.TAG, "mopub onAdCLICKED ");
                                }
                                MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_NATIVE_CLICK
                                        + "    Ad id:" + mAdNode.slot_id + "Ad title:MOPUB maybe no title !" +
                                        "  SesseionId:" + mopubNativeAdData.getSessionID());
                            }
                        });
                    }
                    long expireTime = AdConfigLoader.getInstance(mContext).getMopubCacheExpireTimeFromConfig();
                    if ((expireTime == 0)) {
                        expireTime = AdConstants.MOUPB_EXPIRE_TIME;
                    }
                    mopubNativeAdData = new MopubNativeAdData(mFlow, nativeAd, mContentSessionID, CacheManager.MOPUB, expireTime, index);
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_NATIVE_FILLED +
                            "    Ad id:" + mAdNode.slot_id + "Ad title:" + "Mopub 不提供title！" + "  seesionID" + mContentSessionID);

                    if (onAdLoadlistener != null) {
                        MyLog.d(MyLog.TAG, "mopub  native adapter onLoad callback");
                        onAdLoadlistener.onLoad(MopubAdAdapter.this);
                    }
                }

                @Override
                public void onNativeFail(NativeErrorCode errorCode) {
                    final String errorMessage = (errorCode != null) ? errorCode.toString() : "";
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_MOPUB_NATIVE_REQUEST_FAILED
                            + "    Ad id:" + mAdNode.slot_id + "errorMessage:" + errorMessage + "  SesseionId:" + mContentSessionID);
                    if (onAdLoadlistener != null) {
                        AdError adError = new AdError(mAdNode.slot_id, errorMessage);
                        onAdLoadlistener.onLoadFailed(adError);
                    }

                }

            };
        }
    }


    @Override
    public void registerViewForInteraction(View view) {

    }

    @Override
    public AdNode getAdNode() {
        return mAdNode;
    }

    @Override
    public void setOnPrivacyIconClickListener(View.OnClickListener listener) {

    }


    @Override
    public void setAdListener(OnAdInnerLoadListener listener) {
        super.setAdListener(listener);
    }

    @Override
    public View getAdView() {
        MyLog.w(MyLog.TAG, "platform MopubAdManger back data is null");
        return vMopubAdView;
    }

    @Override
    public NativeAdData getNativeAd() {
        return mopubNativeAdData;
    }


    @Override
    public int getAdType() {
        return CacheManager.MOPUB;
    }

    @Override
    public String getIconUrl() {
        return "MOPUB  不提供URL";
    }

    @Override
    public void setOnCancelAdListener(OnCancelAdListener listener) {
        this.mOnCancelAdListener = listener;
    }

    @Override
    public void setOnAdClickListener(OnAdClickListener listener) {
        if (mopubNativeAdData != null) {
            mopubNativeAdData.onAdClickListener = listener;
        }
    }

    @Override
    public void setOnAdTouchListener(View.OnTouchListener listener) {

    }


    @Override
    public void showCustomAdView() {
        if (getNativeAd() != null) {
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.SHOW_PLATFROM_THING[getAdType()] +
                    "  Ad id:" + mAdNode.slot_id + "Ad title:" + mopubNativeAdData.getTitle() + " SessionId:" + mopubNativeAdData.getSessionID());
            getNativeAd().setIsShowed();
        }
    }

    @Override
    public String getAdPackageName() {
        return null;
    }

    @Override
    public Flow getFlow() {
        return mFlow;
    }

    @Override
    public void release(ViewGroup viewGroup) {

    }
}
