package mobi.android.adlibrary.internal.ad;

/**
 * Created by vincent on 16/5/4.
 */
public interface OnAdInnerLoadListener {
    void onBannerLoad(IAd ad);
    void onLoad(IAd ad);
    void onLoadFailed(AdError error);
    void onLoadInterstitialAd(WrapInterstitialAd ad);
}
