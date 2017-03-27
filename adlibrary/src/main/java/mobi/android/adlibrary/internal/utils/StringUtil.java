//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package mobi.android.adlibrary.internal.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by vincent on 2016/3/29.
 */
public class StringUtil {
    public StringUtil() {
    }

    public static boolean isEmpty(String str) {
        if(str != null && str.length() != 0) {
            str = str.replaceAll(" ", "");
            if(str != null && str.length() != 0) {
                str = str.replaceAll("\n", "");
                str = str.replaceAll("\r", "");
                str = str.replaceAll("\t", "");
                return str == null || str.length() == 0;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static int valueOfInt(String string, int defaultValue) {
        int value = defaultValue;
        if(string != null && string.length() != 0) {
            try {
                value = Integer.parseInt(string);
            } catch (NumberFormatException var4) {
                var4.printStackTrace();
            }

            return value;
        } else {
            return defaultValue;
        }
    }

    public static long valueOfLong(String string, long defaultValue) {
        long value = defaultValue;
        if(string != null && string.length() != 0) {
            try {
                value = Long.parseLong(string);
            } catch (NumberFormatException var6) {
                var6.printStackTrace();
            }

            return value;
        } else {
            return defaultValue;
        }
    }

    public static float valueOfFloat(String string, float defaultValue) {
        float value = defaultValue;
        if(string != null && string.length() != 0) {
            try {
                value = Float.parseFloat(string);
            } catch (NumberFormatException var4) {
                var4.printStackTrace();
            }

            return value;
        } else {
            return defaultValue;
        }
    }

    public static boolean valueOfBoolean(String string, boolean defaultValue) {
        boolean value = defaultValue;
        if(string != null && string.length() != 0) {
            try {
                value = Boolean.parseBoolean(string);
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return value;
        } else {
            return defaultValue;
        }
    }

    public static boolean isNumeric(String str) {
        if(str != null && str.length() != 0) {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        } else {
            return false;
        }
    }

    public static boolean isEmail(String string) {
        if(string != null && string.length() != 0) {
            String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(string);
            return m.find();
        } else {
            return false;
        }
    }

    public static String GetURLEncoder(String src, String encode) {
        String retStr = "";

        try {
            if(encode.equals("")) {
                retStr = URLEncoder.encode(src, "utf-8");
            } else {
                retStr = URLEncoder.encode(src, encode);
            }
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return retStr;
    }

    public static String GetURLDecoder(String src, String encode) {
        String retStr = "";

        try {
            if(encode.equals("")) {
                retStr = URLDecoder.decode(src, "utf-8");
            } else {
                retStr = URLDecoder.decode(src, encode);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return retStr;
    }


    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
