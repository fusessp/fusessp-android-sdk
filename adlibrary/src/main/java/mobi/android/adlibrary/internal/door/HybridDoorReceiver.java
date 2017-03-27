package mobi.android.adlibrary.internal.door;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import mobi.android.adlibrary.internal.utils.MyLog;


public class HybridDoorReceiver extends BroadcastReceiver {
    private static final String TAG = "HybridDoorReceiver";

    // adb shell am broadcast -n mobi.wifi.toolbox/mobi.wifi.adlibrary.door.HybridDoorReceiver -a mobi.wifi.adlibrary.ACTION_OPEN_DOOR_HYBRID_CONFIG
    // adb shell am broadcast -n mobi.wifi.toolbox/mobi.wifi.adlibrary.door.HybridDoorReceiver -a mobi.wifi.adlibrary.ACTION_CLOSE_DOOR_HYBRID_CONFIG
    // spc packgename
    // adb shell am broadcast -n mobi.supo.cleaner.pro/mobi.android.adlibrary.internal.door.HybridDoorReceiver -a mobi.wifi.adlibrary.ACTION_OPEN_MYLOG
    // adb shell am broadcast -n mobi.android/mobi.android.adlibrary.internal.door.HybridDoorReceiver -a mobi.wifi.adlibrary.ACTION_CLOSE_MYLOG

    private static final String ACTION_OPEN_DOOR_HYBRID_CONFIG = "mobi.wifi.adlibrary.ACTION_OPEN_DOOR_HYBRID_CONFIG";
    private static final String ACTION_CLOSE_DOOR_HYBRID_CONFIG = "mobi.wifi.adlibrary.ACTION_CLOSE_DOOR_HYBRID_CONFIG";
    private static final String ACTION_OPEN_MYLOG = "mobi.wifi.adlibrary.ACTION_OPEN_MYLOG";
    private static final String ACTION_CLOSE_MYLOG = "mobi.wifi.adlibrary.ACTION_CLOSE_MYLOG";

    public HybridDoorReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if(ACTION_OPEN_MYLOG.equals(action)){
            MyLog.DEBUG_MODE = true;
            Toast.makeText(context,"open ^_^",Toast.LENGTH_SHORT).show();
        }else if(ACTION_CLOSE_MYLOG.equals(action)){
            MyLog.DEBUG_MODE =false;
        }
    }
}
