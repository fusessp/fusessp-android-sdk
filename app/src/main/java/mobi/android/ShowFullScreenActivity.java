package mobi.android;

import android.graphics.BitmapFactory;
import android.os.Bundle;
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


public class ShowFullScreenActivity extends AppCompatActivity {
    private RelativeLayout vAdContainer01;

    private TextView vAdContainer01Title;


    private String mSlotId;
    private String mTypeTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_show);

        vAdContainer01 = (RelativeLayout) findViewById(R.id.ad_fullscreen_container);
        vAdContainer01Title = (TextView) findViewById(R.id.ad_fullscreen_title_text);
        MyLog.DEBUG_MODE = true;

        mSlotId = getIntent().getStringExtra("slotId");
        mTypeTitle = getIntent().getStringExtra("typeTitle");

        vAdContainer01Title.setText(mTypeTitle);
        addAd(mSlotId);
    }

    public void addAd(final String slotId) {
        vAdContainer01.removeAllViews();
        Ad ad = (new Ad.Builder(getApplicationContext(), slotId))
                .setParentViewGroup(vAdContainer01)
                .setTransparent(true)
                .build();

        AdAgent.getInstance().loadAd(getApplicationContext(), ad, new OnAdLoadListener() {

            @Override
            public void onLoad(IAd iAd) {
                vAdContainer01.setBackgroundColor(getResources().getColor(R.color.black));
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
                vAdContainer01.removeAllViews();
                ImageView imageView = new ImageView(ShowFullScreenActivity.this);
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_ad));
                vAdContainer01.addView(imageView);
            }

            @Override
            public void onLoadInterstitialAd(WrapInterstitialAd wrapInterstitialAd) {
                MyLog.i(MyLog.TAG, "addAd--onLoadInterstitialAd");
                wrapInterstitialAd.show();
                ShowFullScreenActivity.this.finish();
            }
        });
    }
}
