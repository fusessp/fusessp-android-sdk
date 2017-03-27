package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAppInstallAd;

import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by liuyicheng on 16/6/15.
 */
public class AdmobAppNativeAdData extends NativeAdData {
    private NativeAppInstallAd mNativeAppInstallAd;
    private View vAdView;
    public AdmobAppNativeAdData(Flow flow, NativeAppInstallAd mNativeAppInstallAd, AdNode adNode,
                                final String sessionID, int type, long expireTime, int index) {
        super();
        mNode = adNode;
        this.mNativeAppInstallAd = mNativeAppInstallAd;
        mSessionID = sessionID;
        setAdType(type);
        this.mExpired = expireTime;
        this.mFlowIndex = index;
        mFlow = flow;
        mRating= mNativeAppInstallAd.getStarRating();
    }

    @Override
    public void destroy() {
        mNativeAppInstallAd.destroy();
    }

    @Override
    public String getTitle() {
        if(mNativeAppInstallAd==null){
            return "";
        }
        return mNativeAppInstallAd.getHeadline().toString();
    }

    @Override
    public String getCallToActionText() {
        if(mNativeAppInstallAd!=null) {
            return mNativeAppInstallAd.getCallToAction().toString();
        }else{
            return "";
        }
    }

    @Override
    public String getSubtitle() {
        if (mNativeAppInstallAd != null){
            return mNativeAppInstallAd.getBody().toString();
        }
        return "";
    }

    @Override
    public String getCoverImageUrl() {
        if (mNativeAppInstallAd.getImages() != null && mNativeAppInstallAd.getImages().get(0) != null){
            return  mNativeAppInstallAd.getImages().get(0).getUri().toString();
        }
        return "";
    }


    @Override
    public String getIconImageUrl() {
        if (mNativeAppInstallAd.getIcon() != null && mNativeAppInstallAd.getIcon().getUri() !=null){
            return mNativeAppInstallAd.getIcon().getUri().toString();
        }
        return "";
    }


    public String getHeadline(){
        if(mNativeAppInstallAd.getHeadline() != null){
            return mNativeAppInstallAd.getHeadline().toString();
        }
        return  "";
    }

    public String getStore(){
        if (mNativeAppInstallAd.getStore() != null){
            return mNativeAppInstallAd.getStore().toString();
        }
        return "";
    }

    public String getPrice(){
        if(mNativeAppInstallAd.getPrice() != null){
            return mNativeAppInstallAd.getPrice().toString();
        }
        return "";
    }

    @Override
    public NativeAppInstallAd getAdObject() {
        return mNativeAppInstallAd;
    }

    @Override
    public void setAdView(View adView) {
        vAdView = adView;
    }

    @Override
    public void setAdClickListener(final OnAdClickListener listener) {
        onAdClickListener = listener;
//        if(vAdView!=null){
//            vAdView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onAdClicked();
//                }
//            });
//        }

    }

    @Override
    public void setAdCancelListener(Context context, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.d(MyLog.TAG, "setAdCancelListener onClick");
                if(cancelListener!=null){
                    MyLog.d(MyLog.TAG,"setAdCancelListener cancelListener!=null ");
                    cancelListener.cancelAd();
                }else{
                    MyLog.d(MyLog.TAG,"setAdCancelListener cancelListener==null ");
                }
            }
        });
    }

}
