package cn.fjnu.edu.paint.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.fjnu.utils.SizeUtils;

/**
 * 自定义的TextView
 */
public class PaintTextView extends View {
    final String TAG = "PaintTextView";
    /**画笔*/
    private Paint mPaint;
    /**要绘制的文字*/
    private String mText;
    public PaintTextView(Context context) {
        super(context);
    }

    public PaintTextView(Context context, @androidx.annotation.Nullable @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintTextView(Context context, @androidx.annotation.Nullable @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaintTextView(Context context, @androidx.annotation.Nullable @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "onFinishInflate");
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
        super.onDraw(canvas);

    }


    private void drawText(Canvas canvas){
        Rect textRect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), textRect);
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Log.i(TAG, "drawText->viewWidth:" + viewWidth);
        Log.i(TAG, "drawText->viewHeight:" + viewHeight);
        Log.i(TAG, "drawText->textWidth:" + 20);
        Log.i(TAG, "drawText->textHeight:" + 20);
        if(viewWidth > 0 && viewHeight > 0)
            canvas.drawText(mText, (viewWidth - textRect.width()) * 1.0f / 2, (viewHeight - textRect.height()) * 1.0f / 2 + textRect.height(), mPaint);
    }

    public void setText(String text){
        mText = text;
        Rect textRect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), textRect);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams != null){
            layoutParams.width = (int)(textRect.width() + 20);
            layoutParams.height = (int)(textRect.height() + 20);
            setLayoutParams(layoutParams);
            requestLayout();
        }
    }


    public void setTextSize(float textSize){
        mPaint.setTextSize(textSize);
        Rect textRect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), textRect);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams != null){
            layoutParams.width = (int)(textRect.width() + 20);
            layoutParams.height = (int)(textRect.height() + 20);
            setLayoutParams(layoutParams);
            requestLayout();
        }
    }

    private void init(){
        mText = "";
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //mPaint.setTextAlign(Paint.Align.CENTER);
        setTextSize(SizeUtils.dp2px(16f));
    }

}
