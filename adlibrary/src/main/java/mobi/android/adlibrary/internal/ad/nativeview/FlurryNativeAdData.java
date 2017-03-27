package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;

import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by guojia on 2016/9/7.
 */
public class FlurryNativeAdData extends NativeAdData {

    private static final String AD_TITLE = "headline";
    private static final String AD_SEC_HQIMAGE = "secHqImage";
    private static final String AD_SEC_IMAGE = "secImage";
    private static final String CALL_TO_ACTION = "callToAction";
    private static final String SUMMARY = "summary";
    private static final String APP_RATING = "appRating";
    private static final String AD_ASSET_CATEGORY = "appCategory";
    private static final String TAG = "FlurryNativeAdData";
    private FlurryAdNative mRawAdNative;

    private String mTitle;
    private String mCoverImgUrl;
    private String mIconUrl;
    private String mCallToAction;
    private String mSummary;
    private boolean mIsDownloadAd;
    private float mRating = 4.5f;

    public FlurryNativeAdData(Flow flow, FlurryAdNative rawAd, String sessionId, AdNode node, long expiredTime) {
        mRawAdNative = rawAd;
        mSessionID = sessionId;
        mNode = node;
        mExpired = expiredTime;
        mFlow = flow;
        setAdType(CacheManager.YH);
        setUpData(rawAd);
    }

    private void setUpData(@NonNull FlurryAdNative flurryAdNative) {
        FlurryAdNativeAsset adTitle = flurryAdNative.getAsset(AD_TITLE);
        if (adTitle != null) {
            mTitle = adTitle.getValue();
            MyLog.d(TAG, "titlt: " + mTitle);
        }
        FlurryAdNativeAsset adAdCoverImageAsset = flurryAdNative.getAsset(AD_SEC_HQIMAGE);
        if (adAdCoverImageAsset != null) {
            mCoverImgUrl = flurryAdNative.getAsset(AD_SEC_HQIMAGE).getValue();
            MyLog.d(TAG, "mCoverImgUrl: " + mCoverImgUrl);
        }
        FlurryAdNativeAsset adAdIconImageAsset = flurryAdNative.getAsset(AD_SEC_IMAGE);
        if (adAdIconImageAsset != null) {
            mIconUrl = flurryAdNative.getAsset(AD_SEC_IMAGE).getValue();
            MyLog.d(TAG, "mIconUrl: " + mIconUrl);
        }
        FlurryAdNativeAsset adCallToAction = flurryAdNative.getAsset(CALL_TO_ACTION);
        if (adCallToAction != null) {
            mCallToAction = adCallToAction.getValue();
            MyLog.d(TAG, "mCallToAction: " + mCallToAction);
        }
        FlurryAdNativeAsset adBody = flurryAdNative.getAsset(SUMMARY);
        if (adBody != null) {
            mSummary = adBody.getValue();
            MyLog.d(TAG, "mSummary: " + mSummary);
        }
        try {
            FlurryAdNativeAsset appRating = flurryAdNative.getAsset(APP_RATING);
            if (appRating != null && !TextUtils.isEmpty(appRating.getValue())) {
                mRating = Float.parseFloat(appRating.getValue()) * 5;
                MyLog.d(TAG, "mRating: " + mRating);
            }
        } catch (Exception e) {
        }
        FlurryAdNativeAsset category = flurryAdNative.getAsset(AD_ASSET_CATEGORY);
        mIsDownloadAd = (category != null && !TextUtils.isEmpty(category.getValue()));
    }
    @Override
    public long getStartRequestTime() {
        return 0;
    }

    @Override
    public String getCoverImageUrl() {
        return mCoverImgUrl;
    }

    @Override
    public String getIconImageUrl() {
        return mIconUrl;
    }

    @Override
    public String getSubtitle() {
        return mSummary;
    }

    @Override
    public double getStarRating() {
        return mRating;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getCallToActionText() {
        return mCallToAction;
    }

    @Override
    public Object getAdObject() {
        return mRawAdNative;
    }

    @Override
    public String getId() {
        return mSessionID;
    }

    @Override
    public void setAdView(View adView) {

    }

    @Override
    public void registerViewForInteraction(View view, View buttonView) {
        if ( mRawAdNative != null && view != null) {
            mRawAdNative.setTrackingView(view);
        }
    }

    @Override
    public String getPrivacyInformationIconUrl() {
        return null;
    }

    @Override
    public String getPrivacyInformationIconClickThroughUrl() {
        return null;
    }

    @Override
    public void handlePrivacyIconClick(Context context, View view) {
        if ( privacyIconClickListener != null) {
            privacyIconClickListener.onClick(view);
        }
    }

    @Override
    public void setAdClickListener(OnAdClickListener listener) {
        onAdClickListener = listener;
    }

    @Override
    public void setAdCancelListener(Context context, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelListener!=null){
                    cancelListener.cancelAd();
                }
            }
        });
    }

    @Override
    public void setAdTouchListener(View.OnTouchListener listener) {
        //empty, don't support
    }

    @Override
    public void setPrivacyIconClickListener(View.OnClickListener listener) {
        privacyIconClickListener = listener;
    }

    @Override
    public String getSessionID() {
        return mSessionID;
    }

    @Override
    public void destroy() {
        mRawAdNative.destroy();
    }
}
