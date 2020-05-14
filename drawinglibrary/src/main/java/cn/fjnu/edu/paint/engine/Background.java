package cn.fjnu.edu.paint.engine;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.UUID;

import cn.edu.fjnu.utils.DeviceInfoUtils;
import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.paint.adapter.CustomPhotoAdapter;
import cn.fjnu.edu.ui.activity.PaintMainActivity;

public class Background extends Dialog {

		public Background(final Context context, int theme) {
			super(context, theme);
			setContentView(R.layout.dialog_for_newcreate);
			ListView listView = (ListView) findViewById(R.id.newlist);
			String[] way = { "相机", "文件", "自带"};
			ArrayAdapter<String> adapter=new ArrayAdapter<>(context, R.layout.list_text_center,way);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
					switch(position){
					case 0:
						try{
							PaintMainActivity.photopath = Environment.getExternalStorageDirectory() + File.separator + UUID.randomUUID().toString() + ".jpg";
                            Uri uri = FileProvider.getUriForFile(context, "cn.fjnu.edu.paint.fileprovider", new File(PaintMainActivity.photopath));
							Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
							if(resolveInfos == null || resolveInfos.size() == 0){
								Toast.makeText(context, "未安装相机应用", Toast.LENGTH_SHORT).show();
							}else{
								for(ResolveInfo resolveInfo : resolveInfos){
									context.grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
									context.grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
								}
								intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
								//intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
								intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
								PaintMainActivity.MActivity.startActivityForResult(intent, 1);
							}

						}
						catch(Exception e){
							e.printStackTrace();
						}
						dismiss();
						break;
					case 1:
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
						if(resolveInfos == null || resolveInfos.size() == 0){
							Toast.makeText(context, "未安装图库应用", Toast.LENGTH_SHORT).show();
						}else{
							for(ResolveInfo resolveInfo : resolveInfos)
								Log.i("Background", "resolveInfo->activityInfo->packageName:" + resolveInfo.activityInfo.packageName);
							PaintMainActivity.MActivity.startActivityForResult(intent,2);
						}
						dismiss();
						break;
					case 2:
						dismiss();
						GridView gridView;
						final Dialog backDialog=new Dialog(getContext(), android.R.style.Theme_Holo_Light_Dialog);
						backDialog.setTitle("选择背景图");
						backDialog.setContentView(R.layout.background);
						gridView=(GridView)backDialog.findViewById(R.id.grid_background);
						gridView.setGravity(Gravity.CENTER);
						gridView.setColumnWidth(DeviceInfoUtils.getScreenWidth(context)/3);
						CustomPhotoAdapter customPhotoAdapter=new CustomPhotoAdapter(PaintMainActivity.MActivity);
						gridView.setAdapter(customPhotoAdapter);
						gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {
								Bitmap bitmap= BitmapFactory.decodeResource(PaintMainActivity.MActivity.getResources(), PaintMainActivity.bigBackId[position]);
								int widthCount=PaintMainActivity.drawWidth/bitmap.getWidth();
								int heightCount=PaintMainActivity.drawHeight/bitmap.getHeight();
								int paintWidth=widthCount*bitmap.getWidth();
								int paintHeight=heightCount*bitmap.getHeight();
								Bitmap drawBitmap= Bitmap.createBitmap(paintWidth, paintHeight, Config.RGB_565);
								Canvas mCanvas=new Canvas(drawBitmap);
								for(int i=0;i<heightCount;i++)
									for(int j=0;j<widthCount;j++){
										mCanvas.drawBitmap(bitmap,j*bitmap.getWidth(), i*bitmap.getHeight(),null);
									}
								PaintMainActivity.canvasView.setImageBitmap(drawBitmap);
								backDialog.dismiss();
							}
						});
						backDialog.show();
						break;
					}
				}
			});
		}

	}