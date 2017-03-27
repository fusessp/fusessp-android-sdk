package mobi.android.adlibrary.internal.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import mobi.android.adlibrary.AdAgent;
import mobi.android.adlibrary.internal.ad.bean.AdPlacementConfig;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.utils.MyLog;


/**
 * Created by Administrator on 2016/6/22.
 */
public class AdPreloadService extends IntentService {
    public final static int LOAD_SERVICE_STATIC = 0;

    private static final int RELOAD_INTERVAL_TIME =  5 * 60 * 1000 ;

    public AdPreloadService(){
        super(AdPreloadService.class.getName());
    }

    public static void startPreloadService(Context context) {
        MyLog.d(MyLog.TAG, "startPreloadService");
        Intent alarmIntent = new Intent(context, AdPreloadService.class);
        alarmIntent.setAction(AdConstants.ACTION_REFRESH_CACHE);
        PendingIntent pintent = PendingIntent.getService(context, 2, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int pendingTime = (int) (SystemClock.elapsedRealtime() + 5 * 1000);
        int repeatTime = RELOAD_INTERVAL_TIME;
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, pendingTime, repeatTime, pintent);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        MyLog.d(MyLog.TAG, "AdPreloadService:onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyLog.d(MyLog.TAG, "AdPreloadService:onHandleIntent");
        if (intent == null) {
            return;
        }
        String mAction = intent.getAction();
        if (AdConstants.ACTION_LOAD_CONFIG.equals(mAction)) {
            AdAgent.getInstance().loadAdConfig();
        }else if(AdConstants.ACTION_ADMOB_INTER.equals(mAction)){
            MyLog.d(MyLog.TAG,"InterstitialAd--ACTION_ADMOB_INTER");
            AdPlacementConfig.InterAd interAd = AdConfigLoader.getInstance(this).getAdmobInterAdConfig();
            if(interAd == null){
                MyLog.d(MyLog.TAG,"InterstitialAd--ser配置的数据为空");
                return;
            }
            if(!interAd.enable){
                MyLog.d(MyLog.TAG,"InterstitialAd--ser当前的功能关闭");
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.d(MyLog.TAG,"onDestroy");
    }

}
