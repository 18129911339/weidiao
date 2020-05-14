package com.weidiao.print.activity;

import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.ble.utils.TimeUtil;
import com.weidiao.print.R;
import com.weidiao.print.ble.LeProxy;
import com.weidiao.print.constant.Constants;
import com.weidiao.print.util.AppActivityManager;
import com.weidiao.print.util.FileUtil;
import com.weidiao.print.util.GlideUtil;
import com.weidiao.print.util.HexUtil;
import com.weidiao.print.util.LogUtil;
import com.weidiao.print.util.ResultUtil;
import com.weidiao.print.util.Stm32crc;
import com.weidiao.print.util.ToastUtil;

import java.io.File;

import static com.weidiao.print.activity.MainActivity.mSelectedAddress;

/**
 * 上传图片
 * Created by shcx on 2019/11/17.
 */

public class UploadActivity extends BaseActivity {
    private TextView tv_title;
    private ProgressBar progressBar;
    private LeProxy mLeProxy;
    private TextView tv_percent;
    private Chronometer tv_time;
    private TextView btn_cancel;

    private String path;
    private String uploadPath;
    private int blockLength = 224 * 1; //224字节
    private int chunck , chuncks;//流块
    private boolean isStopUpload = false;

    private ImageView imageView;

    private boolean isSendSuccess = false;
    private long sendTime;

    private boolean isSetImageParame = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initView();
        initData();

    }

    private void initView(){
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.upload));
        tv_percent = findViewById(R.id.tv_percent);
        tv_time = findViewById(R.id.tv_time);
        btn_cancel = findViewById(R.id.btn_cancel);
        imageView = findViewById(R.id.imageView);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStopUpload){
                    stopUpload();
                }
            }
        });

        final Handler mHandler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(UploadActivity.this.isDestroyed())return;
                //每隔1s循环执行run方法
                if(!isSetImageParame){
                    setImageParame();
                }else{
                    long time = System.currentTimeMillis();
                    if(time-sendTime>200 && isSendSuccess == false){
                        if(chunck>0)chunck = chunck-1;
                        upload();
                    }
                }
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(r, 300);//延时100毫秒
    }

    private void initData(){
        path = getIntent().getStringExtra("path");
        uploadPath = path.replace(".jpg",".txt");
        LogUtil.d("path",path);
        LogUtil.d("uploadPath",uploadPath);
        GlideUtil.displayImage(this,path,imageView);
        mLeProxy = LeProxy.getInstance();
        mLeProxy.setEncrypt(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, makeFilter());


        File file = new File(uploadPath);
        if(!file.exists()){
            return ;
        }
        if (file.length() % blockLength == 0) {
            chuncks = (int) file.length() / blockLength;
        } else {
            chuncks = (int) file.length() / blockLength + 1;
        }
        LogUtil.d("filelength",file.length()+"");
        progressBar.setMax(chuncks);
        progressBar.setProgress(chunck);
        tv_percent.setText("0%");
//        tv_time.setBase(SystemClock.elapsedRealtime());
//        tv_time.start();

        if(!mLeProxy.isConnected(mSelectedAddress)){
            ToastUtil.showToast(this,"蓝牙已断开，请重新连接");
            startActivity(new Intent(UploadActivity.this,BlueListActivity.class));
            return;
        }
        updateMtu();
    }

    private void updateMtu() {
        int mtu = 251 ;//最大251
        mLeProxy.requestMtu(mSelectedAddress, mtu);
    }

    private void stopUpload(){
        tv_time.stop();
        isStopUpload = true;
        AppActivityManager.finishActivity(this);
    }


    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_RSSI_AVAILABLE);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
        filter.addAction(LeProxy.ACTION_MTU_CHANGED);
        return filter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }

    private void upload() {
        try {
            isSendSuccess = false;
            sendTime = System.currentTimeMillis();
            String txData = Constants.CODE_IMAGE_UPLOAD;
            String tatal = HexUtil.intToHex(chuncks); //总块数
            if(tatal.length()==2)tatal = "00"+tatal;
            String curr = HexUtil.intToHex(chunck+1); //当前块
            if(curr.length()==2)curr = "00"+curr;
            txData = txData + HexUtil.HighLowHex(tatal.toUpperCase())+HexUtil.HighLowHex(curr.toUpperCase());

            byte[] uploadData = getUploadData();  //图像数据
            String len = HexUtil.intToHex(uploadData.length+14);
            if(len.length()==2)len = "00"+len;  //总字节数
            txData = txData +HexUtil.HighLowHex(len.toUpperCase()) + Constants.uploadCode; //37指令
            String d1 = HexUtil.byteArrayToHex(uploadData);
//            tmpData = tmpData+d1;
//            LogUtil.d("tmpData",tmpData+"");
            txData = txData+d1;
//            LogUtil.d("txData",txData+"");
            int crc = Stm32crc.getStm32crc(HexUtil.hexToShortArray(txData));
//            LogUtil.d("crc",HexUtil.intToHex(crc)+"");
            txData = txData+HexUtil.HighLowHex(HexUtil.intToHex(crc)).toUpperCase();
            byte[] data = DataUtil.hexToByteArray(txData);
//            LogUtil.d("data",txData+"");
//            LogUtil.d("mBlock",txData.length()+"*******");
            LogUtil.d("time2",System.currentTimeMillis()+"");
            mLeProxy.send(mSelectedAddress, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageParame(){
        String no = HexUtil.intToHex(Constants.IMAGE_PARA_IMAGE_NO);  //图像编号
        String count = HexUtil.intToHex(Constants.IMAGE_PARA_LENGTH);
        Constants.IMAGE_PARA_FLIENAME = Integer.parseInt(FileUtil.get10ByteFileName(path));
        String fileName = HexUtil.byteArrayToHex(FileUtil.get10ByteFileName(path).getBytes()); //文件名
        String fileType = HexUtil.intToHex(Constants.IMAGE_PARA_FLIETYPE);//文件类型
        String imageType = HexUtil.intToHex(Constants.IMAGE_PARA_IMAGETYPE);  //图像类型
        String materialType = HexUtil.intToHex(Constants.IMAGE_PARA_MATERIALTYPE);  //材质类型
        String width = HexUtil.HighLowHex(HexUtil.intToHex(Constants.IMAGE_WIDTH));
        String height = HexUtil.HighLowHex(HexUtil.intToHex(Constants.IMAGE_HEIGHT));
        if(width.length()==2)width = width+"00";
        if(height.length()==2)height = height+"00";
        String imageResolution = HexUtil.intToHex(Constants.IMAGE_PARA_IMAGE_RESOLUTION);
        String imageDepth = HexUtil.intToHex(Constants.IMAGE_PARA_IMAGE_DEPTH);
        int code = Constants.IMAGE_PARA_LENGTH +Constants.IMAGE_PARA_FLIENAME +Constants.IMAGE_PARA_FLIETYPE
                +Constants.IMAGE_PARA_IMAGETYPE +Constants.IMAGE_PARA_MATERIALTYPE+Constants.IMAGE_PARA_IMAGE_WIDTH
                +Constants.IMAGE_PARA_IMAGE_HEIGTH +Constants.IMAGE_PARA_IMAGE_RESOLUTION+Constants.IMAGE_PARA_IMAGE_DEPTH;
        LogUtil.d("imageCode" ,code+"");
        String tmpcode = HexUtil.intToHex(code);
        String imageCode = tmpcode.substring(tmpcode.length()-2);

        LogUtil.d("parame" ,count+" " +fileName+" "+fileType+" "+imageType+" "+materialType+" "
        +width+" "+height+" "+imageResolution+" "+imageDepth+" "+imageCode);
        String txData = Constants.CODE_IMAGE_SET+count+fileName+fileType+imageType+materialType
                +width+height+imageResolution+imageDepth+imageCode;
        int crc = Stm32crc.getStm32crc(HexUtil.hexToShortArray(txData));
        LogUtil.d("crc",HexUtil.intToHex(crc)+"");
        txData = txData+HexUtil.HighLowHex(HexUtil.intToHex(crc)).toUpperCase();
        byte[] data = DataUtil.hexToByteArray(txData);
        LogUtil.d("data",txData+"");
        mLeProxy.send(mSelectedAddress, data);

    }

    private byte[] getUploadData(){
        File file = new File(uploadPath);
        if(!file.exists()){
            return null;
        }

        chunck = chunck+1;
        LogUtil.d("chuncks",chuncks+"总块," +"当前："+chunck);
        final byte[] mBlock = FileUtil.getBlock((chunck - 1) * blockLength, file, blockLength);
//        LogUtil.d("mBlock",mBlock.length+"*******");
        return mBlock;

    }



    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);

            switch (intent.getAction()) {
                case LeProxy.ACTION_GATT_DISCONNECTED:// 断线
//                    mSelectedAddresses.remove(address);
//                    mDeviceListAdapter.removeDevice(address);
                    break;

                case LeProxy.ACTION_RSSI_AVAILABLE: {// 更新rssi
//                    LeDevice device = mDeviceListAdapter.getDevice(address);
//                    if (device != null) {
//                        int rssi = intent.getIntExtra(LeProxy.EXTRA_RSSI, 0);
//                        device.setRssi(rssi);
//                        mDeviceListAdapter.notifyDataSetChanged();
//                    }
                }
                break;

                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    if (address.equals(mSelectedAddress)) {
                        displayRxData(intent);
                    }
                    break;

                case LeProxy.ACTION_MTU_CHANGED:
                    int status = intent.getIntExtra(LeProxy.EXTRA_STATUS, BluetoothGatt.GATT_FAILURE);
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        int mMtu = intent.getIntExtra(LeProxy.EXTRA_MTU, 23);
//                        updateTxData();
//                        ToastUtil.show(getActivity(), "MTU has been " + mMtu);
                        LogUtil.d("mtu",mMtu+"");
                        setImageParame();
//                        upload();
                    } else {
//                        ToastUtil.show(getActivity(), "MTU update error: " + status);
                    }

                    break;

            }
        }
    };


    private void displayRxData(Intent intent) {
        String uuid = intent.getStringExtra(LeProxy.EXTRA_UUID);
        byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);

//        String dataStr = "timestamp: " + TimeUtil.timestamp("MM-dd HH:mm:ss.SSS") + '\n'
//                + "uuid: " + uuid + '\n'
//                + "length: " + (data == null ? 0 : data.length) + '\n';
//            dataStr += "data: " + DataUtil.byteArrayToHex(data);

        if(data.length>10 && HexUtil.byteToHex(data[9]).equals(Constants.uploadCode)){ //上传指令
            if(data[2]==0){ //成功
                if(chunck==1 ){
                    tv_time.setBase(SystemClock.elapsedRealtime());
                    tv_time.start();
                }
                isSendSuccess = true;
                progressBar.setProgress(chunck);
                tv_percent.setText((chunck*100)/chuncks+"%");
                if(progressBar.getProgress()==chuncks){
                    jumpWorkingActivity();
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            upload();
                        }
                    },1);
                }
            }else if(data[2]==1){ //校验错误
//                ToastUtil.showToast(UploadActivity.this,"校验错误，请取消重新上传");
            }else if(data[2]==2){//接收超时
//                ToastUtil.showToast(UploadActivity.this,"接收超时");
            }else if(data[2]==3){//拒绝
//                ToastUtil.showToast(UploadActivity.this,"拒绝");
            }else if(data[2]==4){//其它
//                ToastUtil.showToast(UploadActivity.this,"错误");
            }
        }
        if(ResultUtil.getResponseSuccess(data,Constants.setImageParameCode)){
//            Log.d("dataStr",dataStr);
            LogUtil.d("setParame","设置图像参数成功");
            isSetImageParame = true;
            if(chunck==0){
                upload();
            }
        }
    }


    private void jumpWorkingActivity(){
        tv_time.stop();
        Intent intent = new Intent(UploadActivity.this,WorkingActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
        AppActivityManager.finishActivity(this);

    }


}
