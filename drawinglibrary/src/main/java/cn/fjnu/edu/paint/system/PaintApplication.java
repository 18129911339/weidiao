package cn.fjnu.edu.paint.system;

import java.util.Date;

import cn.edu.fjnu.utils.OPUtils;
import cn.edu.fjnu.utils.system.BaseApplication;
import cn.fjnu.edu.paint.config.Config;
import cn.fjnu.edu.paint.config.Const;

public class PaintApplication extends BaseApplication {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();;
		Const.appContext=getApplicationContext();
		//还原画笔的粗细
		OPUtils.saveValToSharedpreferences(Const.Key.PEN_COLOR_SIZE, "" + 5);
		//还原广告渲染
		//OPUtils.saveValToSharedpreferences(Const.Key.SHOW_AD, "");
		String dateNum= Config.getValue(Const.Key.DATE_NUM);
		//String lastTime=Config.getValue(Const.Key.LAST_TIME);
		if("".equals(dateNum)){
			Config.saveValue(Const.Key.DATE_NUM, "0");
			Config.saveValue(Const.Key.LAST_TIME, String.valueOf(System.currentTimeMillis()));
		}
		Const.dateNum= Integer.parseInt( Config.getValue(Const.Key.DATE_NUM));
		Const.lastTime= Long.parseLong(Config.getValue(Const.Key.LAST_TIME));
		Const.currentTime= System.currentTimeMillis();
		OPUtils.saveValToSharedpreferences(Const.Key.APP_INSTALL_TIME,new Date().getTime()+"");
	}

}
