package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;
import cn.ifanmi.findme.util.json.JsonUtil;

public class AuthActivity extends Activity implements IfanActivity {

	private String collegeName;
	private Button btn_back, btn_auth, btn_request;
	private View view_content, view_captcha;
	private TextView txt_college_name;
	private EditText edit_studentid, edit_password, edit_captcha;
	private ImageView img_captcha;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		PersonService service = new PersonService(this);
		String personId = SPUtil.getDefaultUser(this, null).getId();
		collegeName = service.getPersonById(personId).getCollegeName();
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_auth);
		btn_auth = (Button)findViewById(R.id.btn_auth);
		btn_request = (Button)findViewById(R.id.btn_auth_request_captcha);
		view_content = findViewById(R.id.layout_auth_content);
		view_captcha = findViewById(R.id.layout_auth_captcha);
		txt_college_name = (TextView)findViewById(R.id.txt_auth_college_name);
		edit_studentid = (EditText)findViewById(R.id.edit_auth_studentid);
		edit_password = (EditText)findViewById(R.id.edit_auth_password);
		edit_captcha = (EditText)findViewById(R.id.edit_auth_captcha);
		img_captcha = (ImageView)findViewById(R.id.img_auth_captcha);
		progressDialog = new ProgressDialog(this);
		
		txt_college_name.setText(collegeName);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_auth.setOnClickListener(listener);
		btn_request.setOnClickListener(listener);
		
		showPromptDialog();
	}
	
	private void showPromptDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setTitle("提示"); 
        builder.setMessage("实名认证需要与学校教务系统对接，学校教务系统出错或不稳定会导致认证失败。" +
        		"\n实名认证后，实名资料将覆盖您本人的个人资料，确定要认证吗？");
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss(); 
                doCheckCaptchaTask();
            }  
        });  
        builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                finish();
            }  
        });  
        builder.create().show();  
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
		showProgressDialog("检测认证环境...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, AuthActivity.class.getSimpleName());
		Task task = new Task(Task.FAN_CHECKCAPTCHA, taskParams);
		IfanService.addTask(task);
	}
	
	private void doGetCaptchaTask(String captchaUrl) {
		showProgressDialog("请求教务系统验证码...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, AuthActivity.class.getSimpleName());
		taskParams.put(Task.FAN_GETPHOTO_URL, Task.FAN_CAPTCHA_PREFIXURL + captchaUrl);
		Task task = new Task(Task.FAN_GETPHOTO, taskParams);
		IfanService.addTask(task);
	}
	
	private void doAuthTask() {
		showProgressDialog("正在认证...");
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, AuthActivity.class.getSimpleName());
		taskParams.put(Task.FAN_AUTH_STUDENTID, edit_studentid.getText().toString().trim());
		taskParams.put(Task.FAN_AUTH_PASSWORD, edit_password.getText().toString().trim());
		if (view_captcha.isShown()) {
			taskParams.put(Task.FAN_AUTH_CAPTCHA, edit_captcha.getText().toString().trim());
		}
		Task task = new Task(Task.FAN_AUTH, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_CHECKCAPTCHA:
			String check = (String) map.get(JsonString.Return.RESULT);
			refreshCheck(check);
			break;
			
		case Task.FAN_GETPHOTO:
			Drawable drawable = (Drawable) map.get(JsonString.Return.RESULT);
			refreshGet(drawable);
			break;
			
		case Task.FAN_AUTH:
			String auth = (String) map.get(JsonString.Return.RESULT);
			refreshAuth(auth);
			break;
		}
	}
	
	private void refreshCheck(String check) {
		if (check != null) {
			try {
				JSONObject jsonObject = new JSONObject(check);
				JSONObject object = jsonObject.getJSONObject("codeInfo");
				String isOpen = object.getString("isOpen");
				if (isOpen.equals("1")) {
					String hasCaptcha = object.getString("hashCode");
					if (hasCaptcha.equals("true")) {
						if (object.isNull("codeName")) {
							ToastUtil.longPrompt(this, "获取教务系统验证码失败，退出后再试");
						} else {
							String captchaUrl = object.getString("codeName");
							doGetCaptchaTask(captchaUrl);
						}
					} else if (hasCaptcha.equals("false")) {
						view_content.setVisibility(View.VISIBLE);
					} else {
						ToastUtil.prompt(this, "检测认证环境失败");
					}
				} else if (isOpen.equals("0")) {
					ToastUtil.prompt(this, "您的学校暂时未开通实名认证功能");
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshGet(Drawable drawable) {
		if (drawable != null) {
			img_captcha.setImageDrawable(drawable);
			edit_captcha.setVisibility(View.VISIBLE);
			view_captcha.setVisibility(View.VISIBLE);
			view_content.setVisibility(View.VISIBLE);
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	private void refreshAuth(String auth) {
		if (auth != null) {
			try {
				JSONObject jsonObject = new JSONObject(auth);
				JSONObject object = jsonObject.getJSONObject("authInfo");
				String state = object.getString("state");
				String message = object.getString("msg");
				if (state.equals("20001")) {
					ToastUtil.prompt(this, "认证成功");
					JSONObject personObject = object.getJSONObject("user");
					Person person = JsonUtil.getPersonByObject(personObject);
					PersonService personService = new PersonService(this);
					personService.emptyPersonDB();
					personService.insertPerson(person);
					personService.closeDBHelper();
					finish();
				} else {
					doCheckCaptchaTask();
					ToastUtil.prompt(this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			doCheckCaptchaTask();
			ToastUtil.noNet(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_auth:
				finish();
				break;
				
			case R.id.btn_auth:
				if (edit_studentid.getText().toString().trim().isEmpty()) {
					ToastUtil.prompt(AuthActivity.this, "学号不能为空");
				} else if (edit_password.getText().toString().trim().isEmpty()) {
					ToastUtil.prompt(AuthActivity.this, "密码不能为空");
				} else {
					if (view_captcha.isShown() && edit_captcha.getText().toString().trim().isEmpty()) {
						ToastUtil.prompt(AuthActivity.this, "验证码不能为空");
					} else {
						edit_captcha.setText("");
						edit_captcha.setVisibility(View.GONE);
						doAuthTask();
					}
				}
				break;
				
			case R.id.btn_auth_request_captcha:
				doCheckCaptchaTask();
				break;
			}
		}
	}

}
