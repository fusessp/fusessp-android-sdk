package mobi.android.adlibrary.internal.ad.nativeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;
import java.util.List;

import mobi.android.adlibrary.R;
import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.utils.StringUtil;

/**
 * Created by vincent on 2016/1/22.
 */
public class NativeAdView {
    private View vContainerView;
    private ImageView vAdIconImage;
    private ImageView vIconImage;
    private ImageView vAdChocesImage;
    private ImageView vCoverImage;
    private MediaView vMediaView;
    private ImageView vCloseImage;
    private TextView vTitleText;
    private TextView vSubTitleText;
    private View vCallToActionText;
    private TextView vDescriptionText;

    private RatingBar vAppraiseStar;
    private NativeAdData mNativeAd;
    private DisplayImageOptions options;
    private Context context;
    //    private AdChoicesView adChoicesView;
    private View vParentView;
    private ImageView vCoverImageNext;

    public View renderView(Context context, int layout, NativeAdData nativeAd, ViewGroup parentViews) {
        if (nativeAd == null) {
            return null;
        }
        this.context = context;
        mNativeAd = nativeAd;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (parentViews == null) {
            parentViews = new LinearLayout(context);
            parentViews.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        vParentView = inflater.inflate(layout, parentViews, false);
        initView(vParentView);
        initData(nativeAd, layout, parentViews);
        return vParentView;
    }

    public void initView(View parentView) {
        vContainerView = parentView.findViewById(R.id.rl_view_container);
        vIconImage = (ImageView) parentView.findViewById(R.id.icon_image_native);
        vAdIconImage = (ImageView) parentView.findViewById(R.id.ad_icon_image);
        vAdChocesImage = (ImageView) parentView.findViewById(R.id.native_ad_choices_image);
        vCoverImage = (ImageView) parentView.findViewById(R.id.ad_cover_image);
        vMediaView = (MediaView) parentView.findViewById(R.id.ad_fb_cover_image);
        vCloseImage = (ImageView) parentView.findViewById(R.id.close_image);
        vTitleText = (TextView) parentView.findViewById(R.id.ad_title_text);
        vSubTitleText = (TextView) parentView.findViewById(R.id.ad_subtitle_Text);
        vDescriptionText = (TextView) parentView.findViewById(R.id.ad_description_Text);
        vCallToActionText = parentView.findViewById(R.id.calltoaction_text);
        vAppraiseStar = (RatingBar) parentView.findViewById(R.id.ad_ratingbar);
        vCoverImageNext = (ImageView) parentView.findViewById(R.id.ad_cover_image_next);
    }

    public void initData(NativeAdData nativeAd, final int layout, ViewGroup parentViews) {
        options = new DisplayImageOptions.Builder()
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
        if (nativeAd == null) {
            return;
        }
        NativeAd nativeFbAd = null;
        if (nativeAd.getAdObject() instanceof NativeAd) {
            nativeFbAd = (NativeAd) nativeAd.getAdObject();
        }
        if (vMediaView != null && nativeFbAd != null) {
            vMediaView.setVisibility(View.VISIBLE);
            vMediaView.setNativeAd(nativeFbAd);
            if (vCoverImage != null) {
                vCoverImage.setVisibility(View.GONE);
            }
        } else {
            if (vCoverImage != null) {
                String coverUrl = "";
                if (StringUtil.isEmpty(nativeAd.getCoverImageUrl())) {
                    coverUrl = nativeAd.getIconImageUrl();
                } else {
                    coverUrl = nativeAd.getCoverImageUrl();
                }
                vCoverImage.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(coverUrl, vCoverImage, options,
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
                                if (bitmap != null) {
                                    vCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    vCoverImage.setImageBitmap(bitmap);
                                    if (layout == R.layout.layout_ad_view_three_seven) {
                                        if (vCoverImageNext != null) {
                                            vCoverImageNext.setImageBitmap(bitmap);
                                        }
                                    }
                                }
                            }

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

        if (nativeAd.mNode.mTilteColor != 0 && vTitleText != null && vContainerView != null && nativeAd != null && nativeAd.mNode != null) {
            vTitleText.setTextColor(context.getResources().getColor(nativeAd.mNode.mTilteColor));
        }

        if (nativeAd.mNode.mSubTitleColor != 0 && vSubTitleText != null && vContainerView != null && nativeAd != null && nativeAd.mNode != null) {
            vSubTitleText.setTextColor(context.getResources().getColor(nativeAd.mNode.mSubTitleColor));
        }
        if (vContainerView != null) {
            if (nativeAd.mNode.transparent) {
                vContainerView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            } else {
                vContainerView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

        if (vTitleText != null) {
            vTitleText.setText(nativeAd.getTitle());
        }
        if (vSubTitleText != null) {
            vSubTitleText.setText(nativeAd.getSubtitle());
        }

        if (vCallToActionText != null && vCallToActionText instanceof TextView) {
            ((TextView) vCallToActionText).setText(nativeAd.getCallToActionText());
            if (vContainerView != null && nativeAd != null && nativeAd.mNode != null) {
                if (nativeAd.mNode.mButtonColor != 0) {
                    ((TextView) vCallToActionText).setTextColor(context.getResources().getColor(nativeAd.mNode.mButtonColor));
                }
                if (nativeAd.mNode.ctaBackground != 0) {
                    vCallToActionText.setBackgroundResource(nativeAd.mNode.ctaBackground);
                }
            }
        }

        if (vAppraiseStar != null) {
            float rating = (float) nativeAd.getStarRating();
            vAppraiseStar.setRating(rating);
            LayerDrawable stars = (LayerDrawable) vAppraiseStar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD738"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.parseColor("#B7B7B7"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(Color.parseColor("#B7B7B7"), PorterDuff.Mode.SRC_ATOP);
        }
        if (vCloseImage != null) {
            mNativeAd.setAdCancelListener(context, vCloseImage);
        }

        if (vIconImage != null) {
            ImageLoader.getInstance().displayImage(nativeAd.getIconImageUrl(), vIconImage, options,
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
                            vIconImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            vIconImage.setImageBitmap(bitmap);
                        }

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
        if (vAdChocesImage != null) {
            nativeAd.handlePrivacyIconClick(context, vAdChocesImage);
            ImageLoader.getInstance().displayImage(nativeAd.getPrivacyInformationIconUrl(), vAdChocesImage, options,
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
                            if (bitmap != null) {
                                vAdChocesImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                vAdChocesImage.setImageBitmap(bitmap);
                            }
                        }

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


    public List<View> getAllAdElementView() {
        List<View> allAdElementViews = new ArrayList<View>();
        if (vIconImage != null) {
            allAdElementViews.add(vIconImage);
        }
        if (vAdIconImage != null) {
            allAdElementViews.add(vAdIconImage);
        }
        if (vAdChocesImage != null) {
            allAdElementViews.add(vAdChocesImage);
        }
        if (vMediaView != null) {
            allAdElementViews.add(vMediaView);
        }
        if (vCloseImage != null) {
            allAdElementViews.add(vCloseImage);
        }
        if (vTitleText != null) {
            allAdElementViews.add(vTitleText);
        }
        if (vSubTitleText != null) {
            allAdElementViews.add(vSubTitleText);
        }
        if (vDescriptionText != null) {
            allAdElementViews.add(vDescriptionText);
        }
        if (vCallToActionText != null) {
            allAdElementViews.add(vCallToActionText);
        }
        if (vAppraiseStar != null) {
            allAdElementViews.add(vAppraiseStar);
        }

        return allAdElementViews;
    }

    public View getActionAdElementView() {
        if (vCallToActionText != null) {
            return vCallToActionText;
        } else {
            return null;
        }
    }

    public View getAdParentView() {
        if (vParentView != null) {
            return vParentView;
        } else {
            return null;
        }
    }


}
