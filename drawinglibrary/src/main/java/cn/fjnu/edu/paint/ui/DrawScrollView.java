package cn.fjnu.edu.paint.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import cn.fjnu.edu.paint.engine.DrawView;

public class DrawScrollView extends ScrollView {

	private GestureDetector mGestureDetector;
	View.OnClickListener mGestureListener;
	public static DrawScrollView drawScrollView;
	public DrawScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	//	mGestureDetector=new GestureDetector(new YS)
		mGestureDetector=new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
		getDrawScrollView();
	}

	public DrawScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector=new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
		getDrawScrollView();
	}

	public DrawScrollView(Context context) {
		super(context);
		mGestureDetector=new GestureDetector(new YScrollDetector());
		setFadingEdgeLength(0);
		getDrawScrollView();
	}

	public void getDrawScrollView(){
		drawScrollView= DrawScrollView.this;
	}
		@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
			return super.onInterceptTouchEvent(ev)&&mGestureDetector.onTouchEvent(ev)&&DrawView.isMove;
		}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}
	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
			if(Math.abs(distanceY)> Math.abs(distanceX)){
				return true;
			}
			return false;
		}
		
	}

}
