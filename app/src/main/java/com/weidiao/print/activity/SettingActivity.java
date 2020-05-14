package com.weidiao.print.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.weidiao.print.R;
import com.weidiao.print.ble.LeProxy;

/**
 * Created by shcx on 2019/12/3.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SettingActivity";
    private TextView tv_title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.setting));
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.ll_blue).setOnClickListener(this);
        findViewById(R.id.ll_notice).setOnClickListener(this);
        findViewById(R.id.ll_contact).setOnClickListener(this);
        findViewById(R.id.ll_about).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_blue:
                startActivity(new Intent(SettingActivity.this,BlueListActivity.class));
                break;
            case R.id.ll_notice:
                startActivity(new Intent(SettingActivity.this,NoticeActivity.class));
                break;
            case R.id.ll_contact:
                startActivity(new Intent(SettingActivity.this,ContactActivity.class));
                break;
            case R.id.ll_about:
                startActivity(new Intent(SettingActivity.this,AboutActivity.class));
                break;
        }
    }

}
