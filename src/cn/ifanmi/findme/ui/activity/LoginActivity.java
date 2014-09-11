package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Friend;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.dbService.FriendService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_login;
	private EditText edit_phonenumber, edit_password;
	private ProgressDialog progressDialog;
	private String phoneNumber, password, userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_login);
		btn_login = (Button)findViewById(R.id.btn_login_login);
		edit_phonenumber = (EditText)findViewById(R.id.edit_login_phonenumber);
		edit_password = (EditText)findViewById(R.id.edit_login_password);
		progressDialog = new ProgressDialog(this);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_login.setOnClickListener(listener);
	}
	
	private void showProgressDialog(String message) {
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void doLoginTask() {
		showProgressDialog("正在登录...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, LoginActivity.class.getSimpleName());
		taskParams.put(User.PHONENUMBER, phoneNumber);
		taskParams.put(User.PASSWORD, password);
		String regId = JPushInterface.getRegistrationID(getApplicationContext());
		taskParams.put(Task.FAN_AUTHO_DEVICE_CODE, regId);
		taskParams.put(Task.FAN_AUTHO_SYSTEM_TYPE, Task.FAN_AUTHO_SYSTEM_TYPE_ANDROID);
		taskParams.put(Task.FAN_AUTHO_BACK_LOGIN, Task.FAN_AUTHO_BACK_LOGIN_NO);
		Task task = new Task(Task.FAN_AUTHO, taskParams);
		IfanService.addTask(task);
	}
	
	private void doGetFriendTask() {
		showProgressDialog("正在获取好友列表...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, LoginActivity.class.getSimpleName());
		Task task = new Task(Task.FAN_GETFRIEND, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_AUTHO:
			String autho = (String) map.get(JsonString.Return.RESULT);
			refreshAutho(autho);
			break;
			
		case Task.FAN_GETFRIEND:
			List<Friend> friends = (List<Friend>) map.get(JsonString.Return.RESULT);
			refreshFriend(friends);
			break;
		}
	}
	
	private void refreshAutho(String autho) {
		if (autho != null) {
			try {
				JSONObject jsonObject = new JSONObject(autho);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {		//已经注册过了
					userId = jsonObject.getString("userId");
					doGetFriendTask();
				} else if (state.equals("20002")) {	//个人信息还未完善
					SPUtil.saveDefaultUser(this, User.PHONENUMBER, phoneNumber);
					SPUtil.saveDefaultUser(this, User.PASSWORD, password);
					jumpToSelectCollege();
					ToastUtil.longPrompt(this, "个人信息还未完善。完善个人信息后才能前往主页面");
				} else if (state.equals("20003")) {
					ToastUtil.prompt(this, "此帐号还没有注册");
				} else if (state.equals("20004")) {
					ToastUtil.prompt(this, "帐号或密码错误");
				} else {
					ToastUtil.prompt(this, "登录失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshFriend(List<Friend> friends) {
		if (friends != null) {
			FriendService service = new FriendService(this);
			service.insertFriends(friends);
			service.closeDBHelper();
			SPUtil.saveDefaultUser(this, User.ID, userId);
			SPUtil.saveDefaultUser(this, User.PHONENUMBER, phoneNumber);
			SPUtil.saveDefaultUser(this, User.PASSWORD, password);
			jumpToHomePage();
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void jumpToHomePage() {
		Intent intent = new Intent(this, HomePageActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
	
	private void jumpToSelectCollege() {
		Intent intent = new Intent(this, SelectCollegeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_login:
				finish();
				break;
			
			case R.id.btn_login_login:
				phoneNumber = edit_phonenumber.getText().toString().trim();
				password = edit_password.getText().toString().trim();
				if (phoneNumber.length() != 11) {
					ToastUtil.prompt(LoginActivity.this, "电话号码必须为11位数字");
				} else if (password.isEmpty()) {
					ToastUtil.prompt(LoginActivity.this, "密码不能为空");
				} else {
					doLoginTask();
				}
				break;
			}
		}
	}

}
