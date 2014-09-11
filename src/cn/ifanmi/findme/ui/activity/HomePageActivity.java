package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.logic.IfanUpdata;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import com.easemob.EMCallBack;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

@SuppressWarnings("deprecation")
public class HomePageActivity extends ActivityGroup implements IfanActivity {

	private TabHost tabHost;
	private String[] tabs = {"find", "group", "friend", "info", "me"};
	private Class<?>[] hpClasses = {FindActivity.class, InfoActivity.class, 
			FriendActivity.class, GroupActivity.class, MeActivity.class};
	public static final int INDEX_FIND = 0;
	public static final int INDEX_INFO = 1;
	public static final int INDEX_FRIEND = 2;
	public static final int INDEX_GROUP = 3;
	public static final int INDEX_ME = 4;
	private int[] btnIds = {R.id.btn_hp_find, R.id.btn_hp_info, R.id.btn_hp_friend, 
			R.id.btn_hp_group, R.id.btn_hp_me};
	private Button[] buttons = new Button[btnIds.length];
	private int[] txtIds = {R.id.txt_hp_find, R.id.txt_hp_info, R.id.txt_hp_friend, 
			R.id.txt_hp_group, R.id.txt_hp_me};
	private TextView[] textViews = new TextView[txtIds.length];
	private int curIndex;
	private boolean first_time_login = true;
	private boolean first_time_login_error = true;
	private boolean isExit = false;
	private NewMessageBroadcastReceiver nmReceiver;
	private AckMessageReceiver amReceiver;
	private NewInfoReceiver niReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		checkNet();
	}
	
	/**
	 * 检测网络，也可以说是登录服务器。自己的服务器和环信服务器都要登录
	 */
	private void checkNet() {
		new Thread() {
			public void run() {
				while (true) {
					doCheckNetTask();
					try {
						if (first_time_login) {
							sleep(5000);
						} else {
							sleep(600000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void initView() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup(getLocalActivityManager());
		MyOnClickListener listener = new MyOnClickListener();
		for (int i = 0; i < hpClasses.length; i++) {
			TabSpec spec = tabHost.newTabSpec(tabs[i]).setIndicator(tabs[i])
					.setContent(new Intent(this, hpClasses[i]));
			tabHost.addTab(spec);
			buttons[i] = (Button)findViewById(btnIds[i]);
			buttons[i].setOnClickListener(listener);
			textViews[i] = (TextView)findViewById(txtIds[i]);
		}
		buttons[INDEX_FIND].setSelected(true);
		curIndex = INDEX_FIND;
		
		// 注册一个接收消息的BroadcastReceiver
		nmReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(nmReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		amReceiver = new AckMessageReceiver();
		IntentFilter ackMessageIntentFilter = new IntentFilter(
				EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(amReceiver, ackMessageIntentFilter);
		
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		
		niReceiver = new NewInfoReceiver();
		IntentFilter niIntentFilter = new IntentFilter(IntentString.Receiver.NEW_INFO);
		registerReceiver(niReceiver, niIntentFilter);
	}
	
	private void doCheckNetTask() {
		User user = SPUtil.getDefaultUser(this, null);
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, HomePageActivity.class.getSimpleName());
		taskParams.put(User.PHONENUMBER, user.getPhoneNumber());
		taskParams.put(User.PASSWORD, user.getPassword());
		taskParams.put(Task.FAN_AUTHO_DEVICE_CODE, JPushInterface.getRegistrationID(this));
		taskParams.put(Task.FAN_AUTHO_SYSTEM_TYPE, Task.FAN_AUTHO_SYSTEM_TYPE_ANDROID);
		if (first_time_login) {
			taskParams.put(Task.FAN_AUTHO_BACK_LOGIN, Task.FAN_AUTHO_BACK_LOGIN_NO);
		} else {
			taskParams.put(Task.FAN_AUTHO_BACK_LOGIN, Task.FAN_AUTHO_BACK_LOGIN_YES);
		}
		Task task = new Task(Task.FAN_AUTHO, taskParams);
		IfanService.addTask(task);
	}
	
	private void doCheckEMNetTask() {
		String userId = SPUtil.getDefaultUser(this, null).getId();
		EMChatManager.getInstance().login(userId, User.EM_PASSWORD, new EMCallBack() {
			public void onSuccess() {
				
			}
			public void onProgress(int arg0, String arg1) {
				
			}
			public void onError(int arg0, String arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						ToastUtil.prompt(HomePageActivity.this, "登录聊天服务器失败");
					}
				});
			}
		});
	}
	
	private void doCheckUpdataTask() {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, HomePageActivity.class.getSimpleName());
		taskParams.put(Task.FAN_AUTHO_SYSTEM_TYPE, Task.FAN_AUTHO_SYSTEM_TYPE_ANDROID);
		Task task = new Task(Task.FAN_CHECKUPDATE, taskParams);
		IfanService.addTask(task);
	}
	
	private void doExitTask() {
		finish();
		ShareSDK.stopSDK(this);
		IfanService.emptyList();
		Intent intent = new Intent(this, IfanService.class);
		stopService(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
		
/*		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, HomePageActivity.class.getSimpleName());
		Task task = new Task(Task.FAN_EXIT, taskParams);
		IfanService.addTask(task);	*/
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		String result = (String) map.get(JsonString.Return.RESULT);
		switch (taskId) {
		case Task.FAN_AUTHO:
			refreshCheckNet(result);
			break;
			
		case Task.FAN_CHECKUPDATE:
			refreshCheckUpdata(result);
			break;
			
		case Task.FAN_EXIT:
			refreshExit(result);
			break;
		}
	}
	
	private void refreshCheckNet(String result) {
		if (null == result) {
			if (first_time_login_error) {
				first_time_login_error = false;
				ToastUtil.noNet(this);
			}
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {
					if (first_time_login) {
						first_time_login = false;
						doCheckEMNetTask();
						doCheckUpdataTask();
					}
				} else {
					if (first_time_login_error) {
						first_time_login_error = false;
						ToastUtil.prompt(this, "登录失败");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void refreshCheckUpdata(String result) {
		if (result != null) {
			IfanUpdata ifanUpdata = new IfanUpdata(this, result);
			ifanUpdata.checkUpdate();
		}
	}
	
	private void refreshExit(String result) {
		EMChatManager.getInstance().logout();
		ShareSDK.stopSDK(this);
		Intent intent = new Intent(this, IfanService.class);
		stopService(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//这里要依次刷新底下的txt
		updateUrmCount();
	}
	
	@Override
	public void onBackPressed() {
		exitByTwoClick();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(nmReceiver);
		unregisterReceiver(amReceiver);
		unregisterReceiver(niReceiver);
	}
	
	private void exitByTwoClick() {  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        ToastUtil.prompt(this, "再按一次退出程序");
	        Timer tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
				public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务    
	    } else {  
	    	doExitTask();
	    }  
	}  
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			for (int i = 0; i < btnIds.length; i++) {
				if (btnIds[i] == v.getId()) {
					checkIndex(i);
				}
			}
		}
	}
	
	private void checkIndex(int index) {
		buttons[curIndex].setSelected(false);
		tabHost.setCurrentTab(index);
		buttons[index].setSelected(true);
		curIndex = index;
	}
	
	private class MyConnectionListener implements ConnectionListener {
		public void onConnected() {
			//这几个方法，先不写东西
		}
		public void onConnecting(String arg0) {
			//
		}
		public void onDisConnected(String arg0) {
			//
		}
		public void onReConnected() {
			
		}
		public void onReConnecting() {
			
		}
	}
	
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			updateUrmCount();
			if (INDEX_INFO == curIndex) {
				InfoActivity infoActivity = (InfoActivity) getLocalActivityManager().getActivity(tabs[INDEX_INFO]);
				infoActivity.refreshList();
			}
			//这里要中断广播传送，不然ChatActivity可能还会接收到广播
			abortBroadcast();
		}
	}
	
	public void updateUrmCount() {
		int urmCount = EMChatManager.getInstance().getUnreadMsgsCount();
		if (urmCount > 0) {
			textViews[INDEX_INFO].setText(String.valueOf(urmCount));
			textViews[INDEX_INFO].setVisibility(View.VISIBLE);
		} else {
			textViews[INDEX_INFO].setVisibility(View.INVISIBLE);
		}
	}

	private class AckMessageReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};
	
	private class NewInfoReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (IntentString.Receiver.NEW_INFO.equals(action)) {
				String type = intent.getStringExtra(IntentString.Push.TYPE);
				if (IntentString.Push.TYPE_FORCE_QUIT.equals(type)) {
					ToastUtil.longPrompt(context, "您的帐号在其它设备上登录，您被强迫下线！");
					IfanService.emptyList();
					SPUtil.saveDefaultUser(context, User.ID, null);
					Intent forceQuitIntent = new Intent(context, AuthoActivity.class);
					forceQuitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(forceQuitIntent);
				} else if (IntentString.Push.TYPE_STATUS_INFO.equals(type)) {
					if (curIndex == INDEX_GROUP) {
						GroupActivity groupActivity = 
								(GroupActivity) getLocalActivityManager().getActivity(tabs[INDEX_GROUP]);
						groupActivity.refreshNewInfo();
					}
				} else if (IntentString.Push.TYPE_HAS_MATCH.equals(type)) {
					FindActivity findActivity = (FindActivity) getLocalActivityManager().getActivity(tabs[INDEX_FIND]);
					findActivity.doGetMatchTask();
				} else if (IntentString.Push.TYPE_GIRL_LIKE_YOU.equals(type)) {
					FindActivity findActivity = (FindActivity) getLocalActivityManager().getActivity(tabs[INDEX_FIND]);
					findActivity.doGetMatchTask();
				} else if (IntentString.Push.TYPE_NEW_FRIEND.equals(type)) {
					Intent nfIntent = new Intent(IntentString.Receiver.NEW_FRIEND);
					sendBroadcast(nfIntent);
				} else if (IntentString.Push.TYPE_BOY_LIKE_YOU.equals(type)) {
					if (curIndex == INDEX_FIND) {
						FindActivity findActivity = 
								(FindActivity) getLocalActivityManager().getActivity(tabs[INDEX_FIND]);
						findActivity.refreshNewInfo();
					}
				}
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (IntentString.EM.ACTION_CLICK.equals(action)) {
			String type = intent.getStringExtra(IntentString.Push.TYPE);
			if (IntentString.EM.TYPE_CLICK.equals(type)) {
				checkIndex(INDEX_INFO);
			}
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
			String type = intent.getStringExtra(IntentString.Push.TYPE);
			if (IntentString.Push.TYPE_HAS_MATCH.equals(type)) {
				checkIndex(INDEX_FIND);
			} else if (IntentString.Push.TYPE_GIRL_LIKE_YOU.equals(type)) {
				checkIndex(INDEX_FIND);
			} else if (IntentString.Push.TYPE_NEW_FRIEND.equals(type)) {
				checkIndex(INDEX_FRIEND);
			} 
		}
	}


}
