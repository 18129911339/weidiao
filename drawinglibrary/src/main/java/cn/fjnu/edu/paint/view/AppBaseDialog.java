package cn.fjnu.edu.paint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import cn.edu.fjnu.utils.DeviceInfoUtils;

/**
 * @author GaoFei
 * App基础对话框
 */
public abstract class AppBaseDialog extends Dialog {
    /**
     * 对话框主视图
     */
    private View mView;
    private Context mContext;
    public AppBaseDialog(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(getLayoutRes(), null);
        setContentView(mView, new LinearLayout.LayoutParams(DeviceInfoUtils.getScreenWidth(mContext) * 3 / 4, LinearLayout.LayoutParams.WRAP_CONTENT));
        init();
    }




    /**
     * 返回资源ID
     * @return
     */
    public abstract int getLayoutRes();


    /**
     * 获取对话框的主视图
     * @return
     */
    public View getMainView(){
        return mView;
    }

    public abstract void init();


}