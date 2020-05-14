package com.weidiao.print.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.weidiao.print.R;


public class ConfirmDialog extends Dialog {

    private TextView tvOk,tv_cancel,tv_dec;

    private Context context;
    private ConfirmClickListener onConfirmClickListener;


    public static ConfirmDialog create(Context context) {
        ConfirmDialog instance = new ConfirmDialog(context);
        return instance;

    }

    private ConfirmDialog(Context context) {
        super(context, R.style.MyAlertDialog);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.dialog_confirm_layout, null);

        setContentView(contentView, lp);
        tvOk = (TextView) contentView.findViewById(R.id.tv_ok);
        tv_cancel = contentView.findViewById(R.id.tv_cancel);
        tv_dec = contentView.findViewById(R.id.tv_dec);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onConfirmClickListener) {
                    onConfirmClickListener.confirmClick();
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 设置确认的监听
     */
    public void setConfirmListener(ConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }


    public void setTvDec(String dec){
        tv_dec.setText(dec);
    }

    public TextView getTvOk() {
        return tvOk;
    }


    public TextView getTv_cancel() {
        return tv_cancel;
    }


    public interface ConfirmClickListener {
        public void confirmClick();
    }


}
