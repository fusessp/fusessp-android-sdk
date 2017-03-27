package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.mopub.nativeads.NativeAd;

import java.util.HashMap;
import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.R;

import mobi.android.adlibrary.internal.ad.AdEventConstants;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.cache.CacheManager;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by vincent on 16/5/4.
 */
public class NativeAdViewManager {
    private static HashMap<Integer,Integer> mLayouts = new HashMap<>();

    //TODO all styles of ad view(if add new style of view ,please add xml)
    public static void initLayout(){
        MyLog.d(MyLog.TAG, "初始化布局");
        mLayouts.put(101, R.layout.layout_ad_view_one_one);
        mLayouts.put(102, R.layout.layout_ad_view_one_two);
        mLayouts.put(103, R.layout.layout_ad_view_one_three);
        mLayouts.put(104, R.layout.layout_ad_view_one_four);
        mLayouts.put(105, R.layout.layout_ad_view_one_five);

        mLayouts.put(201, R.layout.layout_ad_view_two_one);
        mLayouts.put(202, R.layout.layout_ad_view_two_two);
        mLayouts.put(203, R.layout.layout_ad_view_two_three);
        mLayouts.put(204, R.layout.layout_ad_view_two_four);

        mLayouts.put(301, R.layout.layout_ad_view_three_one);
        mLayouts.put(302, R.layout.layout_ad_view_three_two);
        mLayouts.put(303, R.layout.layout_ad_view_three_three);
        mLayouts.put(304, R.layout.layout_ad_view_three_four);
        mLayouts.put(305, R.layout.layout_ad_view_three_five);
        mLayouts.put(306, R.layout.layout_ad_view_three_six);
        mLayouts.put(307, R.layout.layout_ad_view_three_seven);
    }

    public static void addLayout(int key ,int value){
        mLayouts.put(key,value);
    }

    public static NativeAdView createAdView() {
        NativeAdView nativeAdView = new NativeAdView();
        return nativeAdView;
    }

    public static View loadAdView(Context context, NativeAdView nativeAdView, int native_style, NativeAdData nativeAd, ViewGroup root) {
        if(nativeAd == null){
            MyLog.e(MyLog.TAG,"native data is null");
            return null;
        }



        Integer layoutId = mLayouts.get(native_style);

        if(layoutId == null){
            MyLog.d(MyLog.TAG, AdEventConstants.AD_CONFIG_STYLE_IS_WRONG + "xml style is wrong");
            return null;
        }
        View adView = null;
        switch(nativeAd.getAdType()){
            case CacheManager.ADMOB_APP:
                AdmobNativeAdView admobnativeAdView = new AdmobNativeAdView();
                adView = admobnativeAdView.renderView(context, layoutId, nativeAd,(NativeAppInstallAd)nativeAd.getAdObject(), root);
                setCancelListener(context,adView,nativeAd);
                if (native_style == 8) {
                    ImageView iv_choice = (ImageView) adView.findViewById(R.id.native_ad_choices_image);
                    iv_choice.setVisibility(View.GONE);
                }
                break;
            case CacheManager.ADMOB_CONTENT:
                AdmobNativeAdView admobnativeContentAdView = new AdmobNativeAdView();
                adView = admobnativeContentAdView.renderView(context, layoutId, nativeAd,(NativeContentAd) nativeAd.getAdObject(), root);
                setCancelListener(context,adView,nativeAd);
                if (native_style == 8) {
                    ImageView iv_choice = (ImageView) adView.findViewById(R.id.native_ad_choices_image);
                    iv_choice.setVisibility(View.GONE);
                }
                break;
            case CacheManager.MOPUB:
                MopubAdView mopubAdView = new MopubAdView();
                adView = mopubAdView.getMopubAdView(context, getLayoutID(native_style), (NativeAd) nativeAd.getAdObject(), root);
                if (adView.findViewById(R.id.close_image)!=null){
                    adView.findViewById(R.id.close_image).setVisibility(View.INVISIBLE);
                }
                break;
            case CacheManager.MOPUB_BANNER:
                LayoutInflater inflater = LayoutInflater.from(context);
                adView = inflater.inflate(R.layout.layout_ad_view_mopub, null);
                break;
            default:
                if(nativeAdView == null){
                    nativeAdView = createAdView();
                }
                adView = nativeAdView.renderView(context, layoutId, nativeAd, root);
                break;

        }
        //set custom ad action background
        if(adView!=null){
            View view=adView.findViewById(R.id.calltoaction_text);
            Drawable drawable=AdAgent.getInstance().mActionBackground;
            if(view!=null && drawable != null){
                view.setBackgroundDrawable(drawable);
            }
        }

        return adView;
    }
    public static int getLayoutID(int layoutId) {
        int layId = 0;
        if( mLayouts.get(layoutId) != null){
            layId = mLayouts.get(layoutId);
        }
        return layId;
    }

    public static void setCancelListener(Context context,View view,NativeAdData nativeAd){
        if (view.findViewById(R.id.close_image)!=null){
            nativeAd.setAdCancelListener(context,view.findViewById(R.id.close_image));
        }
    }


}

