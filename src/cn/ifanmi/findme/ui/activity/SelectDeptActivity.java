package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.bean.User;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class SelectDeptActivity extends Activity implements IfanActivity {

	private String collegeId;
	private Button btn_back;
	private ListView list;
	private List<String> depts;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_dept);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		collegeId = getIntent().getStringExtra(IntentString.Extra.COLLEGEID);
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_select_dept);
		list = (ListView)findViewById(R.id.list_select_dept);
		progressDialog = new ProgressDialog(this);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String deptName = depts.get(arg2);
				String deptId = indexToId(arg2);
				SPUtil.saveDefaultUser(SelectDeptActivity.this, User.DEPTID, deptId);
				SPUtil.saveDefaultUser(SelectDeptActivity.this, User.DEPTNAME, deptName);
				Intent intent = new Intent(SelectDeptActivity.this, SelectGradeActivity.class);
				startActivity(intent);
			}
		});
		
		doGetDept();
	}
	
	private void showProgressDialog() {
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("正在获取院系列表...");
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private void doGetDept() {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, SelectDeptActivity.class.getSimpleName());
		taskParams.put(User.COLLEGEID, collegeId);
		Task task = new Task(Task.FAN_GETDEPT, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		depts = (List<String>) map.get(JsonString.Return.RESULT);
		if (depts != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_common_txt_item, depts);
			list.setAdapter(adapter);
		} else {
			ToastUtil.noNet(this);
		}
	}
	
	/**
	 * 将院系数组的下标转为String类型的id
	 * @param index
	 * @return
	 */
	private String indexToId(int index) {
		String id = collegeId;
		if (index < 10) {
			id = id + "00" + String.valueOf(index + 1);
		} else if (index < 100) {
			id = id + "0" + String.valueOf(index + 1);
		}
		return id;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}

}
