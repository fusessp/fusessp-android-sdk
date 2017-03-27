package mobi.android.adlibrary.internal.ad.bean;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import mobi.android.adlibrary.internal.utils.MyLog;
import mobi.android.adlibrary.internal.utils.StorageUtils;


/**
 * Created by vincent on 2016/3/29.
 */
public class AdLimitAdInfo implements Serializable {

    private static final int HOURS = 60 * 60 * 1000;

    private String adId;
    private float mNHourLimit;
    private float mMTimeLimit;
    private ArrayList<Long> mNHourLimitAllData;
    private ArrayList<Long> mMTimeLimitAllData;
    private Context context;

    public void init(Context context, float mNHourLimit, float mMTimeLimit, String adId) {
        this.context = context;
        this.mNHourLimit = mNHourLimit;
        this.mMTimeLimit = mMTimeLimit;
        this.adId = adId;
        mNHourLimitAllData = getNLimitDataFromLocalFiled();//间隔n小时请求1次
        mMTimeLimitAllData = getMTimeLimitDataFromLocalFiled();//24小时总请求不超过m次
    }

    /**
     * 根据条件是否应该能够去获取广告
     * 条件--间隔2小时请求1次+24小时总请求不超过4次
     * * @return
     */
    public boolean getAdSuccessedByLimit() {

        return getNHourLimitDataIsCanGetAd(mNHourLimit) &&
                getMTimeLimitDataIsCanGetAd(mMTimeLimit);
    }

    /**
     * 判断间隔mNHourLimit小时请求1次是否正确
     *
     * @param mNHourLimit
     * @return
     */
    public boolean getNHourLimitDataIsCanGetAd(float mNHourLimit) {
        boolean isGetNLimitAd = false;
        Long mNowTime = System.currentTimeMillis();
        if (mNHourLimitAllData.size() > 0) {
            long mLastTime = mNHourLimitAllData.get(0);
            MyLog.d(MyLog.TAG, "mNowTime" + String.valueOf(mNowTime) + "---mLastTime" + String.valueOf(mLastTime) + "-------差值:" + String.valueOf(mNowTime - mLastTime)+"-- - mNHourLimit"+String.valueOf(mNHourLimit*HOURS));

            if (mNowTime - mLastTime > mNHourLimit * HOURS) {
                isGetNLimitAd = true;
            }
        } else {
            isGetNLimitAd = true;
        }
        MyLog.d(MyLog.TAG, "AD getNHourLimitDataIsCanGetAd is:" + isGetNLimitAd);
        return isGetNLimitAd;
    }


    /**
     * 判断24小时总展示不超过mMTimeLimit次
     *
     * @return
     */
    public boolean addShowtime() {
        Long mNowTime = System.currentTimeMillis();
        int length = mMTimeLimitAllData.size();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                long beforeTime = mMTimeLimitAllData.get(i);
                if (mNowTime - beforeTime > 24 * HOURS) {
                    mMTimeLimitAllData.remove(i);
                    i--;
                    length = mMTimeLimitAllData.size();
                }
            }
            mMTimeLimitAllData.add(mNowTime);
            StorageUtils.serializeToFile(mMTimeLimitAllData, context.getFilesDir() + File.separator + "Times" + adId);
            mNHourLimitAllData.clear();
            mNHourLimitAllData.add(mNowTime);
            StorageUtils.serializeToFile(mNHourLimitAllData, context.getFilesDir() + File.separator + "Hours" + adId);
            return true;
        } else {
            mMTimeLimitAllData.add(mNowTime);
            StorageUtils.serializeToFile(mMTimeLimitAllData, context.getFilesDir() + File.separator + "Times" + adId);
            mNHourLimitAllData.clear();
            mNHourLimitAllData.add(mNowTime);
            StorageUtils.serializeToFile(mNHourLimitAllData, context.getFilesDir() + File.separator + "Hours" + adId);
            return true;
        }

    }


    /**
     * 判断24小时总展示不超过mMTimeLimit次     * @param mMTimeLimit
     *
     * @return
     */
    public boolean getMTimeLimitDataIsCanGetAd(float mMTimeLimit) {
        boolean isGetMTimeLimitAd = false;
        int length = mMTimeLimitAllData.size();
        MyLog.d(MyLog.TAG, "requestLength---------" + String.valueOf(length));
        if (length > 0) {
            if (mMTimeLimitAllData.size() < mMTimeLimit) {
                isGetMTimeLimitAd = true;
            }
        } else {
            isGetMTimeLimitAd = true;
        }
        MyLog.d(MyLog.TAG, "AD getMTimeLimitDataIsCanGetAd is:" + isGetMTimeLimitAd);
        return isGetMTimeLimitAd;
    }

    /**
     * 从本地文件中获取上次请求广告成功的时间点
     *
     * @return
     */
    public ArrayList<Long> getNLimitDataFromLocalFiled() {
        mNHourLimitAllData = (ArrayList<Long>) StorageUtils.FileToObject(context.getFilesDir() + File.separator + "Hours" + adId);
        if (mNHourLimitAllData == null) {
            mNHourLimitAllData = new ArrayList<Long>();
        }
        return mNHourLimitAllData;
    }

    /**
     * 从本地文件夹中获取24小时内的请求成功的广告时间点
     * * @return
     */
    public ArrayList<Long> getMTimeLimitDataFromLocalFiled() {
        mMTimeLimitAllData = (ArrayList<Long>) StorageUtils.FileToObject(context.getFilesDir() + File.separator + "Times" + adId);
        if (mMTimeLimitAllData == null) {
            mMTimeLimitAllData = new ArrayList<Long>();
        }
        return mMTimeLimitAllData;
    }
}
