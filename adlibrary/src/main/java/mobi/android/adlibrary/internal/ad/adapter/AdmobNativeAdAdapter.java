package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

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
import mobi.android.adlibrary.internal.ad.nativeview.AdmobAppNativeAdData;
import mobi.android.adlibrary.internal.ad.nativeview.AdmobNativeContentAdData;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.DeviceUtil;
import mobi.android.adlibrary.internal.utils.MyLog;
/**
 * Created by liuyicheng on 16/6/14.
 * modify by vincent on 16/09/04
 */
public class AdmobNativeAdAdapter extends AdAdapter {
    public final static int ADMOB_ADVANCE_APP_TYPE = 1;
    public final static int ADMOB_ADVANCE_CONTENT_TYPE = 2;
    public final static int ADMOB_ADVANCE_ECPM_FRIST = 3;
    private OnCancelAdListener mOnCancelAdListener;
    private NativeAdData mAdmobNativeAdData;
    private String mAdmobSessionID;
    private int mAdType;
    private AdNode mAdNode;
    private Flow mFlow;
    private com.google.android.gms.ads.AdListener mAdmobNativelistener;

    public AdmobNativeAdAdapter(Context context, AdNode adNode) {
        super(context);
        this.mContext = context;
        this.mAdType = CacheManager.ADMOB;
        this.mAdNode = adNode;
        initAdmobEventListener(adNode);
    }


    public void loadAd(final int index, final Flow flow) {
        MyLog.i(MyLog.TAG, "advanced admob adapter loadAd " + "       Ad id " + mAdNode.slot_id + " Ad name:" + mAdNode.slot_name);

        mFlow = flow;
        AdLoader.Builder builder = new AdLoader.Builder(mContext, mFlow.key);
        String requestLog = "";
        switch (mFlow.admob_type) {
            case ADMOB_ADVANCE_APP_TYPE:
                MyLog.d(MyLog.TAG, "advanced admob ad start load APPAD" + "   Ad id:" + mAdNode.slot_id + " Ad name:" + mAdNode.slot_name);

                builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                        getDataAndCallBack(nativeAppInstallAd, null, mAdNode, index);
                    }
                });
                requestLog = AdEventConstants.AD_ADMOB_NATIVE_APP_REQUEST;
                break;
            case ADMOB_ADVANCE_CONTENT_TYPE:
                MyLog.d(MyLog.TAG, "advanced admob ad start load ContentAD" + "   Ad id:" + mAdNode.slot_id + " Ad name:" + mAdNode.slot_name);

                builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                        getDataAndCallBack(null, nativeContentAd, mAdNode, index);

                    }
                });
                requestLog = AdEventConstants.AD_ADMOB_NATIVE_CONTENT_REQUEST;

                break;

            case ADMOB_ADVANCE_ECPM_FRIST:
            default:
                MyLog.d(MyLog.TAG, "advanced admob ad start load ContentAD and AppAd" + "   Ad id:" + mAdNode.slot_id + " Ad name:" + mAdNode.slot_name);


                builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                        getDataAndCallBack(nativeAppInstallAd, null, mAdNode, index);
                    }
                });

                builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                        getDataAndCallBack(null, nativeContentAd, mAdNode, index);
                    }
                });
                requestLog = AdEventConstants.AD_ADMOB_NATIVE_REQUEST;
                break;

        }
        /**
         * reback data not contain drawable
         */
        NativeAdOptions builderOptions = new NativeAdOptions.Builder()
                .setReturnUrlsForImageAssets(true)
                .build();
        mAdmobSessionID = UUID.randomUUID().toString();
        MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + requestLog +
                "    Ad id:" + mAdNode.slot_id + "  SesseionId:" + mAdmobSessionID);
        AdLoader adLoader = builder.withAdListener(mAdmobNativelistener)
                .withNativeAdOptions(builderOptions)
                .build();
        adLoader.loadAd(new AdRequest.Builder()
                .addTestDevice("5344C74B7203570D239D1545AF93774F")
                .build());

    }

    /**
     * create admob data and save data
     *
     * @param nativeContentAd
     * @param nativeAppInstallAd
     * @param adNode
     */
    private void getDataAndCallBack(NativeAppInstallAd nativeAppInstallAd, NativeContentAd nativeContentAd, AdNode adNode, int index) {
        if (nativeAppInstallAd == null && nativeContentAd == null) {
            return;
        }
        long expireTime = AdConfigLoader.getInstance(mContext).getAdmobCacheExpireTimeFromConfig();

        if (expireTime == 0) {
            expireTime = AdConstants.ADMOB_EXPIRE_TIME;
        }
        String adTypeFill = "";
        if (nativeContentAd != null) {
            mAdmobNativeAdData = new AdmobNativeContentAdData(mFlow, nativeContentAd, mAdNode, mAdmobSessionID,
                    CacheManager.ADMOB_CONTENT, expireTime, index);
            adTypeFill = AdEventConstants.AD_ADMOB_NATIVE_CONTENT_FILLED;
            mAdType = CacheManager.ADMOB_CONTENT;
            if (nativeContentAd.getImages().get(0) != null && DeviceUtil.isWiFiActive(mContext)) {
                downloadImage(nativeContentAd.getImages().get(0).getUri().toString());
            }
        } else {
            mAdmobNativeAdData = new AdmobAppNativeAdData(mFlow, nativeAppInstallAd, mAdNode, mAdmobSessionID,
                    CacheManager.ADMOB_APP, expireTime, index);
            if (nativeAppInstallAd.getVideoController().hasVideoContent()) {
                adTypeFill = AdEventConstants.AD_ADMOB_NATIVE_APP_CONTAINS_VIDEO_FILLED;
            } else {
                adTypeFill = AdEventConstants.AD_ADMOB_NATIVE_APP_FILLED;
            }
            mAdType = CacheManager.ADMOB_APP;
            if (DeviceUtil.isWiFiActive(mContext)) {
                if (nativeAppInstallAd.getImages().get(0) != null) {
                    downloadImage(nativeAppInstallAd.getImages().get(0).getUri().toString());
                }
                if (nativeAppInstallAd.getIcon().getUri() != null) {
                    downloadImage(nativeAppInstallAd.getIcon().getUri().toString());
                }
            }
        }

        MyLog.d(MyLog.TAG, "设置内容广告超时时间：" + mAdmobNativeAdData.mExpired);

        MyLog.d(MyLog.TAG, adNode.slot_name + "_" + adTypeFill + "    Ad id:" + adNode.slot_id + "Ad title:"
                + mAdmobNativeAdData.getTitle() + "  seesionID" + mAdmobSessionID);

        MyLog.d(MyLog.TAG, "admob native " + mAdmobNativeAdData.getAdType() + "ad adapter onLoad call back");

        onAdLoadlistener.onLoad(AdmobNativeAdAdapter.this);
    }


    private void initAdmobEventListener(final AdNode node) {
        mAdmobNativelistener = new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (mOnCancelAdListener != null) {
                    mOnCancelAdListener.cancelAd();
                    MyLog.i(MyLog.TAG, "AdmobAdvancedAD ----onAdClosed");
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);


                String faileddRequest = "";
                if (mFlow.admob_type == ADMOB_ADVANCE_APP_TYPE) {
                    faileddRequest = AdEventConstants.AD_ADMOB_NATIVE_APP_REQUEST_FAILED;
                } else if (mFlow.admob_type == ADMOB_ADVANCE_CONTENT_TYPE) {
                    faileddRequest = AdEventConstants.AD_ADMOB_NATIVE_CONTENT_REQUEST_FAILED;
                } else if (mFlow.admob_type == ADMOB_ADVANCE_ECPM_FRIST) {
                    faileddRequest = AdEventConstants.AD_ADMOB_NATIVE_REQUEST_FAILED;
                }
                MyLog.d(MyLog.TAG, node.slot_name + "_" + faileddRequest + "    Ad id:" + node.slot_id
                        + "errorCode:" + errorCode);


                if (onAdLoadlistener == null) {
                    return;
                }
                AdError adError = new AdError();
                adError.slotid = node.slot_id;
                switch (errorCode) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        adError.adError = AdErrorType.OTHER;
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        adError.adError = AdErrorType.INVALID_REQUEST;
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        adError.adError = AdErrorType.NETWORK_FAILD;
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        adError.adError = AdErrorType.NO_FILL;
                        break;
                }
                onAdLoadlistener.onLoadFailed(adError);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                if (mOnCancelAdListener != null) {
                    mOnCancelAdListener.cancelAd();
                    MyLog.i(MyLog.TAG, "AdmobAdvancedAD ----onAdClosed");
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                MyLog.i(MyLog.TAG, "AdmobAdvancedAD------onAdCLICKED");

                if (mAdmobNativeAdData != null) {
                    MyLog.d("aalistener", "onAdOpened nativeAdData != null");
                    if (mAdmobNativeAdData.onAdClickListener != null) {
                        mAdmobNativeAdData.onAdClickListener.onAdClicked();
                    }
                } else {
                    MyLog.d("aalistener", "onAdOpened nativeAdData == null");
                    return;
                }
                String tittle = "no title";
                String sessionID = "no sessionId";
                if (mAdmobNativeAdData != null) {
                    if (mAdmobNativeAdData.getTitle() != null) {
                        tittle = mAdmobNativeAdData.getTitle();
                    }
                    if (mAdmobNativeAdData.getSessionID() != null) {
                        sessionID = mAdmobNativeAdData.getSessionID();
                    }
                }
                if (node == null) {
                    return;
                }
                if (mAdmobNativeAdData.getAdType() == CacheManager.ADMOB_CONTENT) {
                    MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_CONTENT_CLICK +
                            "    Ad id:" + node.slot_id + "Ad title:" + tittle + "  SesseionId:" + sessionID);

                } else if (mAdmobNativeAdData.getAdType() == CacheManager.ADMOB_APP) {
                    if (mAdmobNativeAdData.getAdObject() instanceof NativeAppInstallAd) {
                        if (((NativeAppInstallAd) mAdmobNativeAdData.getAdObject()).getVideoController().hasVideoContent()) {
                            MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_APP_CONTAINS_VIDEOP_CLICK +
                                    "    Ad id:" + node.slot_id + "Ad title:" + tittle + "  SesseionId:" + sessionID);
                        }
                    }
                    MyLog.d(MyLog.TAG, node.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_APP_CLICK +
                            "    Ad id:" + node.slot_id + "Ad title:" + tittle + "  SesseionId:" + sessionID);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                MyLog.i(MyLog.TAG, "new AdmobNativeAdAdapter onAdLoaded");

            }
        };
    }

    @Override
    public int getAdType() {
        return mAdType;
    }

    @Override
    public View getAdView() {
        MyLog.w(MyLog.TAG, "platform AdmobAdManger back data is null");
        return null;


    }

    @Override
    public NativeAdData getNativeAd() {
        return mAdmobNativeAdData;
    }

    @Override
    public String getIconUrl() {
        return null;
    }


    public void setOnCancelAdListener(OnCancelAdListener listener) {
        this.mOnCancelAdListener = listener;
    }

    public void setOnAdClickListener(OnAdClickListener listener) {
        if (mAdmobNativeAdData != null) {
            MyLog.d("aalistener", "adpter mNativeAd setmOnAdClickListener");
            mAdmobNativeAdData.onAdClickListener = listener;
        }
    }

    @Override
    public void setOnAdTouchListener(View.OnTouchListener listener) {

    }

    @Override
    public void setOnPrivacyIconClickListener(View.OnClickListener listener) {

    }

    @Override
    public void registerViewForInteraction(View view) {
    }

    @Override
    public AdNode getAdNode() {
        if (mAdNode != null) {
            return mAdNode;
        }
        return new AdNode();
    }

    @Override
    public Flow getFlow() {
        return mFlow;
    }

    @Override
    public void showCustomAdView() {
        if (getNativeAd() != null) {
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.SHOW_PLATFROM_THING[getNativeAd().getAdType()] +
                    "  Ad id:" + mAdNode.slot_id + "Ad title:" + mAdmobNativeAdData.getTitle() + " SessionId:" + mAdmobNativeAdData.getSessionID());
            getNativeAd().setIsShowed();
        }
    }

    @Override
    public String getAdPackageName() {
        return null;
    }

    @Override
    public void release(ViewGroup viewGroup) {

    }
}
