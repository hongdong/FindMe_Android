package cn.ifanmi.findme.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	public static void prompt(Context context, String promptString) {
		Toast.makeText(context, promptString, Toast.LENGTH_SHORT).show();
	}
	
	public static void longPrompt(Context context, String promptString) {
		Toast.makeText(context, promptString, Toast.LENGTH_LONG).show();
	}

	public static void serverError(Context context) {
		Toast.makeText(context, "服务器出错了！", Toast.LENGTH_SHORT).show();
	}
	
	public static void noNet(Context context) {
		Toast.makeText(context, "网络连接出错了！", Toast.LENGTH_SHORT).show();
	}
	
}
