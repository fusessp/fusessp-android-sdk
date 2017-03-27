package mobi.android.adlibrary.internal.ad.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import mobi.android.adlibrary.internal.ad.IAd;
import mobi.android.adlibrary.internal.ad.OnAdInnerLoadListener;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by vincent on 16/5/4.
 */
public abstract class AdAdapter implements IAd {
    public static final String NATIVE = "native";
    public static final String INTERSTITIAL = "interstitial";
    public static final String BANNER = "banner";
    public static final String NATIVE_EXPRESS = "native_express";
    protected Context mContext;
    OnAdInnerLoadListener onAdLoadlistener;
    DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(false)
            .delayBeforeLoading(0)
            .cacheInMemory(false)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.ARGB_4444)
            .displayer(new SimpleBitmapDisplayer())
            .handler(new Handler()) // default
            .build();

    AdAdapter(Context context) {
        this.mContext = context;
    }

    protected void setAdListener(OnAdInnerLoadListener listener) {
        onAdLoadlistener = listener;
    }

    protected void loadAd(int num, Flow flow) {
    }

    protected void loadAdByCache(AdNode node, ViewGroup viewGroup) {
    }

    protected abstract int getAdType();

    protected void downloadImage(String url) {
        ImageLoader.getInstance().loadImage(url,mDisplayImageOptions,new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                MyLog.d(MyLog.TAG,"down load image success!!!");
            }

        });

    }


}
