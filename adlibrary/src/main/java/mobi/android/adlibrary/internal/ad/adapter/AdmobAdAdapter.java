package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.UUID;

import mobi.android.adlibrary.internal.ad.AdErrorType;
import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.OnCancelAdListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by vincent on 16/5/4.
 */
public class AdmobAdAdapter extends AdAdapter {

    private final String NATIVE_EXPRESS = "native_express";
    private AdView vAdView;
    private NativeExpressAdView vNativeExpressAdView;
    private String mBannerSessionID;
    private String mExpressSessionID;
    private InterstitialAd mInterstitialAd;
    private OnAdClickListener mOnAdClickListener;
    private OnCancelAdListener mOnCancelAdListener;
    private WrapInterstitialAd mWrapInterstitialAd;
    private AdNode mAdNode;
    private Flow mFlow;
    private AdListener mListener;
    public AdmobAdAdapter(Context context, AdNode node) {
        super(context);
        this.mAdNode = node;
    }


    public void loadAd(int num,Flow flow) {
        MyLog.i(MyLog.TAG, "new AdmobAdAdapter loadAd"+"    Ad id:"+mAdNode.slot_id+" Ad name:"+mAdNode.slot_name);

        mFlow = flow;
        initAdListener(flow);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("5344C74B7203570D239D1545AF93774F")
                .build();

        if (flow.type.equals(INTERSTITIAL)) {
            if (mInterstitialAd == null) {
                mInterstitialAd = new InterstitialAd(mContext);
            }
            mInterstitialAd.setAdUnitId(flow.key);
            mInterstitialAd.setAdListener(mListener);
            mInterstitialAd.loadAd(request);
            mWrapInterstitialAd =new WrapInterstitialAd(mInterstitialAd,mAdNode);
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_REQUEST_ADMOB_FULL_SCREEN_AD +
                    "    Ad id:"+mAdNode.slot_id+" sessionID:"+ mWrapInterstitialAd.getmSessionID());
        }else if (flow.type.equals(BANNER)) {
            if (vAdView == null) {
                vAdView = new AdView(mContext);
            }
            if(mAdNode.width==0){
                vAdView.setAdSize(AdSize.SMART_BANNER);
            }else {
                vAdView.setAdSize(new AdSize(mAdNode.width,mAdNode.height));
            }
            mBannerSessionID = UUID.randomUUID().toString();
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_BANNER_REQUEST +
                    "    Ad id:"+mAdNode.slot_id+" sessionID:"+ mBannerSessionID);
            vAdView.setAdUnitId(flow.key);
            vAdView.setAdListener(mListener);
            vAdView.loadAd(request);
        }else if(NATIVE_EXPRESS.equals(flow.type)){
            //和AdView区分开来，以后Express可能会做视频。-- 和banner的性质一些样。只是这个支持视屏。
            if (vNativeExpressAdView == null) {
                vNativeExpressAdView = new NativeExpressAdView(mContext);
                vNativeExpressAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            if(mAdNode.width == 0){
                vNativeExpressAdView.setAdSize(AdSize.SMART_BANNER);
            }else {
                vNativeExpressAdView.setAdSize(new AdSize(mAdNode.width,mAdNode.height));
            }

            mExpressSessionID = UUID.randomUUID().toString();
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_EXPRESS_REQUEST +
                    "    Ad id:"+mAdNode.slot_id+" sessionID:"+ mExpressSessionID);
            vNativeExpressAdView.setAdUnitId(flow.key);
            MyLog.d(MyLog.TAG,"Express-Key:"+flow.key);
            vNativeExpressAdView.setAdListener(mListener);
            vNativeExpressAdView.loadAd(request);
        }else{
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_REQUEST_ADMOB_OTHER_AD +
                    "    Ad id:"+mAdNode.slot_id+" Ad name:"+mAdNode.slot_name);
        }
    }

    public void initAdListener(final Flow flow){
        mListener = new AdListener() {
            @Override
            public void onAdClosed() {
                if(mOnCancelAdListener !=null){
                    mOnCancelAdListener.cancelAd();
                    MyLog.i(MyLog.TAG, "addAd------onAdClosed"+"    Ad id:"+mAdNode.slot_id+" Ad name:"+mAdNode.slot_name);
                }
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                if (mWrapInterstitialAd !=null&&flow.type.equals(INTERSTITIAL)){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_REQUEST_FAILED_ADMOB_FULL_SCREEN_AD +
                            "    Ad id:" + mAdNode.slot_id);
                }else if (flow.type.equals(BANNER)&& vAdView !=null){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_BANNER_REQUEST_FAILED +
                            "    Ad id:" + mAdNode.slot_id);
                }else if(NATIVE_EXPRESS.equals(flow.type)&& vNativeExpressAdView!=null){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_EXPRESS_REQUEST_FAILED +
                            "    Ad id:" + mAdNode.slot_id);
                }
                if(onAdLoadlistener==null)
                    return;
                AdError adError = new AdError();
                adError.slotid = mAdNode.slot_id;
                switch (errorCode){
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
                if(mOnCancelAdListener !=null){
                    mOnCancelAdListener.cancelAd();
                    MyLog.i(MyLog.TAG, "addAd------onAdLeftApplication"+"    Ad id:"+mAdNode.slot_id+" Ad name:"+mAdNode.slot_name);
                }
                if (mWrapInterstitialAd !=null&&flow.type.equals(INTERSTITIAL)){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_CLICK_ADMOB_FULL_SCREEN_AD + "  Ad id:" + mAdNode.slot_id + " sessionID:" + mWrapInterstitialAd.getmSessionID());
                }else if (flow.type.equals(BANNER)&& vAdView !=null){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_BANNER_CLICK + "    Ad id:" + mAdNode.slot_id + " sessionID:" + mBannerSessionID);
                }else if(flow.type.equals(NATIVE_EXPRESS)&& vNativeExpressAdView !=null){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_EXPRESS_CLICK + "    Ad id:" + mAdNode.slot_id + " sessionID:" + mExpressSessionID);
                }
            }
            @Override
            public void onAdOpened() {
                if(mOnAdClickListener !=null){
                    mOnAdClickListener.onAdClicked();
                }
                if(vAdView !=null){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_AD_CLICK + "    Ad id:" + mAdNode.slot_id);
                }else if (mWrapInterstitialAd !=null&&flow.type.equals(INTERSTITIAL)){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_SHOWING_ADMOB_FULL_SCREEN_AD + "  Ad id:" + mAdNode.slot_id + " sessionID:" + mWrapInterstitialAd.getmSessionID());
                }else if(vNativeExpressAdView !=null){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_EXPRESS_CLICK + "  Ad id:" + mAdNode.slot_id + " sessionID:" + mExpressSessionID);
                }


            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                if (flow.type.equals(INTERSTITIAL)){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_FILL_ADMOB_FULL_SCREEN_AD +
                            "    Ad id:"+mAdNode.slot_id+" Ad name:"+mAdNode.slot_name+" sessionID:"+ mWrapInterstitialAd.getmSessionID());
                    onAdLoadlistener.onLoadInterstitialAd(mWrapInterstitialAd);
                }else if (flow.type.equals(BANNER)){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_BANNER_FILLED +
                            "    Ad id:"+mAdNode.slot_id+" sessionID:"+ mBannerSessionID);
                    onAdLoadlistener.onBannerLoad(AdmobAdAdapter.this);
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_BANNER_SHOW +
                            "    Ad id:" + mAdNode.slot_id + " sessionID:" + mBannerSessionID);
                }else if(NATIVE_EXPRESS.equals(flow.type)){
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_EXPRESS_FILLED +
                            "    Ad id:"+mAdNode.slot_id+" sessionID:"+ mBannerSessionID);
                    onAdLoadlistener.onBannerLoad(AdmobAdAdapter.this);
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_ADMOB_NATIVE_EXPRESS_SHOW +
                            "    Ad id:" + mAdNode.slot_id + " sessionID:" + mBannerSessionID);
                }else{
                    MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.AD_FILL_ADMOB_AD_OTHER_FILLED +
                            "    Ad id:"+mAdNode.slot_id);
                }
            }
        };
    }
    @Override
    public int getAdType() {
        return CacheManager.ADMOB;
    }

    @Override
    public AdNode getAdNode() {
        if (mAdNode != null){
            return mAdNode;
        }
        return new AdNode();
    }


    @Override
    public View getAdView() {
        MyLog.w(MyLog.TAG, "platform AdmobAdManger banner back data is null");
        View backView = null;
        if(BANNER.equals(mFlow.type)){
            backView = vAdView;
        }else if(NATIVE_EXPRESS.equals(mFlow.type)){
            backView = vNativeExpressAdView;
        }
        return backView;
    }

    @Override
    public NativeAdData getNativeAd() {
        return null;
    }

    @Override
    public String getIconUrl() {
        return null;
    }

    @Override
    public void setOnCancelAdListener(OnCancelAdListener listener) {
        this.mOnCancelAdListener = listener;
    }

    @Override
    public void setOnAdClickListener(OnAdClickListener listener) {
        this.mOnAdClickListener = listener;
    }

    @Override
    public void setOnAdTouchListener(View.OnTouchListener listener) {
        if(vAdView !=null){
            vAdView.setOnTouchListener(listener);
        }else if(vNativeExpressAdView != null){
            vNativeExpressAdView.setOnTouchListener(listener);
        }
    }

    @Override
    public void setOnPrivacyIconClickListener(View.OnClickListener listener) {

    }

    @Override
    public void registerViewForInteraction(View view) {

    }

    @Override
    public void showCustomAdView() {
        if(getNativeAd() != null){
            MyLog.d(MyLog.TAG, mAdNode.slot_name + "_" + AdEventConstants.SHOW_PLATFROM_THING[getNativeAd().getAdType()] +
                    "  Ad id:" + mAdNode.slot_id + "Ad title:"  + " SessionId:" );
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
