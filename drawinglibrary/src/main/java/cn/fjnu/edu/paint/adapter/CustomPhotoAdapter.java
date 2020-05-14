package cn.fjnu.edu.paint.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fjnu.utils.DeviceInfoUtils;
import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.ui.activity.PaintMainActivity;


/**
 * 
 * @author GaoFei
 *
 */
public class CustomPhotoAdapter extends BaseAdapter {
	private Context context;
	private List<Integer> photoId=new ArrayList<Integer>();
	private int mScreenWidth;
	public CustomPhotoAdapter(PaintMainActivity mActivity){
		context=mActivity;
		for(int i=0;i<46;i++){
			photoId.add(context.getResources().getIdentifier("b" + i, "drawable", context.getPackageName()));
		}
		mScreenWidth = DeviceInfoUtils.getScreenWidth(context);
	}
	@Override
	public int getCount() {
		return photoId.size();
	}

	@Override
	public Object getItem(int position) {
		
		return position;
	}

	@Override
	public long getItemId(int position) {
		return photoId.get(position);
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parentView) {
		View itemView;
		if(contentView == null){
			LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.paste_img_layout, parentView, false);
		}else{
			itemView = contentView;
		}
		ImageView imageView=(ImageView) itemView.findViewById(R.id.displaygridphoto);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(mScreenWidth / 4, LinearLayout.LayoutParams.WRAP_CONTENT));
		imageView.setImageResource(photoId.get(position));
		return imageView;
	}
}
