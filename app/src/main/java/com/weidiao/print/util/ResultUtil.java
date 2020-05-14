package com.weidiao.print.util;

/**
 * Created by shcx on 2020/1/2.
 */

public class ResultUtil {

    public static boolean getResponseSuccess( byte[] data,String code){
        if(data.length>10 && HexUtil.byteToHex(data[9]).equals(code)){
            if(data[2]==0){
                return true;
            }
        }
        return false;
    }

    public static String isSuccess(byte[] data,String code){
        if(data.length>10 && HexUtil.byteToHex(data[9]).equals(code)){
            return HexUtil.byteToHex(data[2]);
        }
        return "4"; //其他错误
    }
}
