package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.ViewBinder;

import mobi.android.adlibrary.R;

/**
 * Created by liuyicheng on 16/8/12.
 */
public class MopubAdView {
    private Context mContext;
    private ViewBinder viewBinder;
    public View getMopubAdView(Context context, int layout, NativeAd Ad , ViewGroup parentViews) {
        if (Ad == null) {
            return null;
        }
        this.mContext = context;
        viewBinder = new ViewBinder.Builder(layout)
                .mainImageId(R.id.ad_cover_image)
                .iconImageId(R.id.icon_image_native)
                .titleId(R.id.ad_title_text)
                .textId(R.id.ad_subtitle_Text)
                .callToActionId(R.id.calltoaction_text)
                .privacyInformationIconImageId(R.id.native_ad_choices_image)
                .build();
        AdapterHelper adapterHelper = new AdapterHelper(mContext,0,2);
        View returnView = adapterHelper.getAdView(null,parentViews,Ad,viewBinder);
        return returnView;
    }
}
