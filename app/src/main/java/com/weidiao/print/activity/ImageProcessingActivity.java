package com.weidiao.print.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weidiao.print.R;
import com.weidiao.print.constant.Constants;
import com.weidiao.print.util.AppActivityManager;
import com.weidiao.print.util.FileUtil;
import com.weidiao.print.util.LogUtil;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import openvc.util.BitmapUtil;
import openvc.util.ContourUtil;
import openvc.util.ProcessUtil;



/**
 * 图片处理
 * Created by shcx on 2019/11/17.
 */
public class ImageProcessingActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "ImageProcessingActivity";
    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv");
    }

    private static final int REQUEST_WIDTH = Constants.IMAGE_OutPutX;// 该值能压缩到多低取决于边框线有多粗
    private static final int REQUEST_HEIGHT = REQUEST_WIDTH;
    private TextView tv_title;
    private TextView tv_gray,tv_threshold,tv_contours;
    private ImageView imageView;
    private Bitmap mBitmap,tmpBitmap;

    private Mat lastMat;

    private String path;

    private Mat oriMat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        initView();
        initData();
    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.image_process));
        tv_gray = findViewById(R.id.tv_gray);
        tv_gray.setOnClickListener(this);
        tv_gray.setSelected(true);
        imageView = findViewById(R.id.imageView);
        tv_threshold =findViewById(R.id.tv_threshold);
        tv_threshold.setOnClickListener(this);
        tv_contours = findViewById(R.id.tv_contours);
        tv_contours.setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        path = getIntent().getStringExtra("path");
        LogUtil.d("filePath",path);

        oriMat = Imgcodecs.imread(path);
        tmpBitmap = Bitmap.createBitmap(REQUEST_WIDTH,REQUEST_HEIGHT,Bitmap.Config.ARGB_8888);
        gray(oriMat);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_gray:
                setSelectBtn(tv_gray);
                gray(oriMat);
                break;
            case R.id.tv_threshold:
                setSelectBtn(tv_threshold);
                threshold(oriMat);
                break;
            case R.id.tv_contours:
                setSelectBtn(tv_contours);
                contours(oriMat);
                break;
            case R.id.tv_cancel:
                AppActivityManager.finishActivity(this);
                break;
            case R.id.tv_ok:
                String filePath = saveFile();
                LogUtil.d("filePath",filePath);
                //删除图片
                File file = new File(path);
                if(file.exists())file.delete();

                Intent intent = new Intent(ImageProcessingActivity.this,CarvingSetActivity.class);
                intent.putExtra("path",filePath);
                startActivity(intent);
                AppActivityManager.finishActivity(this);
                break;
        }
    }

    private String  saveFile(){

        String fileName = FileUtil.getNewFileName();

        saveSimleChannelImageData(fileName);

        //保存处理后的图片
        String dirPath = FileUtil.getPhotoPath(this);
        File fileFolder = new File(dirPath);
        if(!fileFolder.exists())fileFolder.mkdirs();
        String filename = fileName+".jpg";
        File  jpgFile = new File(fileFolder, filename);
        Imgcodecs.imwrite(jpgFile.getAbsolutePath(),lastMat);

        return jpgFile.getAbsolutePath();
    }

    /**
     * 保存处理后的数据，用于上传
     * @param fileName
     */
    private void saveSimleChannelImageData(String fileName){
        int width = lastMat.cols();
        int height = lastMat.rows();
        int channels= lastMat.channels();
        LogUtil.d("mat",width+"--"+height+"--"+channels);
        byte [] data = new byte[width*height];
        lastMat.get(0, 0, data);
        FileUtil.saveToFileWithByte(this,data,fileName);
    }


    private void setSelectBtn(TextView tv){
        tv_gray.setSelected(false);
        tv_threshold.setSelected(false);
        tv_contours.setSelected(false);
        tv.setSelected(true);
    }

    private void gray(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e("dd", "mat == null || mat.empty()");
            return;
        }

        Mat grayMat = ProcessUtil.gray(mat);
        BitmapUtil.matToBitmap(grayMat, tmpBitmap);
        lastMat = grayMat;
        Constants.IMAGE_PARA_IMAGETYPE = 0x01;

        imageView.setImageBitmap(tmpBitmap);
    }


    private void threshold(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat thresholdMat = ProcessUtil.threshold(mat);
        lastMat = thresholdMat;
        Constants.IMAGE_PARA_IMAGETYPE = 0x02;
        BitmapUtil.matToBitmap(thresholdMat, tmpBitmap);
        imageView.setImageBitmap(tmpBitmap);
    }


    private void contours(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat mat1 = contours2(mat);
        lastMat = mat1;
        Constants.IMAGE_PARA_IMAGETYPE = 0x02;
        BitmapUtil.matToBitmap(mat1, tmpBitmap);
        imageView.setImageBitmap(tmpBitmap);
    }


    private Mat contours2(Mat mat){
        Mat contoursMat = ContourUtil.contours(mat);
        Mat grayMat = ProcessUtil.gray(contoursMat);
        Mat mat1 = new Mat();
        Core.bitwise_not(grayMat,mat1);
        return mat1;
    }


}
