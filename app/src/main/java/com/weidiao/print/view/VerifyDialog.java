package com.weidiao.print.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.weidiao.print.R;


public class VerifyDialog extends Dialog {

    private VerificationCodeView verificationCodeView;
    private TextView tvOk;
    private EditText et_verification;

    private Context context;
    private ConfirmClickListener onConfirmClickListener;


    public static VerifyDialog create(Context context) {
        VerifyDialog instance = new VerifyDialog(context);
        return instance;

    }

    private VerifyDialog(Context context) {
        super(context, R.style.MyAlertDialog);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.dialog_verify_layout, null);

        setContentView(contentView, lp);
        tvOk = (TextView) contentView.findViewById(R.id.tv_ok);
        et_verification = (EditText) contentView.findViewById(R.id.et_verification);
        verificationCodeView = (VerificationCodeView) contentView.findViewById(R.id.verificationCodeView);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onConfirmClickListener) {
                    onConfirmClickListener.confirmClick(verificationCodeView.getVerificationText(),et_verification.getText().toString());
                }
            }
        });
    }

    /**
     * 设置确认的监听
     */
    public void setConfirmListener(ConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }


    public void setVerifyCode(String code){
        verificationCodeView.setVerificationText(code);
    }

    public interface ConfirmClickListener {
        public void confirmClick(String code,String etCode);
    }


}
