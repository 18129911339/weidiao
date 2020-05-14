package com.weidiao.print.constant;

/**
 * Created by shcx on 2019/12/14.
 */

public class Constants {

    public static String CODE_DEVICE_STATE = "6142FF010001000E00312A009CB1"; //设备状态

    public static String CODE_DEVICE_STOP = "6142FF010001000E0032F95F77C3"; //设备停止

    public static String CODE_DEVICE_PAUSE = "6142FF010001000F003301";//暂停  暂停雕刻

    public static String CODE_DEVICE_RESUME = "6142FF010001000F003302";//暂停 --恢复雕刻

    public static String CODE_DEVICE_START = "6142FF010001000F003501"; //开始雕刻

    public static String CODE_DEVICE_PREVIEW = "6142FF01000100120034";//开始预览

    public static String CODE_IMAGE_GET = "6142FF010001000F003601";  //获取图像参数

    public static String CODE_IMAGE_SET = "6142FF01000100230036"; //设置图像参数

    public static String CODE_IMAGE_UPLOAD = "6142FF"; //上传



    public static String stateCode = "31";  //上传指令37
    public static String preViewCode = "34";  //开始预览指令
    public static String setImageParameCode = "36";  //开始雕刻35
    public static String uploadCode = "37";  //上传指令37
    public static String startWorkCode = "35";  //开始雕刻35
    public static String pauseWorkCode = "33";  //暂停雕刻
    public static String stopWorkCode = "32";  //停止雕刻



    //图片裁剪大小
    public static int IMAGE_OutPutX = 500;
    public static int IMAGE_OutPutY = 500;
    public static int IMAGE_WIDTH = 250;  //需要比IMAGE_OutPutX小
    public static int IMAGE_HEIGHT = 250;

    //图像参数  16进制字符串
    public static int IMAGE_PARA_IMAGE_NO = 0x01;
    public static int IMAGE_PARA_LENGTH = 0x15;
    public static int IMAGE_PARA_FLIENAME = 0x01;
    public static int IMAGE_PARA_FLIETYPE = 0x01;  //0x01-BMP，0x02-GCode
    public static int IMAGE_PARA_IMAGETYPE = 0x01; //0x01-灰度图  0x02-二值图，
    public static int IMAGE_PARA_MATERIALTYPE = 0x01;  //材质类型
    public static int IMAGE_PARA_IMAGE_WIDTH = IMAGE_WIDTH;
    public static int IMAGE_PARA_IMAGE_HEIGTH = IMAGE_HEIGHT;
    public static int IMAGE_PARA_IMAGE_RESOLUTION = 10;  //雕刻分辨率 //（10~100 pix/mm） 尺寸大小
    public static  String IMAGE_PARA_IMAGE_MATERIAL = "";
    public static int IMAGE_PARA_IMAGE_DEPTH = 30;//（30%~100%）
    public static int IMAGE_PARA_IMAGE_CODE= 0x01;





}
