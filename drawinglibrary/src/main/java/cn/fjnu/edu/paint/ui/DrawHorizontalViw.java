package cn.fjnu.edu.paint.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import cn.fjnu.edu.paint.engine.DrawView;

public class DrawHorizontalViw extends HorizontalScrollView {

	public DrawHorizontalViw(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawHorizontalViw(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawHorizontalViw(Context context) {
		super(context);
	}
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
				if(DrawView.isMove){
					return true;
				}
					 
				else
					return false;
	}

}
