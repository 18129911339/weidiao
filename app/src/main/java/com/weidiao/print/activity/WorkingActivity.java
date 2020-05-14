package com.weidiao.print.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.weidiao.print.util.Stm32crc;
import com.weidiao.print.util.ToastUtil;
import com.weidiao.print.view.ConfirmDialog;

import static com.weidiao.print.activity.MainActivity.mSelectedAddress;

/**
 * 开始镭雕
 * Created by shcx on 2019/11/17.
 */

public class WorkingActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_title;
    private ProgressBar progressBar;
    private TextView tv_percent;
    private Chronometer tv_time;
    private TextView btn_cancel;
    private TextView tv_set_dec;
    private TextView btn_left,btn_right,btn_new,btn_again;
    private ImageView imageView;
    private String path;

    private LeProxy mLeProxy;

    private boolean isFinish = false;
    private boolean isPause = false;


    private int FINISH_CODE = 0;

    private LinearLayout ll_w1,ll_w2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working);
        initView();
        initData();
    }

    private void initView(){
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ll_w1 = findViewById(R.id.ll_w1);
        ll_w2 = findViewById(R.id.ll_w2);
        ll_w2.setVisibility(View.GONE);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.working_title));
        tv_percent = findViewById(R.id.tv_percent);
        tv_time = findViewById(R.id.tv_time);
        btn_cancel = findViewById(R.id.btn_cancel);
        imageView = findViewById(R.id.imageView);
        tv_set_dec = findViewById(R.id.tv_set_dec);
        tv_set_dec.setText("雕刻面积："+Constants.IMAGE_PARA_IMAGE_RESOLUTION+"mm，材料："
                +Constants.IMAGE_PARA_IMAGE_MATERIAL+"，雕刻深度："+Constants.IMAGE_PARA_IMAGE_DEPTH+"%");
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_left = findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        btn_new = findViewById(R.id.btn_new);
        btn_new.setOnClickListener(this);
        btn_again = findViewById(R.id.btn_again);
        btn_again.setOnClickListener(this);
    }

    private void initData(){
        path = getIntent().getStringExtra("path");
        GlideUtil.displayImage(this,path,imageView);
        mLeProxy = LeProxy.getInstance();
        mLeProxy.setEncrypt(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, makeFilter());
        progressBar.setMax(100);
        progressBar.setProgress(0);
        tv_percent.setText("0%");
//        tv_time.setBase(SystemClock.elapsedRealtime());
//        tv_time.start();
        startWork();


    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==FINISH_CODE){
                finishWork();
            }
        }
    };


    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_RSSI_AVAILABLE);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
        return filter;
    }

    private void startWork() {
        ll_w1.setVisibility(View.VISIBLE);
        ll_w2.setVisibility(View.GONE);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        tv_percent.setText("0%");
        tv_time.setText("00:00");
        isFinish = false;
        try {
            String txData = Constants.CODE_DEVICE_START;
            int crc = Stm32crc.getStm32crc(HexUtil.hexToShortArray(txData));
            txData = txData+HexUtil.HighLowHex(HexUtil.intToHex(crc)).toUpperCase();
            LogUtil.d("startWork",txData.length()+"#####"+txData);
            if (txData.length() > 0) {
                byte[] data = DataUtil.hexToByteArray(txData);
                mLeProxy.send(mSelectedAddress, data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
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
//        ToastUtil.showToast(this,dataStr);
//        Log.d("dddd",dataStr);

        if(data.length>10 ){
            if(HexUtil.byteToHex(data[9]).equals(Constants.startWorkCode)){
                if(data[2]==0){
                    LogUtil.d("xy","开始雕刻");
                    handler.postDelayed(runnable,1000);
                    btn_left.setText(getResources().getString(R.string.working_pause));
                }
            }else if(HexUtil.byteToHex(data[9]).equals(Constants.stateCode)){
                if(data[2]==0){
                    String state = HexUtil.byteToHex(data[10]);
                    if(state.equals("00")){
                        LogUtil.d("state","设备空闲");
                        isFinish = true;
                        btn_left.setText(getResources().getString(R.string.working_start));
                    }else if(state.equals("01")){
                        LogUtil.d("state","设备故障");
                        ToastUtil.showToast(this,"设备故障");
                        btn_left.setText("恢复雕刻");
                    }else if(state.equals("02")){
                        //75 41 00 01 00 01 00 14 00 31 02 08 53 6A 00 00 5F 3F AF 93
                        if(data.length<=15)return;
                        int progress = HexUtil.HexToInt(HexUtil.byteToHex(data[11]));
                        progressBar.setProgress(progress);
                        tv_percent.setText(progress+"%");
                        String strtime = HexUtil.byteToHex(data[15])+HexUtil.byteToHex(data[14])
                                +HexUtil.byteToHex(data[13])+HexUtil.byteToHex(data[12]);
                        int time = HexUtil.HexToInt(strtime);
                        String tt = com.weidiao.print.util.TimeUtil.msecToTime(time);
                        LogUtil.d("tt",tt);
                        tv_time.setText(tt);
                        LogUtil.d("state","正在打印"+"--"+progress + "  time:"+time + "***"+tt);
                    }else if(state.equals("03")){
                        LogUtil.d("state","暂停打印");
                        ToastUtil.showToast(this,"暂停打印");
                        btn_left.setText("恢复雕刻");
                    }else if(state.equals("04")){
                        LogUtil.d("state","打印完毕");
                        isFinish = true;
                        handler.removeCallbacks(runnable);
                        handler.sendEmptyMessage(FINISH_CODE);

                    }
                }
            }else  if(HexUtil.byteToHex(data[9]).equals(Constants.pauseWorkCode)){
                if(data[2]==0) {
                    LogUtil.d("xy", "暂停雕刻");
                    ToastUtil.showToast(this,"暂停雕刻");
                    btn_left.setText("恢复雕刻");
                }
            }else  if(HexUtil.byteToHex(data[9]).equals(Constants.stopWorkCode)){
                if(data[2]==0) {
                    LogUtil.d("xy", "停止雕刻");
                    ToastUtil.showToast(this,"停止雕刻");
                    isFinish = true;
                }
            }
        }
    }

    Runnable runnable=new Runnable(){

        @Override
        public void run() {
            if(!isFinish){
                getWorkState();
                handler.postDelayed(this, 1000);
            }
        }
    };


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_left:
                if(!isPause){
                    pauseWork();
                }else {
                    resumeWork();
                }
                break;
            case R.id.btn_right:
                stopWork();
                break;
            case R.id.btn_new:
                AppActivityManager.finishActivity(this);
                break;
            case R.id.btn_again:
                startWork();
                break;
        }
    }

    private void pauseWork(){
        final ConfirmDialog dialog = ConfirmDialog.create(this);
        dialog.setTvDec(getResources().getString(R.string.pause_work_dec));
        dialog.setConfirmListener(new ConfirmDialog.ConfirmClickListener() {
            @Override
            public void confirmClick() {
                isPause = true;
                btn_left.setText("恢复雕刻");
                String txData = Constants.CODE_DEVICE_PAUSE;
                int crc = Stm32crc.getStm32crc(HexUtil.hexToShortArray(txData));
                txData = txData+HexUtil.HighLowHex(HexUtil.intToHex(crc)).toUpperCase();
                LogUtil.d("pauseWork",txData.length()+"#####"+txData);
                byte[] data = DataUtil.hexToByteArray(txData);
                mLeProxy.send(mSelectedAddress, data);
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void resumeWork(){
        btn_left.setText("暂停雕刻");
        isPause = false;
        String txData = Constants.CODE_DEVICE_RESUME;
        int crc = Stm32crc.getStm32crc(HexUtil.hexToShortArray(txData));
        txData = txData+HexUtil.HighLowHex(HexUtil.intToHex(crc)).toUpperCase();
        LogUtil.d("resumeWork",txData.length()+"#####"+txData);
        if (txData.length() > 0) {
            byte[] data = DataUtil.hexToByteArray(txData);
            mLeProxy.send(mSelectedAddress, data);
        }

    }

    private void stopWork(){
        final ConfirmDialog dialog = ConfirmDialog.create(this);
        dialog.setTvDec(getResources().getString(R.string.stop_work_dec));
        dialog.setConfirmListener(new ConfirmDialog.ConfirmClickListener() {
            @Override
            public void confirmClick() {
                String txData = Constants.CODE_DEVICE_STOP;
                LogUtil.d("stopWork",txData.length()+"#####"+txData);
                byte[] data = DataUtil.hexToByteArray(txData);
                mLeProxy.send(mSelectedAddress, data);
                dialog.dismiss();
                AppActivityManager.getAppManager().finishActivity();
            }
        });
        dialog.show();
    }

    private void finishWork(){
        final ConfirmDialog dialog = ConfirmDialog.create(this);
        dialog.setTvDec("雕刻完成");
        dialog.getTv_cancel().setVisibility(View.GONE);
        dialog.setConfirmListener(new ConfirmDialog.ConfirmClickListener() {
            @Override
            public void confirmClick() {
                dialog.dismiss();
                ll_w1.setVisibility(View.GONE);
                ll_w2.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
    }
}
