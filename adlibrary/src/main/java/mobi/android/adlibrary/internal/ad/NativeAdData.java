package mobi.android.adlibrary.internal.ad;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.ad.bean.Flow;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by vincent on 2016/5/12.
 */
public abstract class NativeAdData {
    public long mExpired;
    public View.OnClickListener privacyIconClickListener;
    public AdNode mNode;
    public OnCancelAdListener cancelListener;
    public OnAdClickListener onAdClickListener;
    public int mFlowIndex;//this data belong to which flow.
    public Flow mFlow;
    protected long mStartRequetTime;
    protected String mSessionID;
    protected double mRating = 4.5;
    private long mTime;
    private int mVisitCount;
    private int mAdType;
    private boolean isClicked;
    private boolean isShowed;
    private boolean mIsTransparent;
    protected NativeAdData() {
        mStartRequetTime = System.currentTimeMillis();
        mTime = SystemClock.elapsedRealtime();
        mVisitCount = 0;

    }

    public boolean getIsClicked() {
        return isClicked;
    }

    public boolean getIsShowed(){
//        MyLog.d(MyLog.TAG,"getIsShowed  already showed ad session"+mSessionID+"-----nodeid"+mNode.slot_id);
        return isShowed;
    }

    public  void setIsClickedTrue(){
        isClicked = true;
    }

    public void setIsShowed(){
//        MyLog.d(MyLog.TAG,"set IsShowed  already showed ad session"+mSessionID+"-----nodeid"+mNode.slot_id);
        isShowed = true;
    }
    public boolean getmExpired() {
        long duration = SystemClock.elapsedRealtime() - mTime;
        if (duration >= mExpired || isClicked || isShowed){
            if (isClicked ){
                MyLog.d(MyLog.TAG,"缓存已点击  删除广告" +"clicked:"+isClicked+ "-----adtpye:"+getAdType());
            }else if (isShowed){
                MyLog.d(MyLog.TAG,"缓存已展示  删除广告"+"show:"+isShowed+ "-----adtpye:"+getAdType());
            }else {
                MyLog.d(MyLog.TAG,"缓存超时 删除广告");
                MyLog.d(MyLog.TAG,"duration: " +duration+  " = elapsedRealtime:" +SystemClock.elapsedRealtime() +
                        " -mTime:"+mTime+ "-----adtpye:"+getAdType()+"------超时时间："+ mExpired);
            }

            return true;
        }
        return false;
    }

    /**
     * this natvie data is expire or not(create object time to now)
     * @return
     */
    public boolean getNavtiveDataExpired() {
        long duration = System.currentTimeMillis() - mStartRequetTime;
        return duration >= mExpired;
    }

    public int getAdType() {
        return mAdType;
    }

    public void setAdType(int type) {
        mAdType = type;
    }

    public int getVisitCount() {
        return mVisitCount;
    }

    public NativeAdData increaseVisit() {
        mVisitCount++;
        return this;
    }
    public  long getStartRequestTime(){return mStartRequetTime;}

    public  String getCoverImageUrl(){return  null;}

    public  String getIconImageUrl(){return null;}

    public  String getSubtitle(){return null;}

    public  double getStarRating(){
          return mRating;
    }

    public  String getTitle(){return  null;}

    public  String getCallToActionText(){return null;}

    public  Object getAdObject(){return null;}

    public  String getId(){return null;}

    public  void setAdView(View adView){}

    public  void registerViewForInteraction(View view, View buttonView){}

    public  String getPrivacyInformationIconUrl(){ return null;}

    public  String getPrivacyInformationIconClickThroughUrl(){return null;}

    public  void handlePrivacyIconClick(Context context, View view){}

    public  void setAdClickListener(OnAdClickListener listener){}

    public  void setAdCancelListener(Context context, View view){}

    public  void setAdTouchListener(View.OnTouchListener listener){}

    public  void setPrivacyIconClickListener(View.OnClickListener listener){}

    public  String getSessionID(){
        return mSessionID;
    }
    public  void destroy(){}

    public boolean ismIsTransparent() {
        return mIsTransparent;
    }

    public void setmIsTransparent(boolean mIsTransparent) {
        this.mIsTransparent = mIsTransparent;
    }

    @Override
    public int hashCode() {
        return mSessionID.hashCode();
    }
}
