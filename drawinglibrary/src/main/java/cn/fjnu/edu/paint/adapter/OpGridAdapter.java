package cn.fjnu.edu.paint.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.paint.bean.OpInfo;

/**
 * Created by gaofei on 2017/3/26.
 */

public class OpGridAdapter extends ArrayAdapter<OpInfo> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int mResId;
    public OpGridAdapter(Context context, int resource, List<OpInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mResId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(mResId, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imgOp = (ImageView) convertView.findViewById(R.id.img_op);
            holder.textOp = (TextView) convertView.findViewById(R.id.text_op);
            convertView.setTag(holder);
        }
        ViewHolder contentHolder = (ViewHolder) convertView.getTag();
        contentHolder.imgOp.setImageResource(getItem(position).getResId());
        contentHolder.textOp.setText(getItem(position).getTitle());
        return convertView;
    }

    class ViewHolder{
        ImageView imgOp;
        TextView textOp;
    }
}
