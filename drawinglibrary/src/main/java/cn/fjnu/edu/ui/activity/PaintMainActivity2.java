package cn.fjnu.edu.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.edu.fjnu.utils.DeviceInfoUtils;
import cn.edu.fjnu.utils.OPUtils;
import cn.edu.fjnu.utils.SizeUtils;
import cn.fjnu.edu.paint.R;
import cn.fjnu.edu.paint.config.Const;
import cn.fjnu.edu.paint.data.Configs;
import cn.fjnu.edu.paint.data.Shape_Type;
import cn.fjnu.edu.paint.engine.Background;
import cn.fjnu.edu.paint.engine.DrawView;
import cn.fjnu.edu.paint.ui.ColorPicker;
import cn.fjnu.edu.paint.ui.DisplayPenSizeView;
import cn.fjnu.edu.paint.ui.OpacityBar;
import cn.fjnu.edu.paint.view.OpDialog;
import cn.fjnu.edu.paint.view.PaintTextView;
import cn.fjnu.edu.paint.view.PastePhotoDialog;

public class PaintMainActivity2 extends Activity {

    private static final String TAG = "PaintMainActivity";
    public static final int SHARE_MODE = 0;
    public static final int SAVE_MODE = 1;
    public static final int CUT_MODE = 2;
    public static int saveType = SAVE_MODE;
    public static String photopath;
    public static String mTmpCropPath;
    public static PaintMainActivity2 MActivity;
    public static int[] backID = new int[46];
    public static int[] bigBackId = new int[46];
    public static boolean isReduce = false;
    public static boolean isLoad = true;
    public boolean isMeasure = true;
    private  int DEFAULT_WIDTH = 480;
    private  int DEFAULT_HEIGHT = 800;
    private int opacity = 0xff;
    private DrawView canvansImageView;
    private SeekBar penSeekBar;
    private TextView penTextView;
    private DisplayPenSizeView processImageView;
    private ImageView mImgShowOp;
    private boolean isInitMain = false;
    private int penSize;
    private Button process_okButton;
    private Button process_cancelButton;
    private Dialog setsizeDialog;
    private View mainView;
    private ZoomControls zoomCanvas;
    private float oreignWidthScalex;
    private float oreignHeightScalex;
    private int penProcess = 5;
    public static int drawWidth;
    public static int drawHeight;
    public static int screenWidth;
    public static int screenHeight;
    public static DrawView canvasView;
    private boolean isBlackColor = false;
    private String paintText = null;
    private String[] mColors = {"#F44336", "#E91E63", "#9C27B0", "#2196F3", "#03A9F4", "#00BCD4",
            "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722",
            "#795548", "#9E9E9E", "#607D8B"};
    private static final int REQUEST_STORAGE_PERMISSION_CODE = 1;
    //广告容器页面
    private RelativeLayout mLayoutAd;
    private OpDialog mOpDialog;
    private PastePhotoDialog mPastePhotoDialog;
    private Dialog mTextInputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_paint);
        Log.i(TAG, "after setContentView");
        checkPermission();
        init();
        //初始化主功能
        initMain();
        //初始化广告
        //initAd();
        //canvansImageView.setImageResource(R.drawable.app_rm);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                showStorageRequestPermission();
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i(TAG, "onWindowFocusChaned->hasFocus:" + hasFocus);
        super.onWindowFocusChanged(hasFocus);
        if (isLoad) {
            drawWidth = mainView.getWidth();
            drawHeight = mainView.getHeight();
            canvansImageView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(mainView.getWidth(), mainView.getHeight()));
            isLoad = false;
            if (isMeasure) {
                screenWidth = mainView.getWidth();
                screenHeight = mainView.getHeight();
                isMeasure = false;
            }
        }


    }

    public void init() {
        DEFAULT_WIDTH = DeviceInfoUtils.getScreenWidth(this) / 2;
        DEFAULT_HEIGHT = DeviceInfoUtils.getScreenHeight(this) / 2;
        mImgShowOp = (ImageView) findViewById(R.id.img_show_op);
        canvansImageView = (DrawView) findViewById(R.id.img_canvans);
        canvansImageView.setImageResource(R.drawable.whitebackground);
        mImgShowOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOpDialog == null)
                   mOpDialog = new OpDialog(PaintMainActivity2.this, new OpDialog.Callback() {
                       @Override
                       public void onClick(int index) {
                           switch (index){
                               case 0:
                                   if(mPastePhotoDialog == null)
                                       mPastePhotoDialog = new PastePhotoDialog(PaintMainActivity2.this, canvansImageView);
                                   mPastePhotoDialog.show();
                                   break;
                               case 1:
                                   AlertDialog alertDialog = new AlertDialog.Builder(PaintMainActivity2.this, android.R.style.Theme_Holo_Light_Dialog)
                                           .setTitle("选择绘图类型")
                                           .setSingleChoiceItems(
                                                   new String[]{"直线", "折线", "矩形",
                                                           "六边形", "椭圆", "自由手绘"},
                                                   canvansImageView.getCurrentShape(),
                                                   new DialogInterface.OnClickListener() {

                                                       @Override
                                                       public void onClick(DialogInterface dialog,
                                                                           int which) {
                                                           switch (which) {

                                                               case Shape_Type.STRAIGIT:
                                                                   canvansImageView
                                                                           .setShape(Shape_Type.STRAIGIT);
                                                                   break;
                                                               case Shape_Type.BROKEN:
                                                                   canvansImageView.setShape(Shape_Type.BROKEN);
                                                                   break;
                                                               case Shape_Type.RECT:
                                                                   canvansImageView
                                                                           .setShape(Shape_Type.RECT);
                                                                   break;
                                                               case Shape_Type.MUTIL:
                                                                   canvansImageView.setShape(Shape_Type.MUTIL);
                                                                   break;
                                                               case Shape_Type.OVAL:
                                                                   canvansImageView.setShape(Shape_Type.OVAL);
                                                                   break;
                                                               case Shape_Type.FREE:
                                                                   canvansImageView
                                                                           .setShape(Shape_Type.FREE);
                                                                   break;
                                                               default:
                                                                   break;
                                                           }
                                                           canvansImageView.setCurrentShape();
                                                           if (canvansImageView.getPaintMode() != DrawView.COMMON_MODE) {
                                                               // fillcolorImageView.setBackgroundColor(Color.TRANSPARENT);
                                                               // cutImageView.setBackgroundColor(Color.TRANSPARENT);
                                                               canvansImageView.setPaintMode(DrawView.COMMON_MODE);
                                                           }

                                                           dialog.dismiss();
                                                       }

                                                   }).create();
                                   alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                   alertDialog.show();
                                   break;
                               case 2:
                                   if (canvansImageView.getPaintMode() != DrawView.COMMON_MODE) {
                                       canvansImageView.setPaintMode(DrawView.COMMON_MODE);
                                   }
                                   break;
                               case 3:
                                   setsizeDialog = new Dialog(PaintMainActivity2.this, android.R.style.Theme_Holo_Light_Dialog);
                                   setsizeDialog.setTitle("画笔粗细");
                                   setsizeDialog.setContentView(getLayoutInflater().inflate(R.layout.shape_paint, null));
                                   setsizeDialog.getWindow().setLayout(DeviceInfoUtils.getScreenWidth(PaintMainActivity2.this), ViewGroup.LayoutParams.WRAP_CONTENT);
                                   setsizeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                       @Override
                                       public void onShow(DialogInterface dialog) {
                                           processImageView.displayPenSize(Integer.parseInt(OPUtils.getValFromSharedpreferences(Const.Key.PEN_COLOR_SIZE)));
                                       }
                                   });
                                   penTextView = (TextView) setsizeDialog.getWindow().findViewById(R.id.process_text);
                                   processImageView = (DisplayPenSizeView) setsizeDialog.getWindow().findViewById(R.id.pen_shape);
                                   process_okButton = (Button) setsizeDialog.getWindow().findViewById(R.id.pen_ok);
                                   process_cancelButton = (Button) setsizeDialog.getWindow().findViewById(R.id.pen_cancel);
                                   process_okButton.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           OPUtils.saveValToSharedpreferences(Const.Key.PEN_COLOR_SIZE, "" + penProcess);
                                           canvansImageView.setPenSize(penProcess);
                                           setsizeDialog.dismiss();
                                       }
                                   });
                                   process_cancelButton.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           setsizeDialog.dismiss();
                                       }
                                   });
                                   penSeekBar = (SeekBar) setsizeDialog.getWindow().findViewById(R.id.pen_seekbar);
                                   penSeekBar.setMax(50);
                                   penSeekBar.setProgress(Integer.parseInt(OPUtils.getValFromSharedpreferences(Const.Key.PEN_COLOR_SIZE)));
                                   penTextView.setText(OPUtils.getValFromSharedpreferences(Const.Key.PEN_COLOR_SIZE));
                                   penSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                       @Override
                                       public void onStopTrackingTouch(SeekBar seekBar) {
                                           penProcess = seekBar.getProgress();
                                           processImageView.displayPenSize(penProcess);
                                       }

                                       @Override
                                       public void onStartTrackingTouch(SeekBar seekBar) {
                                       }

                                       @Override
                                       public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                           penTextView.setText("" + progress);
                                           processImageView.displayPenSize(progress);
                                       }
                                   });
                                   setsizeDialog.show();
                                   break;
                               case 4:
                                   Background backDialog = new Background(PaintMainActivity2.this,
                                           android.R.style.Theme_Holo_Light_Dialog);
                                   backDialog.setTitle("更换背景图");
                                   backDialog.show();
                                   break;
                               case 5:
                                   if (canvansImageView.getPaintMode() != DrawView.ERASER_MODE) {
                                       canvansImageView.setPaintMode(DrawView.ERASER_MODE);
                                   }
                                   break;
                               case 6:
                                   final Dialog colorDialog = new Dialog(PaintMainActivity2.this, android.R.style.Theme_Holo_Light_Dialog);
                                   colorDialog.setTitle("颜色选择");
                                   colorDialog.setContentView(R.layout.dialog_for_selectcolor);
                                   final ColorPicker colorPicker = (ColorPicker) colorDialog
                                           .findViewById(R.id.picker);
                                   colorPicker.setOldCenterColor(canvansImageView.getColor());
                                   colorPicker.setColor(canvansImageView.getColor());
                                   final OpacityBar opacityBar = (OpacityBar) colorDialog
                                           .findViewById(R.id.opacitybar);
                                   final CheckBox blackCheckBox = (CheckBox) colorDialog.findViewById(R.id.black_checkbox);
                                   blackCheckBox.setChecked(isBlackColor);
                                   colorPicker.addOpacityBar(opacityBar);
                                   opacityBar.setOpacity(opacity);
                                   Button colorOKButton = (Button) colorDialog
                                           .findViewById(R.id.colorok);
                                   Button colorCancelButton = (Button) colorDialog
                                           .findViewById(R.id.colorcancel);
                                   colorOKButton.setOnClickListener(new View.OnClickListener() {

                                       @Override
                                       public void onClick(View arg0) {
                                           // colorPicker.getColor()
                                           if (blackCheckBox.isChecked()) {
                                               isBlackColor = true;
                                               canvansImageView.setColor(Color.BLACK);
                                           } else {
                                               isBlackColor = false;
                                               opacity = opacityBar.getOpacity();
                                               canvansImageView.setColor(colorPicker.getColor());
                                           }
                                           //penAlpha=colorPicker.getAlpha();
                                           colorDialog.dismiss();
                                       }
                                   });
                                   colorCancelButton
                                           .setOnClickListener(new View.OnClickListener() {

                                               @Override
                                               public void onClick(View arg0) {
                                                   colorDialog.dismiss();
                                               }
                                           });
                                   colorDialog.show();
                                   break;
                               case 7:
                                   canvansImageView.clear();
                                   break;
                               case 8:
                                   if(mTextInputDialog == null){
                                       mTextInputDialog = new Dialog(PaintMainActivity2.this, android.R.style.Theme_Holo_Light_Dialog);
                                       mTextInputDialog.setTitle("自定义文字");
                                       mTextInputDialog.setContentView(R.layout.dialog_text_input);
                                       final EditText editInput = (EditText) mTextInputDialog.findViewById(R.id.edit_input);
                                       final SeekBar seekBarText = mTextInputDialog.findViewById(R.id.sb_text);
                                       final PaintTextView paintTextView = mTextInputDialog.findViewById(R.id.ptv);
                                       seekBarText.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                           @Override
                                           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                               float dpTextSize = (progress * 1.0f  / seekBar.getMax())  * (Configs.MAX_PAINT_TEXT_SIZE - Configs.MIN_PAINT_TEXT_SIZE);
                                               paintTextView.setTextSize(SizeUtils.dp2px(dpTextSize));
                                               //读取EditText文字内容
                                               //String inputText = editInput.getText().toString().trim();
                                               //paintTextView.setText(inputText.trim());
                                           }

                                           @Override
                                           public void onStartTrackingTouch(SeekBar seekBar) {

                                           }

                                           @Override
                                           public void onStopTrackingTouch(SeekBar seekBar) {

                                           }
                                       });

                                       editInput.addTextChangedListener(new TextWatcher() {
                                           @Override
                                           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                           }

                                           @Override
                                           public void onTextChanged(CharSequence s, int start, int before, int count) {

                                           }

                                           @Override
                                           public void afterTextChanged(Editable s) {
                                               //读取EditText文字内容
                                               String inputText = editInput.getText().toString().trim();
                                               paintTextView.setText(inputText.trim());
                                           }
                                       });
                                       Button btnOK = (Button) mTextInputDialog.findViewById(R.id.btn_ok);
                                       Button btnCancel = (Button) mTextInputDialog.findViewById(R.id.btn_cancel);
                                       btnOK.setOnClickListener(new View.OnClickListener(){
                                           @Override
                                           public void onClick(View v) {
                                               paintText = editInput.getText().toString().trim();
                                               if (paintText.equals("")) {
                                                   Toast.makeText(PaintMainActivity2.this, "请输入自定义文字", Toast.LENGTH_SHORT).show();
                                                   return;
                                               }
                                               float dpTextSize = (seekBarText.getProgress() * 1.0f  / seekBarText.getMax())  * (Configs.MAX_PAINT_TEXT_SIZE - Configs.MIN_PAINT_TEXT_SIZE);
                                               canvansImageView.setPaintTextSize(SizeUtils.dp2px(dpTextSize));
                                               canvansImageView.setPaintMode(DrawView.PASTETEXT_MODE);
                                               canvansImageView.setPaintText(paintText);
                                               mTextInputDialog.dismiss();
                                           }
                                       });
                                       btnCancel.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               mTextInputDialog.dismiss();
                                           }
                                       });
                                   }
                                   mTextInputDialog.show();
                                   break;
                           }

                       }
                   });
                mOpDialog.show();
            }
        });
        mLayoutAd = (RelativeLayout) findViewById(R.id.layout_ad);
        MActivity = PaintMainActivity2.this;
        String saveDir = Environment.getExternalStorageDirectory()
                + "/drawphoto";
        File saveDirFile = new File(saveDir);
        if (!saveDirFile.exists())
            saveDirFile.mkdirs();
        for (int i = 0; i < 46; i++) {
            int resID = getResources().getIdentifier("b" + i, "drawable",
                    getPackageName());
            if (resID != 0) {
                backID[i] = resID;
            }
            resID = getResources().getIdentifier("b" + i, "drawable",
                    getPackageName());
            if (resID != 0) {
                bigBackId[i] = resID;
            }

        }
        canvasView = canvansImageView;
        mainView = findViewById(R.id.rlay);
        zoomCanvas = (ZoomControls) findViewById(R.id.zoom_control);
        zoomCanvas.setVisibility(View.INVISIBLE);
        oreignWidthScalex = canvansImageView.getScaleX();
        oreignHeightScalex = canvansImageView.getScaleY();
        zoomCanvas.setOnZoomInClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                float widthScalex = canvansImageView.getScaleX();
                float heightScalex = canvansImageView.getScaleY();
                if (widthScalex + 0.2 < 5 && heightScalex + 0.2 < 5) {
                    canvansImageView.setScaleX((float) (widthScalex + 0.2));
                    canvansImageView.setScaleY((float) (heightScalex + 0.2));
                }
                if (oreignHeightScalex < heightScalex
                        || oreignWidthScalex < widthScalex) {
                    isReduce = false;
                }
            }
        });
        zoomCanvas.setOnZoomOutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                float widthScalex = canvansImageView.getScaleX();
                float heightScalex = canvansImageView.getScaleY();
                if (widthScalex - 0.2 > 0 && heightScalex - 0.2 > 0) {
                    canvansImageView.setScaleX((float) (widthScalex - 0.2));
                    canvansImageView.setScaleY((float) (heightScalex - 0.2));
                }
                if (oreignHeightScalex > heightScalex
                        || oreignWidthScalex > widthScalex) {
                    isReduce = true;
                }
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (RESULT_OK == resultCode) {
            if (requestCode == 1) {
                try {
                    if(!startZoomPhoto(FileProvider.getUriForFile(this, "cn.fjnu.edu.paint.fileprovider", new File(photopath)))){
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(photopath, options);
                        int scaleX, scaleY, imageWidth, imageHeight;
                        imageWidth = options.outWidth;
                        imageHeight = options.outHeight;
                        scaleX = (imageWidth + DEFAULT_WIDTH - 1) / DEFAULT_WIDTH;
                        scaleY = (imageHeight + DEFAULT_HEIGHT - 1) / DEFAULT_HEIGHT;
                        int maxScale = Math.max(scaleX, scaleY);
                        if(maxScale < 1)
                            options.inSampleSize = 1;
                        else
                            options.inSampleSize = maxScale;
                        options.inJustDecodeBounds = false;
                        Bitmap btp = BitmapFactory.decodeFile(photopath, options);
                        if(btp != null){
                            canvansImageView.setImageBitmap(btp);
                            if(photopath != null)
                                new File(photopath).delete();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "文件保存失败，请重试...", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {
                try {
                    Uri uri = data.getData();
                    if(uri != null && uri.toString().startsWith("content://com.android.providers") && Build.VERSION.SDK_INT >= 19){
                        String wholeID = DocumentsContract.getDocumentId(uri);
                        String id = wholeID.split(":")[1];
                        String[] column = { MediaStore.Images.Media.DATA };
                        String sel = MediaStore.Images.Media._ID + "=?";
                        Cursor cursor = getContentResolver().
                                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        column, sel, new String[]{ id }, null);
                        String filePath = "";
                        if(cursor == null)
                            return;
                        int columnIndex = cursor.getColumnIndex(column[0]);

                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex);
                        }
                        if(TextUtils.isEmpty(filePath))
                            return;
                        uri = FileProvider.getUriForFile(this, "cn.fjnu.edu.paint.fileprovider", new File(filePath));
                        cursor.close();
                    }
                    if(uri != null && !startZoomPhoto(uri)){
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(inputStream, null, options);
                        inputStream.close();
                        int imageWidth, imageHeight;
                        imageWidth = options.outWidth;
                        imageHeight = options.outHeight;
                        Log.i(TAG, "imageWidth:" + imageWidth);
                        Log.i(TAG, "imageHeight:" + imageHeight);
                        int scaleX, scaleY;
                        scaleX = (imageWidth + DEFAULT_WIDTH - 1) / DEFAULT_WIDTH;
                        scaleY = (imageHeight + DEFAULT_HEIGHT - 1) / DEFAULT_HEIGHT;
                        int maxScale = Math.max(scaleX, scaleY);
                        if(maxScale < 1)
                            options.inSampleSize = 1;
                        else
                            options.inSampleSize = maxScale;
                        options.inSampleSize = Math.max(scaleX, scaleY);
                        options.inJustDecodeBounds = false;
                        inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bm = BitmapFactory.decodeStream(inputStream, null, options);
                        canvansImageView.setImageBitmap(bm);
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "选择图片失败...", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }

            } else if (requestCode == 3) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap thePic = extras.getParcelable("data");
                    if (saveType == CUT_MODE) {

                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "yyyy_MM_dd_kk_mm_ss");
                        String date = formatter.format(new Date());
                        String pathString = Environment
                                .getExternalStorageDirectory()
                                + "/drawphoto/"
                                + date + ".png";
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(
                                    pathString);
                            thePic.compress(CompressFormat.PNG, 90,
                                    fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Toast.makeText(this, "文件保存成功" + pathString,
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(this, "文件保存失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else
                        canvansImageView.setImageBitmap(thePic);
                }

            }else if(requestCode == 4){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mTmpCropPath, options);
                int scaleX, scaleY, imageWidth, imageHeight;
                imageWidth = options.outWidth;
                imageHeight = options.outHeight;
                scaleX = (imageWidth + DEFAULT_WIDTH - 1) / DEFAULT_WIDTH;
                scaleY = (imageHeight + DEFAULT_HEIGHT - 1) / DEFAULT_HEIGHT;
                int maxScale = Math.max(scaleX, scaleY);
                if(maxScale < 1)
                    options.inSampleSize = 1;
                else
                    options.inSampleSize = maxScale;
                options.inJustDecodeBounds = false;
                Bitmap btp = BitmapFactory.decodeFile(mTmpCropPath, options);
                if(btp != null){
                    canvansImageView.setImageBitmap(btp);
                }
                //删除临时文件
                if(photopath != null)
                    new File(photopath).delete();
                if(mTmpCropPath != null)
                    new File(mTmpCropPath).delete();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            new AlertDialog.Builder(PaintMainActivity.this)
//                    .setTitle("温馨提示")
//                    .setMessage("确认退出?")
//                    .setPositiveButton("是",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    dialog.dismiss();
//                                    finish();
//                                    System.exit(0);
//
//                                }
//                            })
//                    .setNegativeButton("否", null).show();
//
//        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.undo) {
            canvansImageView.undo();
        } else if (id == R.id.reply) {
            canvansImageView.redo();
        } else if (id == R.id.share) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showStorageRequestPermission();
                return true;
            }
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy_MM_dd_kk_mm_ss");
                String date = formatter.format(new Date());
                String dirPath = Environment.getExternalStorageDirectory() + "/drawphoto";
                File dirFile = new File(dirPath);
                if (!dirFile.exists())
                    dirFile.mkdirs();
                File pathFile = new File(dirFile, date + ".png");
                canvansImageView.saveImage(pathFile.getAbsolutePath(), SHARE_MODE, false);
                if (pathFile.exists()) {
                    MediaScannerConnection.scanFile(this, new String[]{pathFile.getAbsolutePath()}, new String[]{"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null) {
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                PaintMainActivity2.this.startActivity(Intent.createChooser(
                                        shareIntent, "请选择"));
                            }
                        }
                    });

                } else {
                    Toast.makeText(PaintMainActivity2.this, "文件保存失败", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(PaintMainActivity2.this, "不存在可分享应用", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (id == R.id.save) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showStorageRequestPermission();
                return true;
            }

            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy_MM_dd_kk_mm_ss");
            String date = formatter.format(new Date());
            String dirPath = Environment.getExternalStorageDirectory() + "/drawphoto";
            File dirFile = new File(dirPath);
            if (!dirFile.exists())
                dirFile.mkdirs();
            File pathFile = new File(dirFile, date + ".png");
            canvansImageView.saveImage(pathFile.getAbsolutePath(), SAVE_MODE, true);
        } else if (id == R.id.closecolor) {
            disClsColDialog();
        } else if (id == R.id.areaselect) {
            canvansImageView.setPaintMode(DrawView.CUT_MODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 启动图片剪切
     */
    private boolean startZoomPhoto(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        mTmpCropPath = Environment.getExternalStorageDirectory() + File.separator + UUID.randomUUID().toString() + ".png";
        Uri cropUri = FileProvider.getUriForFile(this, "cn.fjnu.edu.paint.fileprovider", new File(mTmpCropPath));
        intent.setDataAndType(uri, "image/*");
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, 0);
        if(resolveInfos == null || resolveInfos.size() == 0)
            return false;
        for(ResolveInfo itemInfo : resolveInfos){
            Log.i(TAG, "startZoomPhoto->packageName:" + itemInfo.activityInfo.packageName);
            grantUriPermission(itemInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            grantUriPermission(itemInfo.activityInfo.packageName, cropUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", drawWidth);
        intent.putExtra("aspectY", drawHeight);
        // 设置为true直接返回bitmap
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", CompressFormat.PNG.toString());
        //getPackageManager().queryIntentActivities();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 4);
        return  true;
    }

    public ZoomControls getZoomCanvans() {
        return zoomCanvas;
    }

    public int getOpacity() {
        return opacity;
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }


    public void disClsColDialog() {
        final Dialog colorDialog = new Dialog(PaintMainActivity2.this, android.R.style.Theme_Holo_Light_Dialog);
        colorDialog.setTitle("颜色选择");
        colorDialog.setContentView(R.layout.dialog_for_selectcolor);
        final ColorPicker colorPicker = (ColorPicker) colorDialog
                .findViewById(R.id.picker);
        colorPicker.setColor(canvansImageView.getColor());
        final OpacityBar opacityBar = (OpacityBar) colorDialog
                .findViewById(R.id.opacitybar);
        colorPicker.addOpacityBar(opacityBar);
        opacityBar.setOpacity(opacity);
        Button colorOKButton = (Button) colorDialog.findViewById(R.id.colorok);
        Button colorCancelButton = (Button) colorDialog
                .findViewById(R.id.colorcancel);
        colorOKButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                canvansImageView.setFillColor(colorPicker.getColor());
                canvansImageView.setPaintMode(DrawView.FILLCOLOR_MODE);
                colorDialog.dismiss();
            }
        });
        colorCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                colorDialog.dismiss();
            }
        });
        // colorDialog.findViewById(R.id.sv)
        colorDialog.show();
    }


    /**
     * 初始化主界面功能
     **/
    public void initMain() {

    }


    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION_CODE);
        }
    }

    private void showStorageRequestPermission(){
        //提示权限设置对话框
        new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("您必须允许存储权限才能读取,保存,分享图片，可以通过系统设置授予应用权限")
                .setPositiveButton("确定", null).setCancelable(false).show();
    }
}
