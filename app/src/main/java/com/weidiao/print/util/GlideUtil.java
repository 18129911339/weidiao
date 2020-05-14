package com.weidiao.print.util;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

/**
 */
public class GlideUtil  {

    public static void displayImage(Activity activity, String path, ImageView imageView) {
        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.ic_default_image)           //设置错误图片
                .placeholder(R.drawable.ic_default_image)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(imageView);
    }

    public static void displayGif(Activity activity, int id, ImageView imageView) {
        Glide.with(activity)                             //配置上下文
                .load(id)    //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .into(imageView);
    }

}
