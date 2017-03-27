package mobi.android.adlibrary.internal.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by vincent on 2016/3/29.
 */
public class StorageUtils {
    public static boolean serializeToFile(Object obj, String fileName) {
        boolean flag = false;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        if(obj != null && !StringUtil.isEmpty(fileName)) {
            try {
                File e = new File(fileName);
                if(e.exists()) {
                    e.delete();
                } else {
                    e = FileUtil.createFile(fileName);
                }

                fos = new FileOutputStream(fileName);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);
                oos.flush();
                flag = true;
            } catch (FileNotFoundException var16) {
                var16.printStackTrace();
            } catch (IOException var17) {
                var17.printStackTrace();
            } finally {
                try {
                    if(fos != null) {
                        fos.close();
                    }

                    if(oos != null) {
                        oos.close();
                    }
                } catch (IOException var15) {
                    var15.printStackTrace();
                }

            }

            return flag;
        } else {
            return false;
        }
    }

    public static Object FileToObject(String fileName) {
        if(fileName != null && !fileName.equals("")) {
            File file = new File(fileName);
            if(!file.exists()) {
                file = FileUtil.createFile(fileName);
            }

            return FileToObject(file);
        } else {
            return null;
        }
    }

    public static Object FileToObject(File file) {
        Object o = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        if(file != null && file.exists()) {
            try {
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                o = ois.readObject();
            } catch (FileNotFoundException var19) {
                var19.printStackTrace();
            } catch (StreamCorruptedException var20) {
                var20.printStackTrace();
            } catch (IOException var21) {
                var21.printStackTrace();
            } catch (ClassNotFoundException var22) {
                var22.printStackTrace();
            } finally {
                try {
                    if(fis != null) {
                        fis.close();
                    }

                    if(ois != null) {
                        ois.close();
                    }
                } catch (IOException var18) {
                    var18.printStackTrace();
                }

            }
        }

        return o;
    }


    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
