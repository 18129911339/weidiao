package com.weidiao.print.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shcx on 2019/12/14.
 */

public class FileUtil {

    /**
     * 获取10个字节文件名
     * @param path
     * @return
     */
    public static String get10ByteFileName(String path){
        File file = new File(path);
        String fileName = file.getName();
        String tmpName = fileName.substring(0,fileName.indexOf("."));
        if(tmpName.length()>10){
            tmpName =  tmpName.substring(tmpName.length()-10);
        }
        return tmpName;
    }


    public static byte[] getBlock(long offset, File file, int blockSize) {
        byte[] result = new byte[blockSize];
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "r");
            accessFile.seek(offset);
            int readSize = accessFile.read(result);
            if (readSize == -1) {
                return null;
            } else if (readSize == blockSize) {
                return result;
            } else {
                byte[] tmpByte = new byte[readSize];
                System.arraycopy(result, 0, tmpByte, 0, readSize);
                return tmpByte;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }


    //合并两个byte数组
    public static byte[] addBytes(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }


    public static String getPhotoPath(Context context){
        if(getSDPath() == null){
            Toast.makeText(context,"SD卡不能为空", Toast.LENGTH_SHORT).show();
            return "";
        }
        return getSDPath() + "/laserCube/image/";
    }

    /**
     * 获取sd卡的路径
     *
     * @return 路径的字符串
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardUsable = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

        if (sdCardUsable) {
            sdDir = Environment.getExternalStorageDirectory();//获取外存目录
            return sdDir.toString();
        }
        return null;
    }

    public static String saveToFileWithByte(Context context, byte[] bytes,String fileName) {
        File jpgFile = null;
        try {
            String dirPath = FileUtil.getPhotoPath(context);
            File fileFolder = new File(dirPath);
            if(!fileFolder.exists())fileFolder.mkdirs();
            fileName = fileName + ".txt";
            jpgFile = new File(fileFolder, fileName);
            FileOutputStream fos = new FileOutputStream(jpgFile);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jpgFile.getPath();
    }

    public static String getNewFileName(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String fileName = dateFormat.format(new Date(System.currentTimeMillis()));
        return fileName;
    }


    public static byte[] readFileToByteArray(String path) {
        File file = new File(path);
        if(!file.exists()) {
            Log.e("","File doesn't exist!");
            return null;
        }
        try {
            FileInputStream in = new FileInputStream(file);
            long inSize = in.getChannel().size();//判断FileInputStream中是否有内容
            if (inSize == 0) {
                Log.d("","The FileInputStream has no content!");
                return null;
            }

            byte[] buffer = new byte[in.available()];//in.available() 表示要读取的文件中的数据长度
            in.read(buffer);  //将文件中的数据读到buffer中
            return buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
//            try {
//                in.close();
//            } catch (IOException e) {
//                return null;
//            }
            //或IoUtils.closeQuietly(in);
        }
    }


    public static String saveImageWithByte(Context context, byte[] bytes,String fileName) {
        File jpgFile = null;
        try {
            String dirPath = FileUtil.getPhotoPath(context);
            File fileFolder = new File(dirPath);
            if(!fileFolder.exists())fileFolder.mkdirs();
//            fileName = fileName + ".jpg";
            jpgFile = new File(fileFolder, fileName);
            FileOutputStream fos = new FileOutputStream(jpgFile);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jpgFile.getPath();
    }

}
