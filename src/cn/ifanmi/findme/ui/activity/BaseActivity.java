package cn.ifanmi.findme.ui.activity;

import com.easemob.chat.EMChatManager;

import android.app.Activity;

public class BaseActivity extends Activity {

	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * 这个太重要了，应用回到前台后，调用下面的方法会清空应用的通知数。
		 * 不然的话，通知数会一直叠加
		 * 目前我只设置了InfoActivity继承这个类
		 */
		EMChatManager.getInstance().activityResumed();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
