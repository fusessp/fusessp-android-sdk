package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.view.View;
import com.mopub.nativeads.NativeAd;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by liuyicheng on 16/8/12.
 */
public class MopubNativeAdData extends NativeAdData {
    private NativeAd mNativeAd;
    public MopubNativeAdData(Flow flow, NativeAd nativeAd, String SessionID, int type, long expireTime, int index) {
        super();
        mNativeAd = nativeAd;
        mSessionID = SessionID;
        setAdType(type);
        this.mExpired = expireTime;
        this.mFlowIndex = index;
        mFlow = flow;
    }

    @Override
    public String getSubtitle() {
        return "mopub native ";
    }

    public double getStarRating() {
        return mRating;
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
    public void setAdView(View adView) {
    }

    @Override
    public String getPrivacyInformationIconUrl() {
        return "mopub native  不提供URL";
    }

    @Override
    public String getPrivacyInformationIconClickThroughUrl() {
        return "mopub native  不提供URL";
    }

    @Override
    public void setAdClickListener(final OnAdClickListener listener) {
        onAdClickListener =listener;
    }

    @Override
    public void setAdCancelListener(Context context, View view) {
        MyLog.d(MyLog.TAG, " mopub setAdCancelListener onClick");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
