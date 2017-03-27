package mobi.android.adlibrary.internal.ad.bean;

/**
 * Created by vincent on 2016/5/10.
 */
public class WrapInterstitialAd {

    private com.facebook.ads.InterstitialAd fbInterstitualAd;
    private com.google.android.gms.ads.InterstitialAd admobIInterstitualAd;

    public WrapInterstitialAd(com.facebook.ads.InterstitialAd interstitialAd){
        fbInterstitualAd=interstitialAd;
        admobIInterstitualAd=null;
    }

    public WrapInterstitialAd(com.google.android.gms.ads.InterstitialAd interstitialAd){
        fbInterstitualAd=null;
        admobIInterstitualAd=interstitialAd;
    }

    public void show(){
        if(fbInterstitualAd!=null){
            fbInterstitualAd.show();
            return;
        }
        if(admobIInterstitualAd!=null){
            admobIInterstitualAd.show();
            return;
        }
    }
}
