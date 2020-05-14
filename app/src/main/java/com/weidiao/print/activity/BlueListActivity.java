package com.weidiao.print.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ble.api.DataUtil;
import com.ble.ble.BleService;
import com.ble.ble.scan.LeScanResult;
import com.ble.ble.scan.LeScanner;
import com.ble.ble.scan.OnLeScanListener;
import com.ble.utils.ToastUtil;
import com.weidiao.print.R;
import com.weidiao.print.ble.LeDevice;
import com.weidiao.print.ble.LeProxy;
import com.weidiao.print.util.AppActivityManager;
import com.weidiao.print.util.HexUtil;
import com.weidiao.print.util.LogUtil;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;

import static com.weidiao.print.activity.MainActivity.mSelectedAddress;

/**
 * Created by shcx on 2019/12/3.
 */

public class BlueListActivity extends BaseActivity {

    private static final String TAG = "SearchBlueActivity";
    private static final int MSG_SCAN_STARTED = 1;
    private static final int MSG_SCAN_DEVICE = 2;
    private static final int MSG_SCAN_STOPPED = 3;

    private Handler mHandler = new MyHandler();

    private ListView listView;
    private LeDeviceListAdapter leDeviceListAdapter;

    private LeProxy mLeProxy = LeProxy.getInstance();

    private String base_name = "LaserCube" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_list);
        initView();

        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, makeFilter());
    }

    private void initView(){
        findViewById(R.id.btn_fresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fresh();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = findViewById(R.id.device_listview);
        leDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(leDeviceListAdapter);
        listView.setOnItemClickListener(mOnItemClickListener);
    }

    private void fresh(){
        leDeviceListAdapter.clear();
        LeScanner.startScan(mOnLeScanListener);
    }


    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //单击连接设备
            LeScanner.stopScan();
            LeDevice device = leDeviceListAdapter.getItem(position);
            mLeProxy.connect(device.getAddress(), false);

        }
    };




    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_CONNECTED);
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_CONNECT_ERROR);
        filter.addAction(LeProxy.ACTION_CONNECT_TIMEOUT);
        filter.addAction(LeProxy.ACTION_GATT_SERVICES_DISCOVERED);
        return filter;
    }

    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);

            switch (intent.getAction()) {
                case LeProxy.ACTION_GATT_CONNECTED:
                    ToastUtil.show(mContext, getString(R.string.scan_connected) + " " + address);
                    mSelectedAddress = address;
                    AppActivityManager.finishActivity(BlueListActivity.this);
//                    jumpMainActivity(address);
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    ToastUtil.show(mContext, getString(R.string.scan_disconnected) + " " + address);
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    ToastUtil.show(mContext, getString(R.string.scan_connection_error) + " " + address);
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    ToastUtil.show(mContext, getString(R.string.scan_connect_timeout) + " " + address);
                    break;
                case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                    ToastUtil.show(mContext, "Services discovered: " + address);
                    break;
            }
        }
    };

    private void jumpMainActivity( String address){
        Intent intent = new Intent(BlueListActivity.this,MainActivity.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }


    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected()");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LeProxy.getInstance().setBleService(service);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //申请打开手机蓝牙，requestCode为LeScanner.REQUEST_ENABLE_BT
            LeScanner.requestEnableBluetooth(this);
        }else{
            if (Build.VERSION.SDK_INT >= 23) {
                //TODO Android6.0开始，扫描是个麻烦事，得检测APP有没有定位权限，手机定位有没有开启
                if (!LeScanner.hasFineLocationPermission(this)) {
                    showAlertDialog(
                            R.string.scan_tips_no_location_permission,
                            R.string.to_grant_permission,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LeScanner.startAppDetailsActivity(BlueListActivity.this);
                                }
                            });
                } else if (!LeScanner.isLocationEnabled(this)) {
                    showAlertDialog(
                            R.string.scan_tips_location_service_disabled,
                            R.string.to_enable,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LeScanner.requestEnableLocation(BlueListActivity.this);
                                }
                            });
                } else {
                    LeScanner.startScan(mOnLeScanListener);
                }
            } else {
                LeScanner.startScan(mOnLeScanListener);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LeScanner.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        unbindService(mConnection);
    }


    //蓝牙扫描监听
    private final OnLeScanListener mOnLeScanListener = new OnLeScanListener() {
        @Override
        public void onScanStart() {
            mHandler.sendEmptyMessage(MSG_SCAN_STARTED);
        }

        @Override
        public void onLeScan(LeScanResult leScanResult) {
            BluetoothDevice device = leScanResult.getDevice();
            if(TextUtils.isEmpty(device.getName()))return;
            Message msg = new Message();
            msg.what = MSG_SCAN_DEVICE;
            msg.obj = new LeDevice(
                    device.getName(),
                    device.getAddress(),
                    leScanResult.getRssi(),
                    leScanResult.getLeScanRecord().getBytes()
            );
            mHandler.sendMessage(msg);
        }

        @Override
        public void onScanFailed(int errorCode) {
        }

        @Override
        public void onScanStop() {
            mHandler.sendEmptyMessage(MSG_SCAN_STOPPED);
        }
    };


    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SCAN_STARTED:
//                    fragment.mRefreshLayout.setRefreshing(true);
                    break;

                case MSG_SCAN_DEVICE:
                    leDeviceListAdapter.addDevice((LeDevice) msg.obj);
                    break;

                case MSG_SCAN_STOPPED:
//                    fragment.mRefreshLayout.setRefreshing(false);
                    break;

            }
        }
    }


    private void showAlertDialog(int msgId, int okBtnTextId, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(msgId)
                .setPositiveButton(okBtnTextId, okListener)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LeScanner.REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<LeDevice> mLeDevices = new ArrayList<>();

        void addDevice(LeDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                notifyDataSetChanged();
            }
        }

        void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public LeDevice getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_device_list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.txt_rssi);
                viewHolder.connect = (TextView) view.findViewById(R.id.btn_connect);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            LeDevice device = mLeDevices.get(i);
            String deviceName = device.getName();
            if (!TextUtils.isEmpty(deviceName)){
                if(device.getName().contains("BLE")){
                    LogUtil.d("device.getName()",device.getName());
                    if(!device.getName().contains(base_name)){
                        device.setName(base_name+"_"+device.getName());
                    }
                    viewHolder.deviceName.setText(device.getName());
                }else{
                    viewHolder.deviceName.setText(deviceName);
                }
            } else{
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.deviceRssi.setText("rssi: " + device.getRssi() + "dbm");

            return view;
        }
    }

    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        TextView connect;
    }

}
