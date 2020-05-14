package cn.fjnu.edu.paint.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import cn.fjnu.edu.paint.engine.DrawView;
import cn.fjnu.edu.ui.activity.PaintMainActivity;

public class EXRelativeLayout  extends RelativeLayout {

	private float mX;
	private boolean isContinue;
	public EXRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public EXRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public EXRelativeLayout(Context context) {
		super(context);
	}



	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if(PaintMainActivity.isReduce)
				DrawView.listenMode= DrawView.PAINT_MODE;
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mX=event.getX();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(Math.abs(mX-getWidth())<10){
				isContinue=true;
				DrawView.listenMode= DrawView.SLIDE_MODE;
			}
				
			else
				isContinue=false;
			break;
		
		case MotionEvent.ACTION_MOVE:
			if(isContinue){
				if(Math.abs(mX-getWidth())>20){

				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if(PaintMainActivity.isReduce)
				DrawView.listenMode= DrawView.PAINT_MODE;
			break;
		default:
			break;
		}
	  return true;
	}
	
}
