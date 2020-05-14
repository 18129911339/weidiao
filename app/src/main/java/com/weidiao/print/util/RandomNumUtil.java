package com.weidiao.print.util;

/**
 * Created by shcx on 2019/12/23.
 */

public class RandomNumUtil {

    /**
     * 获取一个几位随机数（num=4则为4位数的随机数）
     */
    public static String getRandomNum(int num) {
        String dataCode = "";
        for (int i = 0; i < num; i++) {
            dataCode += String.valueOf(Math.round(Math.random() * 9));
        }
        return dataCode;
    }

}
