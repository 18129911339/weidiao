package com.weidiao.print.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.weidiao.print.R;

/**
 * Created by shcx on 2019/12/3.
 */

public class ContactActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "NoticeActivity";
    private TextView tv_title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();

    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("联系我们");
        findViewById(R.id.btn_back).setOnClickListener(this);
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
