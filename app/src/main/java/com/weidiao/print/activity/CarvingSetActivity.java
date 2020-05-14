package com.weidiao.print.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.lzy.imagepicker.ImagePicker;
import com.weidiao.print.R;
import com.weidiao.print.constant.Constants;
import com.weidiao.print.util.AppActivityManager;
import com.weidiao.print.util.BitmapUtil;
import com.weidiao.print.util.FileUtil;
import com.weidiao.print.util.GlideUtil;
import com.weidiao.print.util.HexUtil;
import com.weidiao.print.util.LogUtil;
import com.weidiao.print.util.ToastUtil;
import com.weidiao.print.view.XCDropDownListView;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 雕刻设置
 * Created by shcx on 2019/11/17.
 */

public class CarvingSetActivity extends BaseActivity {
    private TextView tv_title;
    private XCDropDownListView dropDownListView1,dropDownListView2;
    private ImageView imageView;
    private EditText edit_size;
    private String path;
    private Mat oriMat;
    private String fileName;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carving_set);
        initView();
        initData();
    }

    private void initView(){
        dropDownListView1 = (XCDropDownListView)findViewById(R.id.drop_down_list_view1);
        dropDownListView2 = findViewById(R.id.drop_down_list_view2);
        edit_size = findViewById(R.id.edit_size);
        imageView = findViewById(R.id.imageView);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("雕刻设置");
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpPreviewActivity();
            }
        });

    }

    private void initData(){
        path = getIntent().getStringExtra("path");
        GlideUtil.displayImage(this,path,imageView);
        String[] metes = getResources().getStringArray(R.array.carving_meta);
        ArrayList<String> list1 = new ArrayList<>(Arrays.asList(metes));
        dropDownListView1.setItemsData(list1);
        String[] depths= getResources().getStringArray(R.array.carving_depth);
        ArrayList<String> list2 = new ArrayList<>(Arrays.asList(depths));
        dropDownListView2.setItemsData(list2);

        File file = new File(path);
        fileName = file.getName();
        byte[] data = FileUtil.readFileToByteArray(path.replace("jpg","txt"));
        oriMat = new Mat(Constants.IMAGE_OutPutX,Constants.IMAGE_OutPutY, CvType.CV_8UC1);
        oriMat.put(0,0,data);

    }


    private void jumpPreviewActivity(){
        String size = edit_size.getText().toString();
        if(!TextUtils.isEmpty(size)){
            int num = Integer.parseInt(size);
            if(num<10 || num>100){
                ToastUtil.showToast(CarvingSetActivity.this,"面积大小10~100");
                return;
            }else{
                Constants.IMAGE_PARA_IMAGE_RESOLUTION = num;
                Constants.IMAGE_WIDTH = 10*num;
                Constants.IMAGE_HEIGHT = 10*num;
                saveImageData(fileName.replace(".jpg",""));
            }
        }

        String material = dropDownListView1.getEditTextData();
        Constants.IMAGE_PARA_IMAGE_DEPTH = Integer.parseInt(dropDownListView2.getEditTextData().replace("%",""));
        Constants.IMAGE_PARA_IMAGE_MATERIAL = material;
        LogUtil.d("param",Constants.IMAGE_PARA_IMAGE_RESOLUTION+"---"+material+"----"+Constants.IMAGE_PARA_IMAGE_DEPTH);

        Intent intent = new Intent(CarvingSetActivity.this,CarvingPreviewActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
        AppActivityManager.finishActivity(this);

    }

    /**
     * 保存处理后的数据，用于上传，大小为100*100
     * @param fileName
     */
    private void saveImageData(String fileName){
        Mat imgmat=oriMat.clone();
        Size size = new Size(Constants.IMAGE_WIDTH,Constants.IMAGE_HEIGHT);
        Imgproc.resize(oriMat,imgmat,size);

        int width = imgmat.cols();
        int height = imgmat.rows();
        int channels= imgmat.channels();
        LogUtil.d("mat",width+"--"+height+"--"+channels);
        byte [] data = new byte[width*height];
        imgmat.get(0, 0, data);
        FileUtil.saveToFileWithByte(this,data,fileName);
    }

}
