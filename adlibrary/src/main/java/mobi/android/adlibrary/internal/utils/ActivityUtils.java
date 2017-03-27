package mobi.android.adlibrary.internal.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyan on 2017/1/5.
 */
public class ActivityUtils {
    /**
     * 判断当前的进程是在前台还是后台
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前程序是否在栈顶
     * @param context
     * @return
     */
    public static boolean isTopActivy( Context context)
    {

        boolean isTop = false;

        if(context == null){
            return  isTop;
        }

        try {
            String packageName = DeviceUtil.getAppPackName(context.getApplicationContext());
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            isTop = currentPackageName != null && currentPackageName.equals(packageName);
        }catch (Exception e){
            e.printStackTrace();
        }

        return isTop;
    }

    /**
     * 获取正在运行的程序信息
     * @param context
     * @return
     */
    public static List<String> getRunningProcess(Context context){

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
        PackageManager pm =context.getPackageManager();
        List<String> list = new ArrayList<String>();
        try {
            for(RunningAppProcessInfo ra : run){
                if(ra.processName.equals("system") || ra.processName.equals("com.Android.phone")){ //可以根据需要屏蔽掉一些进程
                    continue;
                }
                list.add(ra.processName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }




}
