package mobi.android.adlibrary.internal.ad;

import java.util.UUID;

import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by vincent on 2016/5/12.
 */
public class WrapInterstitialAd {

    private com.facebook.ads.InterstitialAd fbInterstitualAd;
    private com.google.android.gms.ads.InterstitialAd admobIInterstitualAd;
    private AdNode mNode;
    private String mSessionID;
    public WrapInterstitialAd(com.facebook.ads.InterstitialAd interstitialAd, AdNode node){
        fbInterstitualAd=interstitialAd;
        admobIInterstitualAd=null;
        mSessionID = UUID.randomUUID().toString();
        this.mNode = mNode;
    }

    public WrapInterstitialAd(com.google.android.gms.ads.InterstitialAd interstitialAd, AdNode node){
        fbInterstitualAd=null;
        admobIInterstitualAd=interstitialAd;
        mSessionID = UUID.randomUUID().toString();
        this.mNode = node;
    }

    public String getmSessionID(){
        return mSessionID;
    }

    public void show(){
        if(fbInterstitualAd!=null){
            fbInterstitualAd.show();
            MyLog.d(MyLog.TAG, mNode.slot_name + "_" + AdEventConstants.AD_IS_SHOWING_FACEBOOK_FULL_SCREEN +
                    "  Ad id:" + mNode.slot_id);
            return;
        }
        if(admobIInterstitualAd!=null){
            admobIInterstitualAd.show();

            return;
        }
    }
}