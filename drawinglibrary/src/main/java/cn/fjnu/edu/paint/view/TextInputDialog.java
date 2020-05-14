package cn.fjnu.edu.paint.view;

import android.content.Context;

import cn.fjnu.edu.paint.R;

/**
 * Created by gaofei on 2017/4/15.
 * 文本输入对话框
 */

public class TextInputDialog extends AppBaseDialog {
    public TextInputDialog(Context context){
        super(context);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_text_input;
    }

    @Override
    public void init() {

    }
}
