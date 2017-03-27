package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;


import java.util.List;

import mobi.android.adlibrary.R;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.StringUtil;

/**
 * Created by liuyicheng on 16/6/15.
 */
public class AdmobNativeAdView {
    private static final int COVER_IMAGEVIEW = 1; //大图
    private static final int ICON_IMAGEVIEW = 2;  //小图
    private static final int AD_CHOICE_IMAGEVIEW = 3;
    private String mRenderViewTime;
    private DisplayImageOptions mOptions;

    public AdmobNativeAdView() {
        mOptions = new DisplayImageOptions.Builder()
//              .showImageOnLoading(R.drawable.ad_cover_back_new)
                .showImageForEmptyUri(R.drawable.ad_cover_back_new)
                .showImageOnFail(R.drawable.ad_cover_back_new)
                .resetViewBeforeLoading(false)
                .delayBeforeLoading(0)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_4444)
                .displayer(new SimpleBitmapDisplayer())
                .handler(new Handler()) // default
                .build();
    }

    public View renderView(Context context, int layout, NativeAdData nativeAd, NativeAppInstallAd nativeappAd, ViewGroup parentViews) {
        mRenderViewTime = String.valueOf(System.currentTimeMillis());
        MyLog.d(MyLog.TAG, "ADMOBT renderView App " + "   time:   " + mRenderViewTime);
        if (nativeappAd == null) {
            MyLog.d(MyLog.TAG, "ADMOBT nativeappAd == null,return " + "   time:   " + mRenderViewTime);
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        NativeAppInstallAdView installAdView = new NativeAppInstallAdView(context);
        View adView = inflater.inflate(layout, installAdView, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(adView.getLayoutParams().width, adView.getLayoutParams().height);
        installAdView.setLayoutParams(params);
        installAdView.addView(adView);
        populateAppInstallAdView(nativeappAd, installAdView, layout, nativeAd, context);
        MyLog.d(MyLog.TAG, "ADMOBT get app view" + "   time:   " + mRenderViewTime);
        return installAdView;
    }

    public View renderView(Context context, int layout, NativeAdData nativeAd,
                           NativeContentAd nativeContentAd, ViewGroup parentViews) {
        mRenderViewTime = String.valueOf(System.currentTimeMillis());
        MyLog.d(MyLog.TAG, "ADMOBT renderView Content " + "   time:   " + mRenderViewTime);
        if (nativeContentAd == null) {
            MyLog.d(MyLog.TAG, "ADMOBT nativeContentAd == null,return" + "   time:   " + mRenderViewTime);
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        final NativeContentAdView contentAdView = new NativeContentAdView(context);

        View adView = inflater.inflate(layout, contentAdView, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(adView.getLayoutParams().width, adView.getLayoutParams().height);
        contentAdView.setLayoutParams(params);
        contentAdView.addView(adView);
        populateContentAdView(nativeContentAd, contentAdView, layout, nativeAd, context);
        MyLog.d(MyLog.TAG, "ADMOBT get Content view " + "   time:   " + mRenderViewTime);
        return contentAdView;
    }

    //TODO 赋值方式过于混乱需要修改
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView, int layout, NativeAdData nativeAd,
                                          Context context) {
        boolean hasVideo  = nativeAppInstallAd.getVideoController().hasVideoContent();
        if (hasVideo){
            MyLog.d(MyLog.TAG,"this admob app ad has  video content!!!!!!!!!!!");
            if (adView.findViewById(R.id.ad_admob_cover_image) != null){
                if (adView.findViewById(R.id.ad_fb_cover_image) != null){
                    adView.findViewById(R.id.ad_fb_cover_image).setVisibility(View.GONE);
                }
                if (adView.findViewById(R.id.ad_cover_image) != null){
                    adView.findViewById(R.id.ad_cover_image).setVisibility(View.GONE);
                }
                adView.findViewById(R.id.ad_admob_cover_image).setVisibility(View.VISIBLE);
                MediaView mv = (MediaView) adView.findViewById(R.id.ad_admob_cover_image);
                adView.setMediaView(mv);
            }

        }else {
            MyLog.d(MyLog.TAG,"this admob app have not video content!!!!!!!");
        }
        MyLog.d(MyLog.TAG, "ADMOBT adView set App view " + "   time:   " + mRenderViewTime);
        View vContainerView = adView.findViewById(R.id.rl_view_container);
        TextView titltView = (TextView) adView.findViewById(R.id.ad_title_text);
        if (vContainerView != null && titltView != null && nativeAd != null && nativeAd.mNode != null && nativeAd.mNode.mTilteColor != 0) {
            titltView.setTextColor(context.getResources().getColor(nativeAd.mNode.mTilteColor));
        }
        TextView subTitltView = (TextView) adView.findViewById(R.id.ad_subtitle_Text);
        if (vContainerView != null && subTitltView != null && nativeAd != null && nativeAd.mNode != null && nativeAd.mNode.mSubTitleColor != 0) {
            subTitltView.setTextColor(context.getResources().getColor(nativeAd.mNode.mSubTitleColor));
        }
        TextView calltoActionView = (TextView) adView.findViewById(R.id.calltoaction_text);
        if (vContainerView != null && calltoActionView != null && nativeAd != null && nativeAd.mNode != null) {
            if (nativeAd.mNode.mButtonColor != 0) {
                calltoActionView.setTextColor(context.getResources().getColor(nativeAd.mNode.mButtonColor));
            }
            if (nativeAd.mNode.ctaBackground != 0) {
                calltoActionView.setBackgroundResource(nativeAd.mNode.ctaBackground);
            }
        }

        if (vContainerView != null && nativeAd != null && nativeAd.mNode != null) {
            if (nativeAd.mNode.transparent) {
                vContainerView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            } else {
                vContainerView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }
        View title = adView.findViewById(R.id.ad_title_text);
        if (title != null) {
            adView.setHeadlineView(title);
        }
        View coverImage = adView.findViewById(R.id.ad_cover_image);
        if (coverImage != null) {
            adView.setImageView(coverImage);
        }
        View subTitle = adView.findViewById(R.id.ad_subtitle_Text);
        if (subTitle != null) {
            adView.setBodyView(subTitle);
        }
        View callToAction = adView.findViewById(R.id.calltoaction_text);
        if (callToAction != null) {
            adView.setCallToActionView(callToAction);
        }
        View iconImage = adView.findViewById(R.id.icon_image_native);
        if (iconImage != null) {
            adView.setIconView(iconImage);
        }
//        adView.setPriceView(adView.findViewById(R.id.ad_subtitle_Text));
        View ratingBar = adView.findViewById(R.id.ad_ratingbar);
        if (ratingBar != null) {
            adView.setStarRatingView(ratingBar);
        }
        View descriptionText = adView.findViewById(R.id.ad_description_Text);
        if (descriptionText != null) {
            adView.setStoreView(descriptionText);
        }
        //有旋转角度的cover图
        ImageView vCoverNext = (ImageView) adView.findViewById(R.id.ad_cover_image_next);
        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        }
        if (subTitle != null && nativeAppInstallAd.getBody() != null) {

            ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
//
            MyLog.d(MyLog.TAG, "副标题内容:" + nativeAppInstallAd.getBody());
        }

        View view = adView.getCallToActionView();
        if (view != null && view instanceof TextView) {
            MyLog.d(MyLog.TAG, "CTA-App:" + nativeAppInstallAd.getCallToAction());
            if(layout == R.layout.layout_ad_view_one_four){
                ((TextView) adView.getCallToActionView()).setText("");
            }else{
                ((TextView) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
            }

        }

        if (adView.getIconView() != null) {
            Uri uri = nativeAppInstallAd.getIcon().getUri();
//
            setViewBitmap(uri, (ImageView) adView.getIconView(), null,ICON_IMAGEVIEW, layout, context, false);
        }

        List<NativeAd.Image> images = nativeAppInstallAd.getImages();

        if (images.size() > 0) {
            ImageView cover = (ImageView) adView.getImageView();
            if (adView.getImageView() != null) {
                Uri uri = images.get(0).getUri();
                setViewBitmap(uri, cover, vCoverNext,COVER_IMAGEVIEW, layout, context, true);
            }

        }
        if (descriptionText != null && adView.getStoreView() != null) {
            if (nativeAppInstallAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
            }
        }

        if (adView.getStarRatingView() != null) {
            if (nativeAppInstallAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {

                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAppInstallAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
        MyLog.d(MyLog.TAG, "ADMOBT setNativeAd app view" + "   time:   " + mRenderViewTime);

    }

    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView, int layout, NativeAdData nativeAd,
                                       Context context) {

        boolean isCoverType = false;//判断当前是否有cover控件
        //如果有大图的话，用cover来填充
        if (adView.findViewById(R.id.ad_cover_image) != null) {
            isCoverType = true;
            adView.setImageView(adView.findViewById(R.id.ad_cover_image));
        }
        adView.setLogoView(adView.findViewById(R.id.icon_image_native));

        adView.setHeadlineView(adView.findViewById(R.id.ad_title_text));
        adView.setBodyView(adView.findViewById(R.id.ad_description_Text));
        adView.setCallToActionView(adView.findViewById(R.id.calltoaction_text));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_subtitle_Text));
        View vContainerView = adView.findViewById(R.id.rl_view_container);
        TextView titltView = (TextView) adView.findViewById(R.id.ad_title_text);
        if (vContainerView != null && titltView != null && nativeAd != null && nativeAd.mNode != null && nativeAd.mNode.mTilteColor != 0) {
            titltView.setTextColor(context.getResources().getColor(nativeAd.mNode.mTilteColor));
        }
        TextView subTitltView = (TextView) adView.findViewById(R.id.ad_subtitle_Text);
        if (vContainerView != null && subTitltView != null && nativeAd != null && nativeAd.mNode != null && nativeAd.mNode.mSubTitleColor != 0) {
            subTitltView.setTextColor(context.getResources().getColor(nativeAd.mNode.mSubTitleColor));
        }

        if (vContainerView != null && nativeAd != null && nativeAd.mNode != null) {
            if (nativeAd.mNode.transparent) {
                vContainerView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            } else {
                vContainerView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

        // Some assets are guaranteed to be in every NativeContentAd.
        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        }
//        MyLog.d("RTR","url:"+nativeContentAd.getImages().get(0).getUri().toString()+"   title"+nativeContentAd.getHeadline().toString()+"   Advertiser"+nativeContentAd.getAdvertiser()+
//                "   CallToAction"+nativeContentAd.getCallToAction()+"   LogoUri"+nativeContentAd.getLogo().getUri().toString());
        if (adView.getBodyView() != null) {
            ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        }

        if (adView.getCallToActionView() != null && adView.getCallToActionView() instanceof TextView) {
            TextView callToActionView = (TextView) adView.getCallToActionView();
            if(R.layout.layout_ad_view_one_four == layout){
                callToActionView.setText("");
            }else{
                callToActionView.setText(nativeContentAd.getCallToAction());
            }
            MyLog.d(MyLog.TAG, "CTA-Content:" + nativeContentAd.getCallToAction());
            if (vContainerView != null && nativeAd != null && nativeAd.mNode != null) {
                if (nativeAd.mNode.mButtonColor != 0) {
                    callToActionView.setTextColor(context.getResources().getColor(nativeAd.mNode.mButtonColor));
                }
                if (nativeAd.mNode.ctaBackground != 0) {
                    callToActionView.setBackgroundResource(nativeAd.mNode.ctaBackground);
                }
            }
        }

        if (adView.getAdvertiserView() != null) {
            ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());
        }
        ImageView vImageCoverNext = (ImageView) adView.findViewById(R.id.ad_cover_image_next);
        //填充大图
        Uri uri = null;
        List<NativeAd.Image> images = nativeContentAd.getImages();
        if (images != null && images.size() > 0) {
            ImageView cover = (ImageView) adView.getImageView();
            if (images.get(0) == null) {
                return;
            }
            uri = images.get(0).getUri();
            if (uri != null) {
                setViewBitmap(uri, cover,vImageCoverNext, COVER_IMAGEVIEW, layout, context, isCoverType);
            }
        }
        //填充icon
        NativeAd.Image logoImage = nativeContentAd.getLogo();
        ImageView icon = (ImageView) adView.getLogoView();
        if (logoImage != null && !StringUtil.isEmpty(logoImage.getUri().toString())) {
            setViewBitmap(logoImage.getUri(), icon, null,ICON_IMAGEVIEW,layout, context, false);
        } else {
            setViewBitmap(uri, icon, null,ICON_IMAGEVIEW, layout, context, isCoverType);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
        MyLog.d(MyLog.TAG, "ADMOBT setNativeAd app view " + "   time:   " + mRenderViewTime);
    }

    public void setViewBitmap(Uri uri, final ImageView imageView,final ImageView imageCoverNext, final int imageType, final int layout, final Context context, boolean isCover) {
        String url = uri.toString();
        MyLog.e(MyLog.TAG, "admob url:" + url);
        if (imageView == null) {
            return;
        }
//
//        if (isCover && imageType == COVER_IMAGEVIEW && !(layout == R.layout.layout_ad_view_model_fifteen ||
//                layout == R.layout.layout_ad_view_model_eightteen ||
//                layout == R.layout.layout_ad_view_model_nineteen)) {
//            if (imageView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
//                RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); //取控件textView当前的布局参数
//                linearParams.width = DeviceUtil.getScreenWidth(context);
//                linearParams.height = DeviceUtil.getScreenWidth(context) * 10 / 19;
//                imageView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
//            } else if (imageView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
//                LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) imageView.getLayoutParams(); //取控件textView当前的布局参数
//                linearParams.width = DeviceUtil.getScreenWidth(context);
//                linearParams.height = DeviceUtil.getScreenWidth(context) * 10 / 19;
//                imageView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
//            }


            ImageLoader.getInstance().displayImage(url, imageView, mOptions,
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1,
                                                      Bitmap bitmap) {
                            if (bitmap == null) {
                                return;
                            }
                            if (imageType == ICON_IMAGEVIEW) {
//                                if (layout == R.layout.layout_ad_view_model_fifteen) {
//                                    imageView.setVisibility(View.VISIBLE);
//                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                    Bitmap bit = DeviceUtil.createCircleImage(bitmap);
//                                    imageView.setImageBitmap(bit);
//                                } else if (layout == R.layout.layout_ad_view_model_eightteen
//                                        || layout == R.layout.layout_ad_view_model_nineteen
//                                        || layout == R.layout.layout_ad_view_model_twentytwo
//                                        || layout == R.layout.layout_ad_view_model_twentythree
//                                        || layout == R.layout.layout_ad_view_model_twentyfour) {
//                                    imageView.setVisibility(View.VISIBLE);
//                                    Bitmap bit = DeviceUtil.getRoundedCornerBitmap(context,bitmap,5,bitmap.getWidth(),bitmap.getHeight(),false,false,false,false);
//                                    imageView.setImageBitmap(bit);
//                                } else {
//                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                                    imageView.setImageBitmap(bitmap);
//                                }
                                imageView.setVisibility(View.VISIBLE);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageBitmap(bitmap);
                                if (layout == R.layout.layout_ad_view_three_seven) {
                                    if (imageCoverNext != null){
                                        imageCoverNext.setImageBitmap(bitmap);
                                    }
                                }
                            }
                        }
//                            if (layout == R.layout.layout_ad_view_model_fifteen && imageType == ICON_IMAGEVIEW) {
//                                imageView.setVisibility(View.VISIBLE);
//                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                Bitmap bit = DeviceUtil.createCircleImage(bitmap);
//                                MyLog.e(MyLog.TAG, "15 rouder couner");
//                                imageView.setImageBitmap(bit);
//                            } else if (layout == R.layout.layout_ad_view_model_eightteen && imageType == ICON_IMAGEVIEW) {
//                                imageView.setVisibility(View.VISIBLE);
//                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                Bitmap bit = DeviceUtil.getAdmobIconRoundedCornerBitmap(bitmap);
//                                MyLog.e(MyLog.TAG, "18 rouder couner");
//                                imageView.setImageBitmap(bit);
//                            } else if (layout == R.layout.layout_ad_view_model_nineteen && imageType == ICON_IMAGEVIEW) {
//                                imageView.setVisibility(View.VISIBLE);
//                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                Bitmap bit = DeviceUtil.getAdmobIconRoundedCornerBitmap(bitmap);
//                                MyLog.e(MyLog.TAG, "19 rouder couner");
//                                imageView.setImageBitmap(bit);
//                            } else {
//                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                imageView.setImageBitmap(bitmap);
//                            }


                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                        }
                    },
                    new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view,
                                                     int current, int total) {
                        }
                    });
        }

    }

