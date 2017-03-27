package mobi.android;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.ad.Ad;
import mobi.android.adlibrary.internal.ad.AdError;
import mobi.android.adlibrary.internal.ad.IAd;
import mobi.android.adlibrary.internal.ad.OnAdClickListener;
import mobi.android.adlibrary.internal.ad.OnAdLoadListener;
import mobi.android.adlibrary.internal.ad.OnCancelAdListener;
import mobi.android.adlibrary.internal.ad.WrapInterstitialAd;
import mobi.android.adlibrary.internal.utils.MyLog;


public class ShowActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout vAdContainer01;
    private RelativeLayout vAdContainer02;
    private RelativeLayout vAdContainer03;
    private RelativeLayout vAdContainer04;
    private RelativeLayout vAdContainer05;
    private RelativeLayout vAdContainer06;
    private RelativeLayout vAdContainer07;

    private TextView vAdContainer01Title;
    private TextView vAdContainer02Title;
    private TextView vAdContainer03Title;
    private TextView vAdContainer04Title;
    private TextView vAdContainer05Title;
    private TextView vAdContainer06Title;
    private TextView vAdContainer07Title;

    private RelativeLayout[] vAllContainers;
    private TextView[] vAllContainersTitle;

    private Handler handler;
    private boolean mIsGetView;
    private int layoutId;
    private String[] mSlotIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        vAdContainer01 = (RelativeLayout) findViewById(R.id.ad_container01);
        vAdContainer02 = (RelativeLayout) findViewById(R.id.ad_container02);
        vAdContainer03 = (RelativeLayout) findViewById(R.id.ad_container03);
        vAdContainer04 = (RelativeLayout) findViewById(R.id.ad_container04);
        vAdContainer05 = (RelativeLayout) findViewById(R.id.ad_container05);
        vAdContainer06 = (RelativeLayout) findViewById(R.id.ad_container06);
        vAdContainer07 = (RelativeLayout) findViewById(R.id.ad_container07);

        vAdContainer01Title = (TextView) findViewById(R.id.ad_container01_title);
        vAdContainer02Title = (TextView) findViewById(R.id.ad_container02_title);
        vAdContainer03Title = (TextView) findViewById(R.id.ad_container03_title);
        vAdContainer04Title = (TextView) findViewById(R.id.ad_container04_title);
        vAdContainer05Title = (TextView) findViewById(R.id.ad_container05_title);
        vAdContainer06Title = (TextView) findViewById(R.id.ad_container06_title);
        vAdContainer07Title = (TextView) findViewById(R.id.ad_container07_title);

        vAllContainers = new RelativeLayout[]{vAdContainer01, vAdContainer02, vAdContainer03,
                vAdContainer04, vAdContainer05, vAdContainer06, vAdContainer07};
        vAllContainersTitle = new TextView[]{vAdContainer01Title, vAdContainer02Title, vAdContainer03Title,
                vAdContainer04Title, vAdContainer05Title, vAdContainer06Title, vAdContainer07Title};

        MyLog.DEBUG_MODE = true;

        handler = new Handler();

        mIsGetView = getIntent().getBooleanExtra("isGetView", false);
        layoutId = getIntent().getIntExtra("isCustomization", 0);
        mSlotIds = getIntent().getStringArrayExtra("slotId");

        if (layoutId != 0) {
            addAdByCustomization(mSlotIds, layoutId);
            return;
        }

        if (mIsGetView) {
            addAdByGetView(mSlotIds);
            return;
        }

        addAds(mSlotIds);

        vAdContainer06Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityToFullScreen(5);
            }
        });
        vAdContainer07Title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityToFullScreen(6);
            }
        });
    }

    public void activityToFullScreen(int type) {
        Intent intent = new Intent();
        intent.setClass(ShowActivity.this, ShowFullScreenActivity.class);
        intent.putExtra("slotId", mSlotIds[type]);
        intent.putExtra("typeTitle", mSlotIds[type]);
        startActivity(intent);
    }

    public void addAds(final String[] slotIds) {
        for (int i = 0; i < slotIds.length; i++) {
            vAllContainers[i].removeAllViews();
            Ad ad = (new Ad.Builder(getApplicationContext(), slotIds[i]))
                    .setParentViewGroup(vAllContainers[i])
                    .build();
            if (ad.getAdNode() != null) {
                if (i == 5) {
                    vAllContainersTitle[i].setBackgroundColor(getResources().getColor(R.color.black_transparent40));
                    vAllContainersTitle[i].setText(getResources().getText(R.string.click_show_fullscreen_01));
                    continue;
                } else if (i == 6) {
                    vAllContainersTitle[i].setBackgroundColor(getResources().getColor(R.color.black_transparent40));
                    vAllContainersTitle[i].setText(getResources().getText(R.string.click_show_fullscreen_02));
                    continue;
                } else {
                    vAllContainersTitle[i].setText(ad.getAdNode().slot_name);
                }
            }
            final int j = i;
            AdAgent.getInstance().loadAd(getApplicationContext(), ad, new OnAdLoadListener() {

                @Override
                public void onLoad(IAd iAd) {

                    iAd.setOnAdClickListener(new OnAdClickListener() {
                        @Override
                        public void onAdClicked() {
                            MyLog.i(MyLog.TAG, "addAd--OnAdClickListener");
                        }
                    });
                    iAd.setOnCancelAdListener(new OnCancelAdListener() {
                        @Override
                        public void cancelAd() {
                            MyLog.i(MyLog.TAG, "addAd--setOnCancelAdListener");
                        }
                    });
                    iAd.setOnPrivacyIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyLog.i(MyLog.TAG, "addAd--setOnPrivacyIconClickListener");
                        }
                    });
//                    iAd.release(vAdContainer01);
                }

                @Override
                public void onLoadFailed(AdError adError) {
                    MyLog.i(MyLog.TAG, "adError:  " + adError.toString());
                    vAllContainers[j].removeAllViews();
                    ImageView imageView = new ImageView(ShowActivity.this);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_ad));
                    vAllContainers[j].addView(imageView);
                }

                @Override
                public void onLoadInterstitialAd(WrapInterstitialAd wrapInterstitialAd) {
                    vAllContainers[j].removeAllViews();
                    MyLog.i(MyLog.TAG, "addAd--onLoadInterstitialAd");
                    wrapInterstitialAd.show();
                    ShowActivity.this.finish();
                }
            });
        }
    }


    public void addAdByGetView(final String[] slotIds) {
        for (int i = 0; i < slotIds.length; i++) {
            vAllContainers[i].removeAllViews();
            Ad ad = (new Ad.Builder(getApplicationContext(), slotIds[i]))
                    .build();
            if (ad.getAdNode() != null) {
                vAllContainersTitle[i].setText(ad.getAdNode().slot_name);
            }

            final int h = i;
            AdAgent.getInstance().loadAd(getApplicationContext(), ad, new OnAdLoadListener() {

                @Override
                public void onLoad(IAd iAd) {
                    vAllContainers[h].addView(iAd.getAdView());
                    iAd.showCustomAdView();

                    iAd.setOnAdClickListener(new OnAdClickListener() {
                        @Override
                        public void onAdClicked() {
                            MyLog.i(MyLog.TAG, "addAd--OnAdClickListener");
                        }
                    });
                    iAd.setOnCancelAdListener(new OnCancelAdListener() {
                        @Override
                        public void cancelAd() {
                            MyLog.i(MyLog.TAG, "addAd--setOnCancelAdListener");
                        }
                    });
                    iAd.setOnPrivacyIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyLog.i(MyLog.TAG, "addAd--setOnPrivacyIconClickListener");
                        }
                    });

                }

                @Override
                public void onLoadFailed(AdError adError) {
                    MyLog.i(MyLog.TAG, "adError:  " + adError.toString());
                    vAllContainers[h].removeAllViews();
                    ImageView imageView = new ImageView(ShowActivity.this);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_ad));
                    vAllContainers[h].addView(imageView);
                }

                @Override
                public void onLoadInterstitialAd(WrapInterstitialAd wrapInterstitialAd) {
                    vAllContainers[h].removeAllViews();
                    MyLog.i(MyLog.TAG, "addAd--onLoadInterstitialAd");
                    wrapInterstitialAd.show();
                    ShowActivity.this.finish();
                }
            });
        }
    }

    public void addAdByCustomization(final String[] slotIds, int layoutId) {
        for (int i = 0; i < slotIds.length; i++) {
            vAllContainers[i].removeAllViews();

            Ad ad = (new Ad.Builder(getApplicationContext(), slotIds[i]))
                    .setParentViewGroup(vAllContainers[i])
                    .setAppSelfLayout(layoutId)
                    .build();
            if (ad.getAdNode() != null) {
                vAllContainersTitle[i].setText(ad.getAdNode().slot_name);
            }
            final int finalI = i;

            AdAgent.getInstance().loadAd(getApplicationContext(), ad, new OnAdLoadListener() {

                @Override
                public void onLoad(IAd iAd) {

                    iAd.setOnAdClickListener(new OnAdClickListener() {
                        @Override
                        public void onAdClicked() {
                            MyLog.i(MyLog.TAG, "addAd--OnAdClickListener");
                        }
                    });
                    iAd.setOnCancelAdListener(new OnCancelAdListener() {
                        @Override
                        public void cancelAd() {
                            MyLog.i(MyLog.TAG, "addAd--setOnCancelAdListener");
                        }
                    });
                    iAd.setOnPrivacyIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyLog.i(MyLog.TAG, "addAd--setOnPrivacyIconClickListener");
                        }
                    });
                    iAd.release(vAdContainer01);
                }

                @Override
                public void onLoadFailed(AdError adError) {
                    MyLog.i(MyLog.TAG, "adError:  " + adError.toString());
                    vAllContainers[finalI].removeAllViews();
                    ImageView imageView = new ImageView(ShowActivity.this);
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_ad));
                    vAllContainers[finalI].addView(imageView);
                }

                @Override
                public void onLoadInterstitialAd(WrapInterstitialAd wrapInterstitialAd) {
                    vAllContainers[finalI].removeAllViews();
                    MyLog.i(MyLog.TAG, "addAd--onLoadInterstitialAd");
                    wrapInterstitialAd.show();
                    ShowActivity.this.finish();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {

    }
}
