package cn.fjnu.edu.paint.view;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import cn.edu.fjnu.utils.DeviceInfoUtils;
import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.paint.bean.OpInfo;

/**
 * 操作对话框
 * Created by gaofei on 2017/3/26.
 */

public class OpDialog extends  AppBaseDialog implements View.OnClickListener{
    private static  final String TAG = "OpDialog";
    private static final String mTitles[] = {"贴图", "形状", "画笔", "粗细", "背景", "橡皮", "颜色", "清空", "文字"};
    private static int[] mPhotos = { R.drawable.paste_main, R.drawable.shape_main, R.drawable.paint_main,
            R.drawable.size_main, R.drawable.background_main, R.drawable.erase_main,
            R.drawable.color_main, R.drawable.empty_main, R.drawable.text_main};
    private static int[] mBackgrounds = {R.drawable.op_b1, R.drawable.op_b2, R.drawable.op_b3};
    private GridLayout mLayoutOpContainer;
    private Context mContext;
    private Callback mCallback;
    public interface Callback{
        void onClick(int index);
    }

    public OpDialog(Context context, Callback callback) {
        super(context);
        mContext = context;
        mCallback = callback;

    }


    @Override
    public int getLayoutRes() {
        return R.layout.dialog_op;
    }


    private void initData(){
        int dialogWidth = DeviceInfoUtils.getScreenWidth(mContext) * 3 / 4;
        int itemWidth = dialogWidth / 3;
        for(int i = 0; i < mTitles.length; ++i){
            OpInfo opInfo = new OpInfo();
            opInfo.setResId(mPhotos[i]);
            opInfo.setTitle(mTitles[i]);
            OpItemView opItemView = new OpItemView(mContext, opInfo);
            opItemView.setBackgroundColor(Color.parseColor("#515151"));
            //opItemView.setBackgroundResource(mBackgrounds[(i + i / 3) % 3]);
            opItemView.setOnClickListener(this);
            GridLayout.LayoutParams itemViewParams = new GridLayout.LayoutParams();
            itemViewParams.width = itemWidth;
            itemViewParams.height = itemWidth;
            mLayoutOpContainer.addView(opItemView, itemViewParams);

        }

    }

    @Override
    public void onClick(View view) {
        dismiss();
        mCallback.onClick(mLayoutOpContainer.indexOfChild(view));
    }

    public void init(){
        mLayoutOpContainer = (GridLayout) findViewById(R.id.layout_op_container);
        Log.i(TAG, "onCreate");
        initData();
    }
}
