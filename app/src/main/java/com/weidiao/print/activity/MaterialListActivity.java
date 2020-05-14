package com.weidiao.print.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzy.imagepicker.util.Utils;
import com.lzy.imagepicker.view.GridSpacingItemDecoration;
import com.weidiao.print.R;
import com.weidiao.print.adapter.MaterialListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shcx on 2019/11/17.
 */

public class MaterialListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private MaterialListAdapter adapter;
    private List<String> listData;
    private TextView tv_title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_list);
        initView();
        initData();
    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("素材");
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Utils.dp2px(this, 7), false));

        adapter = new MaterialListAdapter(this);
        recyclerView.setAdapter(adapter);
        listData = new ArrayList<>();
        adapter.setOnItemClickListener(new MaterialListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MaterialListActivity.this, MeterialImageCropActivity.class);
                String path = listData.get(position);
                intent.putExtra("path",path);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData(){
        for(int i=1;i<15;i++){
            listData.add("m_f"+i);
        }

        adapter.setImages(listData);
        adapter.notifyDataSetChanged();
    }


}
