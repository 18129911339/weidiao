package cn.fjnu.edu.paint.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 */

public class BitmapUtil {


    /**
     * 保存图片
     * @param bm
     * @throws IOException
     */
    public static String saveImage(Bitmap bm,String folderName) {
        File jpgFile ;
        File dirFile = new File(folderName);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
       String fileName = System.currentTimeMillis() + ".jpg";
        jpgFile = new File(folderName,fileName);
        try {
            FileOutputStream bos = new FileOutputStream(jpgFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpgFile.getPath();
    }



}
