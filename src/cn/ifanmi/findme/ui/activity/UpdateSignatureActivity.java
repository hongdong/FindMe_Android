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
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class UpdateSignatureActivity extends Activity implements IfanActivity {

	private String personId;
	private PersonService service;
	private Person person;
	private Button btn_back, btn_ok;
	private EditText edit_signature;
	private String signature;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_signature);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		personId = SPUtil.getDefaultUser(this, null).getId();
		service = new PersonService(this);
		person = service.getPersonById(personId);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_update_signature);
		btn_ok = (Button)findViewById(R.id.btn_ok_update_signature);
		edit_signature = (EditText)findViewById(R.id.edit_update_signature);
		progressDialog = new ProgressDialog(this);
		
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				signature = edit_signature.getText().toString().trim();
				doUpdataUDTask();
			}
		});
		edit_signature.setText(person.getSignature());
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
	
	private void doUpdataUDTask() {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, UpdateSignatureActivity.class.getSimpleName());
		taskParams.put(Person.SIGNATURE, signature);
		Task task = new Task(Task.FAN_UPDATEUD, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		String result = (String) map.get(JsonString.Return.RESULT);
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				String state = jsonObject.getString(JsonString.Return.STATE);
				if (state.equals("20001")) {
					ToastUtil.prompt(this, "修改个性签名成功");
					service.updataPersonInfo(personId, Person.SIGNATURE, signature);
					finish();
				} else {
					ToastUtil.prompt(this, "修改个性签名失败");
				}
			}  catch (JSONException e) {
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
		service.closeDBHelper();
	}

}
