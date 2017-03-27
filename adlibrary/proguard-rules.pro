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

-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keep public class com.google.android.gms.ads.** {
  public *;
}

-keep public class com.google.ads.** {
  public *;
}
-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
# for dl provided depandency
-keep class com.google.gson.Gson {
    public *;
}
-keep class com.google.gson.reflect.TypeToken { *; }

-keep class com.inmobi.** { *; }
-dontwarn com.inmobi.**


-keepclassmembers class com.mopub.** { public *; }
-keep public class com.mopub.**
-keep public class android.webkit.JavascriptInterface {}

# Explicitly keep any custom event classes in any package.
-keep class * extends com.mopub.mobileads.CustomEventBanner {}
-keep class * extends com.mopub.mobileads.CustomEventInterstitial {}
-keep class * extends com.mopub.nativeads.CustomEventNative {}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# Support for Android Advertiser ID.
-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

# Support for Google Play Services
# http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}


# Required to preserve the Flurry SDK
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclasseswithmembers class * {
    public <init> (android.content.Context, android.util.AttributeSet, int);
}

# For VK SDK
-keep class com.my.target.** {*;}

# Required if you are using the InMobi SDK:
-keep class com.inmobi.** { *; }
-dontwarn com.inmobi.**

# Required if you are using the Millennial SDK:
-keep class com.millennialmedia.** { *; }
-dontwarn com.millennialmedia.**


# baidu
-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.content.BroadcastReceiver

 -keep class com.dianxinos.DXStatService.stat.TokenManager {
 public static java.lang.String getToken(android.content.Context);
 }

 -keepnames @com.google.android.gms.common.annotation.KeepName class *
 -keepclassmembernames class * {
         @com.google.android.gms.common.annotation.KeepName *;}
 -keep class com.google.android.gms.common.GooglePlayServicesUtil {
       public <methods>;}
 -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
       public <methods>;}
 -keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
       public <methods>;}
