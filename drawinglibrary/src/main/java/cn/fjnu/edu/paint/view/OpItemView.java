package cn.fjnu.edu.paint.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.paint.bean.OpInfo;

/**
 * 操作项
 * Created by gaofei on 2017/4/3.
 */

public class OpItemView extends LinearLayout {
    private OpInfo mInfo;
    private Context mContext;
    private ImageView mImgOp;
    private TextView mTextOp;
    public OpItemView(Context context, OpInfo opInfo){
        super(context);
        mContext = context;
        mInfo = opInfo;
        initView();
        initData();
    }

    private void initView(){
        setOrientation(LinearLayout.VERTICAL);
        View.inflate(mContext, R.layout.adapter_op_grid, this);
        mImgOp = (ImageView) findViewById(R.id.img_op);
        mTextOp = (TextView) findViewById(R.id.text_op);
    }

    private void initData(){
        mImgOp.setImageResource(mInfo.getResId());
        mTextOp.setText(mInfo.getTitle());
    }

}
