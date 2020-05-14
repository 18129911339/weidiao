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

/**
 * 
 * @author GaoFei
 * 贴图适配器
 */
public class PastePhotoAdapter extends BaseAdapter {

	private Context mContext;
	private List<Integer> photoId=new ArrayList<>();
	private int mScreenWidth;
	public PastePhotoAdapter(Context context){
		mContext=context;
		for(int i=0;i<319;i++){
			photoId.add(mContext.getResources().getIdentifier("p" + i, "drawable", mContext.getPackageName()));
		}
		mScreenWidth = DeviceInfoUtils.getScreenWidth(mContext);
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
		ImageView imageView;
		if(contentView == null){
			itemView = LayoutInflater.from(mContext).inflate(R.layout.paste_img_layout, parentView, false);
			imageView = (ImageView) itemView.findViewById(R.id.displaygridphoto);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(mScreenWidth/10, mScreenWidth/10));
		}else{
			itemView = contentView;
			imageView = (ImageView) itemView.findViewById(R.id.displaygridphoto);
		}
		imageView.setImageResource(photoId.get(position));
		return itemView;
	}


}
