package com.weidiao.print.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.util.BitmapUtil;
import com.lzy.imagepicker.view.CropImageView;
import com.weidiao.print.constant.Constants;
import com.weidiao.print.util.FileUtil;
import com.weidiao.print.util.GlideUtil;
import com.weidiao.print.util.ImageUtils;
import com.weidiao.print.util.LogUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 素材图片裁剪
 */
public class MeterialImageCropActivity extends BaseActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {

    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private String pathFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        //初始化View
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);

        if(getIntent().hasExtra("paintPathFile")){ //绘图
            String pathFile = getIntent().getStringExtra("paintPathFile");
            LogUtil.d("pathFile",pathFile);
            Bitmap bitmap = ImageUtils.getBitmapByPath(pathFile);
            mCropImageView.setImageBitmap(bitmap);
        }else{//素材
            String imagePath = getIntent().getStringExtra("path");
            //设置默认旋转角度
            int id = this.getResources().getIdentifier(imagePath, "mipmap", this.getPackageName());
            mCropImageView.setImageResource(id);
        }

        mCropImageView.setOnBitmapSaveCompleteListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_ok) {
//            Bitmap bitmap = mCropImageView.getCropBitmap(600,600,true);
            mCropImageView.saveBitmapToFile(new File(FileUtil.getPhotoPath(this)), Constants.IMAGE_OutPutX, Constants.IMAGE_OutPutY, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCropImageView.setOnBitmapSaveCompleteListener(null);
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        String path = file.getAbsolutePath();
        Intent intent = new Intent(MeterialImageCropActivity.this,ImageProcessingActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) {

    }
}
