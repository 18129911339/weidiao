package com.weidiao.print.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.ble.utils.TimeUtil;
import com.weidiao.print.R;
import com.weidiao.print.ble.LeProxy;
import com.weidiao.print.constant.Constants;
import com.weidiao.print.util.AppActivityManager;
import com.weidiao.print.util.GlideUtil;
import com.weidiao.print.util.HexUtil;
import com.weidiao.print.util.LogUtil;
import com.weidiao.print.util.RandomNumUtil;
import com.weidiao.print.util.ResultUtil;
import com.weidiao.print.util.Stm32crc;
import com.weidiao.print.util.ToastUtil;
import com.weidiao.print.view.VerifyDialog;

import static com.weidiao.print.activity.MainActivity.mSelectedAddress;

/**
 * 雕刻预览
 * Created by shcx on 2019/11/17.
 */

public class CarvingPreviewActivity extends BaseActivity {
    private TextView tv_title,btn_preview,tv_tip;
    private LeProxy mLeProxy;
    private String path;
    private ImageView imageView;
    private String preCode ,preEtCode;
    private boolean isPreview = false;
    private boolean isPreviewing = false; //是否是预览中

    private boolean isSendSuccess = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carving_preview);
        initView();
        initData();
        getWorkState();

    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.carving_preview));
        btn_preview = findViewById(R.id.btn_preview);
        tv_tip = findViewById(R.id.tv_tip);
        imageView = findViewById(R.id.imageView);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPreview){
                    next();
                }else{
                    ToastUtil.showToast(CarvingPreviewActivity.this,"请先预览");
                }

            }
        });
        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPreview = true;

                if(isPreviewing){
                    isPreviewing = false;
                    btn_preview.setText("开始预览");
                    tv_tip.setText("点击[开始预览]打开激光，预览雕刻范围");
                    tv_tip.setTextColor(getResources().getColor(R.color.color_text));
                }else{
                    isPreviewing = true;
                    btn_preview.setText("暂停预览");
                    tv_tip.setText("请调整激光头至合适的位置");
                    tv_tip.setTextColor(getResources().getColor(R.color.color_bg));
                    send();
                }
            }
        });

        final Handler mHandler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(CarvingPreviewActivity.this.isDestroyed())return;
                //每隔1s循环执行run方法

                if(!isSendSuccess&& isPreviewing){
                    send();
                }
                mHandler.postDelayed(this, 500);
            }
        };
        mHandler.postDelayed(r, 500);//延时100毫秒
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                if(TextUtils.isEmpty(preEtCode)){
                    ToastUtil.showToast(CarvingPreviewActivity.this,"请输入安全码");
                }else if(preCode.equals(preEtCode)){
                    jumpUploadActivity();
                }else {
                    ToastUtil.showToast(CarvingPreviewActivity.this,"安全码不正确");
                }
            }
        }
    };

    private void initData(){
        path = getIntent().getStringExtra("path");
        GlideUtil.displayImage(this,path,imageView);
        mLeProxy = LeProxy.getInstance();
        mLeProxy.setEncrypt(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, makeFilter());
    }

    private void getWorkState() {
        try {
            String txData = Constants.CODE_DEVICE_STATE;
            LogUtil.d("getWorkState",txData.length()+"#####"+txData);
            if (txData.length() > 0) {
                byte[] data = DataUtil.hexToByteArray(txData);
                mLeProxy.send(mSelectedAddress, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMtu() {
        int mtu = 251 ;//最大251
        mLeProxy.requestMtu(mSelectedAddress, mtu);
    }


    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_RSSI_AVAILABLE);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
        return filter;
    }

    private void next(){
        final VerifyDialog dialog = VerifyDialog.create(this);
        String num = RandomNumUtil.getRandomNum(4);
        dialog.setVerifyCode(num);
        dialog.setConfirmListener(new VerifyDialog.ConfirmClickListener() {
            @Override
            public void confirmClick(String code, String etCode) {
               preCode = code;
               preEtCode = etCode;
               handler.sendEmptyMessage(0);
               if(code.equals(etCode)){
                    dialog.dismiss();
               }
            }
        });
        dialog.show();
    }


    private void send() {
        try {
            isSendSuccess = false;
            if(!mLeProxy.isConnected(mSelectedAddress)){
                ToastUtil.showToast(this,"蓝牙已断开，请重新连接");
                startActivity(new Intent(CarvingPreviewActivity.this,BlueListActivity.class));
                return;
            }
            String txData = Constants.CODE_DEVICE_PREVIEW;
            String width = HexUtil.HighLowHex(HexUtil.intToHex(Constants.IMAGE_PARA_IMAGE_RESOLUTION));
            String height = HexUtil.HighLowHex(HexUtil.intToHex(Constants.IMAGE_PARA_IMAGE_RESOLUTION));
            if(width.length()==2)width = width+"00";
            if(height.length()==2)height = height+"00";
            txData = txData+width+height;
            int crc = Stm32crc.getStm32crc(HexUtil.hexToShortArray(txData));
            txData = txData+HexUtil.HighLowHex(HexUtil.intToHex(crc)).toUpperCase();
            LogUtil.d("preview",txData.length()+"#####"+txData);
            if (txData.length() > 0) {
                byte[] data = DataUtil.hexToByteArray(txData);
                mLeProxy.send(mSelectedAddress, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if(ResultUtil.getResponseSuccess(data,Constants.preViewCode)){
//            LogUtil.d("dataStr",dataStr);
            LogUtil.d("preview","预览成功");
            isSendSuccess = true;
            isPreview = true;
            if(isPreviewing){
                send();
            }

        }
    }

    private void jumpUploadActivity(){
        isPreviewing = false;
        Intent intent = new Intent(CarvingPreviewActivity.this,UploadActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
        AppActivityManager.finishActivity(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
    }



}
