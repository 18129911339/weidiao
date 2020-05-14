package com.weidiao.print.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.weidiao.print.BuildConfig;
import com.weidiao.print.R;

/**
 * Created by shcx on 2019/12/3.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "AboutActivity";
    private TextView tv_title,tv_app_name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();

    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("关于品牌");
        tv_app_name= findViewById(R.id.tv_app_name);
        findViewById(R.id.btn_back).setOnClickListener(this);
        tv_app_name.setText(getResources().getString(R.string.app_name)+"："+BuildConfig.VERSION_NAME);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_back:
                finish();
                break;
        }
    }

}
