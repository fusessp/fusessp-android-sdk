# FuseSSP Android SDK

Maximize your app's revenue streams and save time, sign up for an account at [FuseSSP.com](http://app.fusessp.com).



## Contents

* [Add the FuseSSP SDK and 3rd-party Networks to your Project](#add-the-fusessp-sdk-and-3rd-party-networks-to-your-project)
* [Modify your Android Manifest](#modify-your-android-manifest)
* [Initialization](#initialization)
* [Ad Data Obtainment](#ad-data-obtainment)
* [Ad Data Callback](#ad-data-callback)
* [Release Ad Resources](#release-ad-resources)
* [Customize Native UI](#customize-native-ui)
* [Requirements](#requirements)


## Add the FuseSSP SDK and 3rd-party Networks to your Project

1. Dependent on the project modle: application only need to add compile project (': adlibrary')

2. Dependent on the adlibrary-release.aar, can also update the 3rd party networks jar package in the build.gradle file.
```java
compile 'com.facebook.android:audience-network-sdk:4.+'
compile 'com.google.android.gms:play-services-ads:9.2.1'
compile 'com.google.firebase:firebase-ads:9.6.0'
compile 'com.mopub.volley:mopub-volley:1.1.0'
compile 'com.flurry.android:analytics:6.5.0'
compile 'com.flurry.android:ads:6.5.0'
compile('com.mopub:mopub-sdk:4.12.0@aar') {
    transitive = true
}
```

Add following rows in your app module proguard-rules.pro file:
```java
-keep public class android.webkit.JavascriptInterface {}
# Support for Android Advertiser ID.
-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}
# Application classes that will be serialized/deserialized over Gson
-keep class org.myteam.analyticssdk.jsonbean.** { *; }
 
# firebase
-keep public @com.google.android.gms.common.util.DynamiteApi class * { *; }
 
# gson
-keep class com.google.gson.** { *; }
 
 
#adlibrary
-keep class mobi.android.adlibrary.internal.ad.bean.** {*;}
 
# libanalytics
-keep class android.library.libinterface.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**
-keep class com.facebook.ads.** { *; }
-keep public class com.google.ads.internal.** {*;}
-keep public class com.google.ads.internal.AdWebView.** {*;}
-keep public class com.google.ads.internal.state.AdState {*;}
-keep public class com.google.ads.mediation.** {*;}
-keep public class com.google.ads.mediation.adfonic.** {*;}
-keep public class com.google.ads.mediation.admob.** {*;}
-keep public class com.google.ads.mediation.adfonic.util.** {*;}
-keep public class com.google.ads.mediation.customevent.** {*;}
-keep public class com.google.ads.searchads.** {*;}
-keep public class com.google.ads.util.** {*;}
-keep public class com.google.ads.** {public *;}
 
# Support for Google Play Services
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {public static final *** NULL; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}
-keep public class com.google.android.gms.ads.** {
  public *;
}
-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
 
# For using GSON @Expose annotation
-keepattributes *Annotation*
 
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class com.inmobi.** { *; }
-keepclassmembers class com.mopub.** { public *; }
-keep public class com.mopub.**
 
# Explicitly keep any custom event classes in any package.
-keep class * extends com.mopub.mobileads.CustomEventBanner {}
-keep class * extends com.mopub.mobileads.CustomEventInterstitial {}
-keep class * extends com.mopub.nativeads.CustomEventNative {}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}
# Required to preserve the Flurry SDK
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * {
    public <init> (android.content.Context, android.util.AttributeSet, int);
}
 -keep class com.google.android.gms.common.GooglePlayServicesUtil {
       public <methods>;}
 -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
       public <methods>;}
 -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
       public <methods>;}
```


## Modify your Android Manifest

Under the main <manifest> element, add the following permissions.
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"/>
```


## Initialization

##### Method:
```java
init()
```

##### Parameters:
```java
Context context//Context object.
String configUrl//Required, please use this fixed address: http://adconfig.cpp.app.fusessp.net?pubid=xxxx, note that the value of pubid needs to be replaced by the "App ID" that was generated after the app you created in FuseSSP.
String channel
String installChannel
```

##### How to use:
```java
AdAgent.getInstance().init(this, configUrl, " channel", "installChannel")
```


## Ad Data Obtainment

##### How to use:
```java
public void loadAd(Context context, Ad ad, OnAdLoadListener listener)
AdAgent.getInstance().loadAd(getApplicationContext(),ad,new OnAdLoadListener() {
            @Override
            public void onLoad(IAd iAd) {
            }
            @Override
            public void onLoadFailed(AdError adError) {
            }
            @Override
            public void onLoadInterstitialAd
(WrapInterstitialAd wrapInterstitialAd) {                   
            }
        });
```

##### Method:
```java
loadAd()
Ad ad = (new Ad.Builder(getApplicationContext(),slotId))//slotId is the "Ad Unit ID" generated after the ad unit you created in FuseSSP.
.setWidth(330)
.setHight(300)
.setParentViewGroup(ad_container)
.isPreLoad(false)
.setTransparent(false)
.setTitleColor(R.color.white)
.setSubTitleColor(R.color.white)
.setCtaTextColor(R.color.white)
.setCtaBackground(R.color.white)
.setAppSelfLayout(R.id.ad_resource_layout)
.build();
```

##### Parameters:
```java
//Ad Builder:
ViewGroup viewGroup//Parent container (used to fill ads).
boolean isPreLoad//Control whether the ad is preloaded (default: false).
boolean setTransparent//Control whether the ad background is transparent (default: false).
int adResourceLayout//The layout id of the custom template layout in the xml.  
int titlecolor//The color of the title text in the ad element.
int subTitleColor//The color of the subtitle text in the ad element.
int ctaTextColor//The color of the CTA text in the ad element.
int ctaBackground//The background color of the CTA button in the ad element.
String slotId//Required, the "Ad Unit ID" that was generated after the app's ad unit you created in FuseSSP.
int width//The width of the ad (required in Admob banner ads)
int hight//The hight of the ad (required in Admob banner ads)
```

#### Additional information

ViewGroup viewGroup

Use ```getView()``` to get ad's view: don't provide the parent container, ad returns Nativedata and adview by default, but ad's data fill and display requires your application to complete. (Through NativeData custom View need to use ```iAd.registerViewForInteraction(view)``` method to register the view in the onLoad callback process.)

Fill parent container to dispaly the ads: provide parent container, SDK will add corresponding ad data you get to the corresponding parent class container, data or view will also be provided in the callback interface ```onLoad(IAd iAd)``` method.

You can only fill the parent container to display the ads or use ```getView()``` to get ad's view, otherwise click event may be invalid.


## Ad Data Callback

##### Method:
```java
new OnAdLoadListener() {
                   @Override
                   public void onLoad(IAd iAd) {}
                   @Override
                   public void onLoadFailed(AdError adError) {}
                   @Override
                  public void onLoadInterstitialAd(WrapInterstitialAd wrapInterstitialAd) {
                                   wrapInterstitialAd.show();
                                             }}
```

##### onLoad: Native Ad and Banner Ad
```java
//IAd is the data returned after callback is successful. Below are the methods in IAd.
View getAdView();//Banner view.
AdCallBackInfo getNativeAd();//Get native data.
String getIconUrl();//Get the url of the ad icon.
void setOnCancelAdListener(OnCancelAdListener var1);//Ad close button click event callback.
void setOnAdClickListener(OnAdClickListener var1);//Ad click event callback.
void setOnAdTouchListener(OnTouchListener var1);//Ad touch event callback.
nativeAd.getPrivacyInformationIconUrl()//Adchoice icon provided by networks.
voidsetOnPrivacyIconClickListener(OnClickListener var1, View var2);//Adchoice icon click event callback.
void registerViewForInteraction(View var1);//If you use the returned data to stitch View, you need to register the displayed View (capture data and trigger event response).
void showCustomAdView();//Custom view, you need to call this method to achieve the RBI event of the ad display.
```

##### onLoad: Interstitial Ad
```java

wrapInterstitialAd.show()//Manually add.
```

##### onLoad: Failed 
```java
AdError//Error information.
enum AdError { NO_FILL, NETWORK_FAILD, NETWORK_TIME_OUT, REQUEST_FREQUENCY, OTHER, INVALID_REQUEST;}
```


## Release Ad Resources

```java
iAd.release(ad_container);//Required
ViewGroup ad_container//Load ad parent control
```


## Customize Native UI

When you create an customized native ad object, the ad element id in the custom layout of the ```setAppSelfLayout()``` method needs to be correspondent to the ad element id in the table below.

|Ad Element | Required | Corresponding Ad Element ID |
|----------|----------|----------|
|Ad icon|Yes|`R.id. icon_image_native`|
|Cover image|Yes|`R.id.ad_cover_image`|
|AD logo|Yes|`R.id.ad_icon_image2`|
|AdChoices icon|Yes|`R.id.native_ad_choices_image`|
|call to action text|Yes|`R.id.calltoaction_text`|
|Title|Yes|`R.id.ad_title_text`|
|Subtitle|Yes|`R.id.ad_subtitle_Text`|
|Facebook mediaView|No|`R.id.ad_fb_cover_image`|
|Admob mediaView|No|`R.id.ad_admob_cover_image`|


## Requirements

- Android 4.0.3 (API Version 15) and up (**Updated in 4.12.0**)
- android.support:appcompat-v7:23.1.1
- Recommended Google Play Services 9.2.1
