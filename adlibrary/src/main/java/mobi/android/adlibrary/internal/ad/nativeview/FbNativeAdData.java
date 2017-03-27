package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.utils.MyLog;


/**
 * Created by guob/1.
 */
public class FbNativeAdData extends NativeAdData {
    private NativeAd mNativeAd;
    public FbNativeAdData(Flow flow, NativeAd nativeAd, AdNode node, String sessionID,
                          int type, long expired, int index) {
        super();
        mNativeAd = nativeAd;
        mNode = node;
        mSessionID = sessionID;
        setAdType(type);
        this.mExpired = expired;
        this.mFlowIndex = index;
        NativeAd.Rating adRating = mNativeAd.getAdStarRating();
        if (adRating != null) {
            mRating = adRating.getValue() * 5 / adRating.getScale();
        }
        mFlow = flow;
    }

    @Override
    public String getCoverImageUrl() {
        NativeAd.Image coverImage = mNativeAd.getAdCoverImage();
        if (coverImage != null) {
            return coverImage.getUrl();
        }
        return "";
    }

    @Override
    public String getIconImageUrl() {
        NativeAd.Image coverImage = mNativeAd.getAdIcon();
        if (coverImage != null) {
            return coverImage.getUrl();
        }
        return "";
    }
    @Override
    public String getSubtitle() {
        return mNativeAd.getAdSubtitle();
    }
    @Override
    public String getTitle() {
        return mNativeAd.getAdTitle();
    }

    @Override
    public String getCallToActionText() {
        return mNativeAd.getAdCallToAction();
    }

    @Override
    public Object getAdObject() {
        return mNativeAd;
    }

    @Override
    public String getId() {
        return mSessionID;
    }

    @Override
    public void registerViewForInteraction(View view, View buttonView) {
        if (view != null && buttonView != null) {
            List<View> views = new ArrayList<>();
            views.add(view);
            views.add(buttonView);
            mNativeAd.registerViewForInteraction(buttonView, views);
        } else if (view != null) {
            mNativeAd.registerViewForInteraction(view);
        } else if (buttonView != null) {
            mNativeAd.registerViewForInteraction(buttonView);
        }

    }

    @Override
    public String getPrivacyInformationIconUrl() {
        if (mNativeAd.getAdChoicesIcon() != null) {
            return mNativeAd.getAdChoicesIcon().getUrl();
        }
        return "";
    }

    @Override
    public String getPrivacyInformationIconClickThroughUrl() {
        return mNativeAd.getAdChoicesLinkUrl();
    }

    @Override
    public void handlePrivacyIconClick(final Context context, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FbNativeAdData.this.privacyIconClickListener != null) {
                    privacyIconClickListener.onClick(v);
                }
                MyLog.d(MyLog.TAG,"AdSDK AdId:"+mNativeAd.getId()+"do handlePrivacyIconClick");
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(getPrivacyInformationIconClickThroughUrl());
                intent.setData(content_url);
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void setAdClickListener(final OnAdClickListener listener) {
//        mNativeAd.setAdListener(new AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                MyLog.d(MyLog.TAG,"FbNativeAdData onAdClicked from cache ");
//                String name = "";
//                if(mNode!=null){
//                    name = mNode.slot_name;
//                }
//                DotAdEventsManager.getInstance(null).sendEvent(AdEventConstants.AD_FACEBOOK_AD_CLICK+"_"+name," Ad title:"+mNativeAd.getAdTitle()+"  SesseionId:"+mNativeAd.getId());
//                listener.onAdClicked();
//            }
//        });
    }
    @Override
    public void setAdCancelListener(Context context, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.d(MyLog.TAG,"setAdCancelListener onClick");
                if(cancelListener!=null){
                    MyLog.d(MyLog.TAG,"setAdCancelListener cancelListener!=null ");
                    cancelListener.cancelAd();
                }else{
                    MyLog.d(MyLog.TAG,"setAdCancelListener cancelListener==null ");
                }
            }
        });
    }
    @Override
    public void setAdTouchListener(View.OnTouchListener listener) {
        mNativeAd.setOnTouchListener(listener);
    }
    @Override
    public void setPrivacyIconClickListener(View.OnClickListener listener) {
        this.privacyIconClickListener = listener;
    }
    public void registerViewForInteraction(View view,List<View> views) {
        if (view!=null && views != null ) {
            mNativeAd.registerViewForInteraction(view, views);
        }

    }
}
