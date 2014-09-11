package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.ToastUtil;

public class SettingUFActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_ok;
	private EditText edit_uf;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_uf);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_setting_uf);
		btn_ok = (Button)findViewById(R.id.btn_ok_setting_uf);
		edit_uf = (EditText)findViewById(R.id.edit_setting_uf);
		progressDialog = new ProgressDialog(this);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_ok.setOnClickListener(listener);
	}
	
	private void showProgressDialog() {
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("稍等片刻...");
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void doUserFeedbackTask() {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, SettingUFActivity.class.getSimpleName());
		taskParams.put(Task.FAN_FEEDBACK_STRING, edit_uf.getText().toString().trim());
		Task task = new Task(Task.FAN_FEEDBACK, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		String result = (String) map.get(JsonString.Return.RESULT);
		refreshFeedback(result);
	}
	
	private void refreshFeedback(String result) {
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {		
					ToastUtil.prompt(this, "反馈成功！");
					edit_uf.setText("");
				} else {
					ToastUtil.prompt(this, "反馈失败！");
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
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_setting_uf:
				finish();
				break;

			case R.id.btn_ok_setting_uf:
				if (edit_uf.getText().toString().trim().isEmpty()) {
					ToastUtil.prompt(SettingUFActivity.this, "您什么都没填写");
				} else {
					doUserFeedbackTask();
				}
				break;
			}
		}
	}

}
