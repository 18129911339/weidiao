package cn.fjnu.edu.paint.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

	public static String getValue(String key){
		SharedPreferences sharedPreferences= Const.appContext.getSharedPreferences(Const.SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}
	
	public static void saveValue(String key, String value){
		SharedPreferences sharedPreferences= Const.appContext.getSharedPreferences(Const.SHARED_NAME, Context.MODE_PRIVATE);
	//	SharedPreferences.Editor editor
		SharedPreferences.Editor editor= sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
}
