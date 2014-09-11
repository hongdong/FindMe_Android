package cn.ifanmi.findme.receiver;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.ui.activity.FindFansActivity;
import cn.ifanmi.findme.ui.activity.HomePageActivity;
import cn.ifanmi.findme.ui.activity.StatusInfoActivity;
import cn.ifanmi.findme.util.SPUtil;
import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
			String extra = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
			try {
				JSONObject jsonObject = new JSONObject(extra);
				String type = jsonObject.getString(IntentString.Push.TYPE);
				Intent infoIntent = new Intent(IntentString.Receiver.NEW_INFO);
				infoIntent.putExtra(IntentString.Push.TYPE, type);
				context.sendBroadcast(infoIntent);
				if (IntentString.Push.TYPE_STATUS_INFO.equals(type)) {
					int push_si = SPUtil.getPush(context, SPUtil.PUSH_SI);
					SPUtil.setPush(context, SPUtil.PUSH_SI, push_si + 1);
				} else if (IntentString.Push.TYPE_BOY_LIKE_YOU.equals(type)) {
					int push_ff = SPUtil.getPush(context, SPUtil.PUSH_FF);
					SPUtil.setPush(context, SPUtil.PUSH_SI, push_ff + 1);
				}
 			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
			String extra = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
			try {
				JSONObject jsonObject = new JSONObject(extra);
				String type = jsonObject.getString(IntentString.Push.TYPE);
				if (IntentString.Push.TYPE_FORCE_QUIT.equals(type)) {
					/**
					 * 强退事件不能再跳转到其它的Activity
					 * 因为在上面的JPushInterface.ACTION_NOTIFICATION_RECEIVED中，
					 * 主页接收到广播后已经销毁，并退到授权页面
					 */
				} else if (IntentString.Push.TYPE_STATUS_INFO.equals(type)) {
					clickIntent(context, StatusInfoActivity.class, type);
				} else if (IntentString.Push.TYPE_HAS_MATCH.equals(type)) {
					clickIntent(context, HomePageActivity.class, type);
				} else if (IntentString.Push.TYPE_GIRL_LIKE_YOU.equals(type)) {
					clickIntent(context, HomePageActivity.class, type);
				} else if (IntentString.Push.TYPE_NEW_FRIEND.equals(type)) {
					clickIntent(context, HomePageActivity.class, type);
				} else if (IntentString.Push.TYPE_BOY_LIKE_YOU.equals(type)) {
					clickIntent(context, FindFansActivity.class, type);
				}
 			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void clickIntent(Context context, Class<?> cls, String type) {
		Intent clickIntent = new Intent(JPushInterface.ACTION_NOTIFICATION_OPENED);
		clickIntent.setClass(context, cls);
		clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		clickIntent.putExtra(IntentString.Push.TYPE, type);
		context.startActivity(clickIntent);
	}

}
