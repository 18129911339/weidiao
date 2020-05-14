package com.weidiao.print.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.imagepicker.util.Utils;
import com.weidiao.print.R;
import com.weidiao.print.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class MaterialListAdapter extends RecyclerView.Adapter {
    private Context mContext;

    private List<String> images;       //当前需要显示的所有的图片数据
    private int width;
    private String BusiSerialNo;
    private String type="0"; //0预评，1进件

    private OnItemClickListener onItemClickListener;

    public MaterialListAdapter(Context mContext) {
        this.mContext = mContext;
        images = new ArrayList<>();
        width = (ScreenUtils.getScreenWidth(mContext)- Utils.dp2px(mContext,46))/3;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_material_list, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final String name = images.get(position);
        int id = mContext.getResources().getIdentifier(name, "mipmap", mContext.getPackageName());
        viewHolder.imageView.setImageResource(id);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setBusiSerialNo(String busiSerialNo) {
        BusiSerialNo = busiSerialNo;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width,width));

        }
    }

    // 自定义点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
