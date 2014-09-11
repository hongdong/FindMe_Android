package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SignupActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_get, btn_signup;
	private EditText edit_phone, edit_password, edit_password_again, edit_captcha;
	private TextView txt_countdown;
	private ProgressDialog progressDialog;
	private String phoneNumber, password, captcha;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int event = msg.arg1;
			int result = msg.arg2;
			@SuppressWarnings("unused")
			Object data = msg.obj;
			dismissProgressDialog();
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {		//提交验证码成功
					doSignupTask();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){	//发送验证码成功
					ToastUtil.prompt(SignupActivity.this, "短信已成功发送，请查收");
				}
			} else {
				ToastUtil.prompt(SignupActivity.this, "操作失败");
			}
		};
	};
	
	@Override
	public void initData() {
		SMSSDK.initSDK(this, "2f26b8450ef8", "557dc5260db50733cd40e75c4839095f");
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eventHandler);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_signup);
		btn_get = (Button)findViewById(R.id.btn_signup_get_captcha);
		btn_signup = (Button)findViewById(R.id.btn_signup_signup);
		edit_phone = (EditText)findViewById(R.id.edit_signup_phonenumber);
		edit_password = (EditText)findViewById(R.id.edit_signup_password);
		edit_password_again = (EditText)findViewById(R.id.edit_signup_password_again);
		edit_captcha = (EditText)findViewById(R.id.edit_signup_captcha);
		txt_countdown = (TextView)findViewById(R.id.txt_signup_countdown);
		progressDialog = new ProgressDialog(this);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_get.setOnClickListener(listener);
		btn_signup.setOnClickListener(listener);
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
	
	private void doCheckCaptchaTask() {
		showProgressDialog("正在检测验证码...");
		SMSSDK.submitVerificationCode("86", phoneNumber, captcha);
	}
	
	private void doSignupTask() {
		showProgressDialog("正在注册...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, SignupActivity.class.getSimpleName());
		taskParams.put(User.PHONENUMBER, phoneNumber);
		taskParams.put(User.PASSWORD, password);
		Task task = new Task(Task.FAN_SIGNUP, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_SIGNUP:
			String login = (String) map.get(JsonString.Return.RESULT);
			refreshLogin(login);
			break;
		}
	}
	
	private void refreshLogin(String login) {
		if (login != null) {
			try {
				JSONObject jsonObject = new JSONObject(login);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {
					ToastUtil.prompt(SignupActivity.this, "注册成功");
					SPUtil.saveDefaultUser(this, User.ID, jsonObject.getString("id"));
					SPUtil.saveDefaultUser(this, User.PHONENUMBER, phoneNumber);
					SPUtil.saveDefaultUser(this, User.PASSWORD, password);
					Intent intent = new Intent(this, SelectCollegeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
					finish();
				} else if (state.equals("20002")) {
					ToastUtil.prompt(SignupActivity.this, "该号码已经被注册");
				} else {
					ToastUtil.prompt(SignupActivity.this, "注册失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
		SMSSDK.unregisterAllEventHandler();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_signup:
				finish();
				break;
				
			case R.id.btn_signup_get_captcha:
				phoneNumber = edit_phone.getText().toString().trim();
				if (11 == phoneNumber.length()) {
					SMSSDK.getVerificationCode("86", phoneNumber);
					new CountDownTimer(60000, 1000) {
						public void onTick(long millisUntilFinished) {
							txt_countdown.setText(String.valueOf(millisUntilFinished / 1000) + "秒");
						}
						public void onFinish() {
							btn_get.setVisibility(View.VISIBLE);
							txt_countdown.setVisibility(View.GONE);
						}
					}.start();
					btn_get.setVisibility(View.GONE);
					txt_countdown.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.prompt(SignupActivity.this, "电话号码必须为11位数字");
				}
				break;
				
			case R.id.btn_signup_signup:
				captcha = edit_captcha.getText().toString().trim();
				password = edit_password.getText().toString().trim();
				String password_again = edit_password_again.getText().toString().trim();
				if (captcha.equals("")) {
					ToastUtil.prompt(SignupActivity.this, "验证码未填写");
				} else if (password.equals("")) {
					ToastUtil.prompt(SignupActivity.this, "密码未填写");
				} else if (password_again.equals("")) {
					ToastUtil.prompt(SignupActivity.this, "密码未填写");
				} else if (!password.equals(password_again)) {
					ToastUtil.prompt(SignupActivity.this, "两次输入的密码不一致");
				} else if (password.length() < 6 || password.length() > 18) {
					ToastUtil.prompt(SignupActivity.this, "密码长度必须为6到18位之间");
				} else {
					doCheckCaptchaTask();
				}
				break;
			}
		}
	}

}
