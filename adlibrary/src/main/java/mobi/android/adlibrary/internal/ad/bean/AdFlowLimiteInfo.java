package mobi.android.adlibrary.internal.ad.bean;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.StorageUtils;

/**
 * Created by vincent on 2016/10/20.
 */
public class AdFlowLimiteInfo implements Serializable {
    private static final int HOURS = 60 * 60 * 1000;
    private String mFlowID;
    private float mDayTimeLimit;
    private ArrayList<Long> mDayTimeLimitAllData;
    private Context context;


    public void init(Context context, float mMTimeLimit, String flowID) {
        MyLog.e(MyLog.TAG,"  init ----mMTimeLimit:"+mMTimeLimit+"------flowID:"+flowID);
        this.context = context;
        this.mDayTimeLimit = mMTimeLimit;
        this.mFlowID = flowID;
        mDayTimeLimitAllData = getDayTimeLimitDataFromLocalFiled();//24小时总请求不超过m次
    }


    /**
     * 根据flow条件是否应该能够去获取广告
     * 24小时总请求不超过4次
     * * @return
     */
    public boolean getFlowAdSuccessedByLimit() {

        return  getMTimeLimitDataIsCanGetAd(mDayTimeLimit);
    }


    /**
     * 判断24小时FLOW 总展示不超过mMTimeLimit次
     * * @param mMTimeLimit
     *
     * @return
     */
    public boolean getMTimeLimitDataIsCanGetAd(float mMTimeLimit) {
        boolean isGetMTimeLimitAd = false;
        Long mNowTime = System.currentTimeMillis();
        int length = mDayTimeLimitAllData.size();
        MyLog.e(MyLog.TAG, "AD  FLOW  requestLength---------     " + String.valueOf(length));
        if (length > 0) {
            if (mDayTimeLimitAllData.size() < mMTimeLimit) {
                isGetMTimeLimitAd = true;
            }
        } else {
            isGetMTimeLimitAd = true;
        }

        MyLog.e(MyLog.TAG, "AD FLOW!!!! getMTimeLimitDataIsCanGetAd is:" + isGetMTimeLimitAd+"-----but still load the lastest flow!!!!");
        return isGetMTimeLimitAd;
    }

    /**
     * 从本地文件夹中获取24小时内的请求成功的广告时间点     * @return
     */
    public ArrayList<Long> getDayTimeLimitDataFromLocalFiled() {
        mDayTimeLimitAllData = (ArrayList<Long>) StorageUtils.FileToObject(context.getFilesDir() + File.separator + "Times" + mFlowID);
        if (mDayTimeLimitAllData == null) {
            mDayTimeLimitAllData = new ArrayList<Long>();
        }
        return mDayTimeLimitAllData;
    }
}
