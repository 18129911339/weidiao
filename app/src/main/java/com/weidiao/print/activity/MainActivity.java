package com.weidiao.print.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.weidiao.print.R;
import com.weidiao.print.constant.Constants;
import com.weidiao.print.util.AppActivityManager;
import com.weidiao.print.util.FileUtil;
import com.weidiao.print.util.ToastUtil;


import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;

import cn.fjnu.edu.ui.activity.PaintMainActivity;
import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity {

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private int REQUEST_CODE_MATERIAL = 102;
    private int REQUEST_CODE_PAINT = 103;
    private int maxImgCount = 1;               //允许选择图片最大数
    private int PERMISSION_REQUEST_COARSE_LOCATION=999;

    public static String mSelectedAddress;

    private static boolean isExit=false;

    public static boolean isFirstFlag = false;

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFirstFlag = true;
        initImagePicker();
        initView();
        //6.0以上的手机要地理位置权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
        EventBus.getDefault().register(this);
    }

    private void initView(){
        findViewById(R.id.ll_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    camera();
                }
            }
        });
        findViewById(R.id.ll_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        findViewById(R.id.ll_material).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpMaterialActivity();
            }
        });
        findViewById(R.id.ll_drawing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpPaintActivity();
            }
        });

        findViewById(R.id.img_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpSettingtActivity();
            }
        });
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setMultiMode(false);                     //单选
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(Constants.IMAGE_OutPutX);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(Constants.IMAGE_OutPutY);                         //保存文件的高度。单位像素
    }

    private void camera(){
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    private void selectImage(){
        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(maxImgCount);
        Intent intent1 = new Intent(MainActivity.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
        startActivityForResult(intent1, REQUEST_CODE_SELECT);
    }

    private void jumpMaterialActivity(){
        Intent intent = new Intent(MainActivity.this,MaterialListActivity.class);
        startActivityForResult(intent,REQUEST_CODE_MATERIAL);
    }

    private void jumpPaintActivity(){
        Intent intent = new Intent(MainActivity.this, PaintMainActivity.class);
        startActivityForResult(intent,REQUEST_CODE_PAINT);
    }

    private void jumpSettingtActivity(){
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if(images.size()>0){
                    byte[] fileData = com.weidiao.print.util.BitmapUtil.compressImage(images.get(0).path,1080,1920,100);
                    String fileName = "temp.jpg";
                    String path = FileUtil.saveImageWithByte(this,fileData,fileName);
                    Intent intent = new Intent(MainActivity.this,ImageProcessingActivity.class);
                    intent.putExtra("path",path);
                    startActivity(intent);
                }
            }
        }else if(data != null && requestCode == REQUEST_CODE_MATERIAL){
//            String name = data.getStringExtra("name");
//            int id = mContext.getResources().getIdentifier(name, "mipmap", mContext.getPackageName());
//            Bitmap bitmap= BitmapFactory.decodeResource(mContext.getResources(), id);
//            imageView.setImageBitmap(bitmap);
//            imageView.setImageBitmap(ImageUtils.toGrayscale(bitmap));
        }else if(data != null && requestCode == REQUEST_CODE_PAINT){
            String pathFile = data.getStringExtra("pathFile");
            Intent intent = new Intent(MainActivity.this, MeterialImageCropActivity.class);
            intent.putExtra("paintPathFile",pathFile);
            startActivity(intent);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == ImageGridActivity.REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera();
            } else {
                ToastUtil.showToast(this,getResources().getString(R.string.no_camera_permission));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void exit(){
        if(!isExit){
            isExit=true;
            ToastUtil.showToast(this,getResources().getString(R.string.exit_app));
            handler.sendEmptyMessageDelayed(0,2000);
        } else{
            AppActivityManager.getAppManager().finishAllActivity();
//            System.exit(0);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            isExit=false;
        }
    };



}
