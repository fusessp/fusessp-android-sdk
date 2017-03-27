
package mobi.android.adlibrary.internal.utils;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import com.stat.analytics.AnalyticsSdk;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import mobi.android.adlibrary.BuildConfig;
import mobi.android.adlibrary.internal.ad.config.AdConfigLoader;
import mobi.android.adlibrary.internal.app.AdConstants;

/**
 * Created by vincent on 2016/3/29.
 */
public class FileUtil {
    public FileUtil() {
    }


    public  static String loadContent(InputStream inputStream){
        char[] buffer=new char[1024];
        StringBuilder sb=new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            int num;
            while (true) {
                num = reader.read(buffer);
                if (num == -1) {
                    break;
                }
                sb.append(buffer,0,num);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static File createFile(String fileName) {
        if(fileName != null && fileName.length() > 0) {
            File file = new File(fileName);
            File folderFile = file.getParentFile();
            if(!folderFile.exists()) {
                folderFile.mkdirs();
            }

            try {
                if(!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return file;
        } else {
            return null;
        }
    }

    public static long getFileSize(String fileName) {
        if(fileName != null && fileName.length() != 0) {
            File file = new File(fileName);
            return getFileSize(file);
        } else {
            return 0L;
        }
    }

    public static long getFileSize(File file) {
        long fileSize = 0L;
        if(file != null && file.exists()) {
            fileSize = file.length();
            if(fileSize > 0L) {
                return fileSize;
            } else {
                FileInputStream input = null;

                try {
                    input = new FileInputStream(file);
                    byte[] e = new byte[8192];

                    int temp1;
                    for(boolean temp = false; (temp1 = input.read(e)) != -1; fileSize += (long)temp1) {
                    }
                } catch (FileNotFoundException var16) {
                    var16.printStackTrace();
                } catch (IOException var17) {
                    var17.printStackTrace();
                } finally {
                    if(input != null) {
                        try {
                            input.close();
                        } catch (IOException var15) {
                            var15.printStackTrace();
                        }
                    }

                }

                return fileSize;
            }
        } else {
            return 0L;
        }
    }

    public static boolean isFileExist(String fileName) {
        boolean flag = false;
        if(fileName != null && fileName.length() != 0) {
            File file = new File(fileName);
            flag = file.exists();

            return flag;
        } else {
            return false;
        }
    }
    public static String readSdCardFile(boolean isReadFirstLine,String path) {
        MyLog.d(MyLog.TAG,"read sdcard file path :"+path);
        StringBuilder builder = new StringBuilder();
        File file = new File(path);
        if (file.exists()) {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line = "";
                    while ((line = buffreader.readLine()) != null) {
                        builder.append(line);
                        if (isReadFirstLine){
                            return builder.toString();
                        }
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
        return builder.toString();
    }

    public static ArrayList<String> getDevMountList(){
        ArrayList<String> out=new ArrayList<>();
        String systemRootPath= Environment.getRootDirectory().getAbsoluteFile() + File.separator + "etc" + File.separator + "vold.fstab";
        String[] toSearch = readSdCardFile(false, systemRootPath).split(" ");
        for (int i=0;i<toSearch.length;i++){
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    public static String getSdCardTestUrl(){
        String lastPath="/AvazuTest/loadConfig_test.txt";
        try{
            if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                return "";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+lastPath;
        if (!new File(filePath).exists()){
            return "";
        }
        String testUrl = readSdCardFile(true, filePath);
        if (!TextUtils.isEmpty(testUrl)) {
            return testUrl;
        }
//        ArrayList<String> devMountList =getDevMountList();
//        if (devMountList!=null){
//            for (String s:devMountList){
//                String url = readSdCardFile(true, s + nextPath);
//                Log.e("",s+nextPath);
//                if (TextUtils.isEmpty(url)){
//                    return url;
//                }
//            }
//        }
        return "";
    }


    public static boolean deleteFile(String filePath) {
        boolean success = false;
        if(isFileExist(filePath)) {
            File file = new File(filePath);
            success = file.delete();
        }

        return success;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            File e = new File(folderPath);
            e.delete();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if(file.exists()) {
            if(file.isDirectory()) {
                String[] tempList = file.list();
                File temp = null;

                for(int i = 0; i < tempList.length; ++i) {
                    if(path.endsWith(File.separator)) {
                        temp = new File(path + tempList[i]);
                    } else {
                        temp = new File(path + File.separator + tempList[i]);
                    }

                    if(temp.isFile()) {
                        temp.delete();
                    }

                    if(temp.isDirectory()) {
                        delAllFile(temp.getPath());
                        temp.delete();
                    }
                }

            }
        }
    }

    public static void copyFile(File src, File tar) throws Exception {
        if(src.isFile()) {
            FileInputStream is = new FileInputStream(src);
            FileOutputStream op = new FileOutputStream(tar);
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(op);
            byte[] bt = new byte[4096];

            for(int len = bis.read(bt); len != -1; len = bis.read(bt)) {
                bos.write(bt, 0, len);
            }

            bis.close();
            bos.close();
            src.delete();
        }

    }

    public static void cutFile(String fileName, long retainSize) {
        if(fileName != null && fileName.length() != 0 && retainSize > 0L) {
            File file = new File(fileName);
            long fileSize = 0L;
            fileSize = getFileSize(file);
            if(fileSize > 0L && retainSize < fileSize) {
                RandomAccessFile rf = null;
                FileChannel fc = null;

                try {
                    rf = new RandomAccessFile(file, "rw");
                    fc = rf.getChannel();
                    fc.truncate(retainSize);
                } catch (FileNotFoundException var19) {
                    var19.printStackTrace();
                } catch (IOException var20) {
                    var20.printStackTrace();
                } finally {
                    if(fc != null) {
                        try {
                            fc.close();
                        } catch (IOException var18) {
                            var18.printStackTrace();
                        }
                    }

                }

            }
        }
    }


    /**
     *
     * @param context
     * @param configurl
     * @param adConfigInfo
     * @param channel   当前的渠道是什么
     * @param installChannel  第一次的安装渠道是什么
     * @return
     */
    public static String getAllConfigPath(Context context, String configurl, String adConfigInfo,String channel,String installChannel,boolean isBlacklist) {
        configurl = configurl + "&pkg_ver=" + DeviceUtil.getAppVersion(context) +
                "&deviceid=" + DeviceUtil.getDeviceId(context) +
                "&pkg_name=" + DeviceUtil.getAppPackName(context.getApplicationContext()) +
//              "&imei=" + ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
                "&android_id=" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) +
                "&osv=" + android.os.Build.VERSION.SDK_INT +
                //用户是否为新用户
                "&new_user=" + DeviceUtil.getIsNewUser(context.getApplicationContext()) +
                //广告配置文件的file_version
                getFileVersion(adConfigInfo) +
                //第一次安装应用的时间
                "&first_time=" + DeviceUtil.getFristInstallTime(context.getApplicationContext()) / 1000 +
                //bid
                "&bid=" + AnalyticsSdk.getBucketId(context.getApplicationContext()) +
                "&sdk_vercode=" + BuildConfig.VERSION_CODE +
                "&sdk_vername=" + BuildConfig.VERSION_NAME;

        if (!StringUtil.isEmpty(channel)) {
            configurl += "&channel=" + channel;
        }
        if (!StringUtil.isEmpty(installChannel)) {
            configurl += "&installchannel=" + installChannel;
        }
        if (AdConfigLoader.getInstance(context) != null) {
            configurl += "&segment_id=" + AdConfigLoader.getInstance(context).getSegmentFromConfig();
        }
        if(isBlacklist){
            configurl += "&func=blacklist";
            //黑名单配置文件的file_version
            configurl += "&file_ver=" + SharePUtil.getString(context, AdConstants.PREF_BLACKLIST_FILE_VERSION,"0");
        }else{
            //广告配置文件的file_version
            configurl += getFileVersion(adConfigInfo);
        }
        return configurl;
    }

    public static String getFileVersion(String adConfig){
        String fileVersion = "0";
        if (!TextUtils.isEmpty(adConfig)) {
            try {
                JSONObject json = new JSONObject(adConfig);
                String version = json.getString("version");
                if (!TextUtils.isEmpty(version)) {
                    fileVersion = version;
                }
            } catch (Exception ignore) {
            }
        }
        return "&file_ver=" + fileVersion;
    }

    /**
     * 通过反射的机制获取到跳转平台的uri数据
     * @param nativeAdData
     * @return
     */
    public static String getStoreAdUri(Object nativeAdData){
        if(nativeAdData == null){
            return "";
        }
        String uri = "";
        Object m_Object = null;
        Field m_field = null;

        try {
            m_field = nativeAdData.getClass().getDeclaredField("m");
            m_field.setAccessible(true);
            m_Object = m_field.get(nativeAdData);

            Class m_clazz = m_Object.getClass();//Class.forName("com.facebook.ads.internal.adapters.r");
            Field uriField = m_clazz.getDeclaredField("d");
            uriField.setAccessible(true);
            Object uriObj = uriField.get(m_Object); // android.net.Uri
            MyLog.i("NativeADFields", "uri =  " + uriObj);
            if(uriObj != null){
                uri = uriObj.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return uri;
    }

    /**
     * 通过广告的uri获取对应的packageName
     * @param uri
     * @return
     */
    public static String getPackageNameFromUri(String uri){
        if(StringUtil.isEmpty(uri)){
            MyLog.d(MyLog.TAG,"不是平台广告，没有对应的厂商地址");
            return null;
        }
        if(StringUtil.isEmpty(uri)){
            return "" ;
        }
        String[] line = uri.split("=");

        if(line==null){
            return "";
        }
        if(line.length<1){
            return "";
        }
        String[] packageName = line[1].split("&");
        if(packageName == null){
            return "";
        }
        if(packageName.length<1){
            return "";
        }
       return packageName[0];
    }

    /**
     * 通过广告的数据获取对应的包名（仅限于FB）
     * @param nativeAdData
     * @return
     */
    public static String getPackageName(Object nativeAdData){
        String uri = getStoreAdUri(nativeAdData);
        if(!StringUtil.isEmpty(uri)){
            uri = getPackageNameFromUri(uri);
        }
        return uri;
    }

    public static String getTodayData(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
}
