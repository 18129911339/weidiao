package com.weidiao.print.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by shcx on 2019/11/17.
 */

public class ScreenUtils {

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * mm毫米转分辨率
     * @param value 10mm
     * @param metrics  getResources().getDisplayMetrics()
     * @return
     */
    public  float applyDimension( float value, DisplayMetrics metrics) {
        return value * metrics.xdpi * (1.0f / 25.4f);
    }

}
