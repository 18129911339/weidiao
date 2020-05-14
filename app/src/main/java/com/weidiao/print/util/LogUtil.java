package com.weidiao.print.util;

import android.util.Log;

/**
 * Created by shcx on 2019/11/17.
 * Log 相关工具类
 */

public class LogUtil {
    public static boolean isLogEnabled = true;

    /**
     * 打印所有的信息
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (isLogEnabled) {
            Log.v(tag, msg);
        }
    }

    /**
     * 打印 info 信息
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (isLogEnabled) {
            Log.i(tag, msg);
        }
    }

    /**
     * 打印 debug 信息
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (isLogEnabled) {
            Log.d(tag, msg);
        }
    }

    /**
     * 打印警告信息
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (isLogEnabled) {
            Log.w(tag, msg);
        }
    }

    /**
     * 打印错误信息
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (isLogEnabled) {
            Log.e(tag, msg);
        }
    }

}
