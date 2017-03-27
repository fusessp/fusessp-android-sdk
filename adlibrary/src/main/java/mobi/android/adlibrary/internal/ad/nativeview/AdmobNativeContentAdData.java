package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.provider.Settings;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.utils.MyLog;
/**
 * Created by liuyicheng on 16/6/15.
 */
public class AdmobNativeContentAdData extends NativeAdData {
    private NativeContentAd mNativeContentAd;
    private View vAdView;

    public AdmobNativeContentAdData(Flow flow, NativeContentAd mNativeContentAd, AdNode adNode,
                                    final String sessionID, int type, long expireTime, int index) {
        super();
        mNode = adNode;
        this.mNativeContentAd = mNativeContentAd;
        mSessionID = sessionID;
        setAdType(type);
        mFlow = flow;
        this.mExpired = expireTime;
        this.mFlowIndex = index;

    }


    public String getAdvertiser(){
        if (mNativeContentAd.getAdvertiser() != null){
            return mNativeContentAd.getAdvertiser().toString();
        }
        return "";
    }

    @Override
    public void destroy() {
        mNativeContentAd.destroy();
    }

    @Override
    public String getTitle() {
        return mNativeContentAd.getHeadline().toString();
    }

    @Override
    public String getCallToActionText() {
        return mNativeContentAd.getCallToAction().toString();
    }

    @Override
    public NativeContentAd getAdObject() {
        return mNativeContentAd;
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
    public String getCoverImageUrl() {
        if (mNativeContentAd.getImages() != null && mNativeContentAd.getImages().get(0) != null){
            return  mNativeContentAd.getImages().get(0).getUri().toString();
        }
        return "";
    }


    @Override
    public String getIconImageUrl() {
        if (mNativeContentAd.getLogo() != null && mNativeContentAd.getLogo().getUri() !=null){
            return mNativeContentAd.getLogo().getUri().toString();
        }
        return "";
    }

    @Override
    public String getSubtitle() {
        if (mNativeContentAd.getBody() != null){
            return   mNativeContentAd.getBody().toString();
        }
      return "";
    }


    public String getHeadline(){
        if(mNativeContentAd.getHeadline() != null){
            return mNativeContentAd.getHeadline().toString();
        }
        return  "";
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
