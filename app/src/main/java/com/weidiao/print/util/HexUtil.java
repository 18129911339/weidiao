package com.weidiao.print.util;

import com.ble.api.DataUtil;

import java.io.File;

/**
 * Created by shcx on 2019/12/14.
 */

public class HexUtil {

    /**
     * @return
     */
    public static String intToHex(int val){
        String s = Integer.toHexString(val);
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        return s;
    }

    public static int HexToInt(String val){
        int s = Integer.parseInt(val, 16);
        return s;
    }


    public static String byteToHex(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 16进制高位转低位  0258---5802
     * @param var0
     * @return
     */
    public static String HighLowHex(String var0){
        String data ="";
        if(var0.length() % 2 == 1) {
            var0 = "0" + var0;
        }
        int length = var0.length()/2;
        for(int i=length;i>=1;i--){
            data = data + var0.substring((i-1)*2,(i-1)*2+2);
        }
        return data;
    }

    public static short[] hexToShortArray(String var0) {
        if(var0 != null && var0.length() != 0) {
            if(var0.length() % 2 == 1) {
                var0 = "0" + var0;
            }

            String[] var1;
            short[] var2 = new short[(var1 = new String[var0.length() / 2]).length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
                var1[var3] = var0.substring(2 * var3, 2 * (var3 + 1));
                var2[var3] = Short.parseShort(var1[var3], 16);
            }

            return var2;
        } else {
            return null;
        }
    }


    public static byte[] hexToByteArray(String var0) {
        if(var0 != null && var0.length() != 0) {
            if(var0.length() % 2 == 1) {
                var0 = "0" + var0;
            }

            String[] var1;
            byte[] var2 = new byte[(var1 = new String[var0.length() / 2]).length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
                var1[var3] = var0.substring(2 * var3, 2 * (var3 + 1));
                var2[var3] = (byte)Integer.parseInt(var1[var3], 16);
            }

            return var2;
        } else {
            return null;
        }
    }

    /**
     * 字符串转16进制
     * @param var0
     * @return
     */
    public static String byteArrayToHex(byte[] var0) {
        if(var0 != null && var0.length != 0) {
            StringBuilder var1 = new StringBuilder(var0.length);

            for(int var2 = 0; var2 < var0.length; ++var2) {
                var1.append(String.format("%02X", new Object[]{Byte.valueOf(var0[var2])}));
//                if(var2 < var0.length - 1) {
//                    var1.append(' ');
//                }
            }

            return var1.toString();
        } else {
            return "";
        }
    }


}
