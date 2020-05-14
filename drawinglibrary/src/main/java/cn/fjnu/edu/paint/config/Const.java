package cn.fjnu.edu.paint.config;

import android.content.Context;
import android.os.Environment;

/**
 *
 * @author GaoFei
 *
 */

public class Const {

	public static Context appContext;
	public static int dateNum;
	public static long lastTime;
	public static long currentTime;
	//应用下载目录
	public static final String AppDownloadDir = Environment.getExternalStorageDirectory() + "/cn/edu/fjnu/paint/app_download";
	public static final long ONE_DAY_MILL=24*60*60*1000;
	public static final String SHARED_NAME="shared_name";
	public static class Key{
		public static final String DATE_NUM="date_num";
		public static final String LAST_TIME="last_time";
		public static final String UPDATE_TIP ="update_tip";
		public static final String DOWNLOAD_ID ="download_id";
		public static final String APP_INSTALL_TIME ="app_instanll_time";
		public static final String PEN_COLOR_SIZE = "pen_color_size";
		public static final String CLICK_AD = "click_ad";
		public static final String SHOW_AD = "show_ad";
	}
	
	
}
