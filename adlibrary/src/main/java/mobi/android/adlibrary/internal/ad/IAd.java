package mobi.android.adlibrary.internal.ad;

/**
 * Created by vincent on 16/5/4.
 */

import android.view.View;
import android.view.ViewGroup;

import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;


public interface IAd {

    View getAdView();

    NativeAdData getNativeAd();

    String getIconUrl();

    void setOnCancelAdListener(OnCancelAdListener listener);

    void setOnAdClickListener(OnAdClickListener listener);

    void setOnAdTouchListener(View.OnTouchListener listener);

    void setOnPrivacyIconClickListener(View.OnClickListener listener);

    void registerViewForInteraction(View view);

    AdNode getAdNode();

    void showCustomAdView();

    String getAdPackageName();

    Flow getFlow();

    void release(ViewGroup viewGroup);
}
