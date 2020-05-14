package cn.fjnu.edu.paint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.paint.adapter.PastePhotoAdapter;
import cn.fjnu.edu.paint.engine.DrawView;

/**
 * Created by gaofei on 2017/4/14.
 * 贴图选择对话框
 */

public class PastePhotoDialog extends AppBaseDialog {
    private Context mContext;
    private DrawView mDrawView;
    public PastePhotoDialog(Context context, DrawView drawView) {
        super(context);
        mContext = context;
        mDrawView = drawView;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.pastephoto_layout;
    }

    @Override
    public void init() {
        GridView photoGridView = (GridView) findViewById(R.id.paste_grid);
        //photoGridView.remove
        PastePhotoAdapter adapter = new PastePhotoAdapter(mContext);
        photoGridView.setAdapter(adapter);
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                mDrawView.setPaintMode(DrawView.COPY_MODE);
                Bitmap copyBitmap = BitmapFactory.decodeResource(mContext.getResources(), (int) id);
                mDrawView.setCopyBitmap(copyBitmap);
                dismiss();
            }

        });
    }
}
