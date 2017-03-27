package mobi.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.utils.MyLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String loadUrl = "http://192222.168.5.222:24317/v3/config?pubid=10010";

    private Button vNativeSmallButton;
    private Button vNativeMediumButton;
    private Button vNativeLargeButton;
    private Button vNativeCustomizationButton;
    private Button vInterstitialAdmobButton;
    private Button vInterstitialFacebookButton;
    private Button vBannerAdmob;
    private Button vBannerMoupb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        vNativeSmallButton = (Button) findViewById(R.id.NativeAdsSmall);
        vNativeMediumButton = (Button) findViewById(R.id.NativeAdsMedium);
        vNativeLargeButton = (Button) findViewById(R.id.NativeAdsLarge);
        vNativeCustomizationButton = (Button) findViewById(R.id.NativeAdsCustomize);
        vInterstitialAdmobButton = (Button) findViewById(R.id.InterstitialAdsAdmob);
        vInterstitialFacebookButton = (Button) findViewById(R.id.InterstitialAdsFacebook);
        vBannerAdmob = (Button) findViewById(R.id.BannerAdsAdmob);
        vBannerMoupb = (Button) findViewById(R.id.BannerAdsMopub);

        vNativeSmallButton.setOnClickListener(this);
        vNativeMediumButton.setOnClickListener(this);
        vNativeLargeButton.setOnClickListener(this);
        vNativeCustomizationButton.setOnClickListener(this);
        vInterstitialAdmobButton.setOnClickListener(this);
        vInterstitialFacebookButton.setOnClickListener(this);
        vBannerAdmob.setOnClickListener(this);
        vBannerMoupb.setOnClickListener(this);

        AdAgent.getInstance().init(this, loadUrl, "lite", "lite");
        MyLog.DEBUG_MODE = true;
    }

    public void showAcitvity(String[] slotIds, boolean isGetView, int layoutid) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ShowActivity.class);
        intent.putExtra("slotId", slotIds);
        intent.putExtra("isGetView", isGetView);
        intent.putExtra("isCustomization", layoutid);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.NativeAdsSmall:
                String[] small = new String[]{"100016"};
                showAcitvity(small, false, 0);
                break;
            case R.id.NativeAdsMedium:
                String[] medium = new String[]{"100023", "100024", "100025", "100026"};
                showAcitvity(medium, false, 0);
                break;
            case R.id.NativeAdsLarge:
                String[] large = new String[]{"100027", "100028", "100029", "100030", "100031", "100032", "100033"};
                showAcitvity(large, false, 0);
                break;
            case R.id.NativeAdsCustomize:
                String[] customization = new String[]{"100034"};
                showAcitvity(customization, false, R.layout.layout_ad_view_one_five);
                break;
            case R.id.InterstitialAdsAdmob:
                String[] interstitialAdsAdmob = new String[]{"100041"};
                showAcitvity(interstitialAdsAdmob, false, 0);
                break;
            case R.id.InterstitialAdsFacebook:
                String[] interstitialAdsFacebook = new String[]{"100042"};
                showAcitvity(interstitialAdsFacebook, false, 0);
                break;
            case R.id.BannerAdsMopub:
                String[] bannerAdsMopub = new String[]{"100035"};
                showAcitvity(bannerAdsMopub, false, 0);
                break;
            case R.id.BannerAdsAdmob:
                String[] bannerAdsAdmob = new String[]{"100036"};
                showAcitvity(bannerAdsAdmob, false, 0);
                break;
            default:
                break;
        }
    }
}
