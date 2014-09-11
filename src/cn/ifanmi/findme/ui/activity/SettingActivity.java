package cn.ifanmi.findme.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.FriendService;
import cn.ifanmi.findme.dbService.PersonService;
import cn.ifanmi.findme.dbService.StatusService;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.ToastUtil;
import com.easemob.chat.EMChatManager;

public class SettingActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_exit;
	private ListView listView;
	private ProgressDialog progressDialog;
	private String[] setting = {"软件介绍", "版本特性", "清除缓存", "关于我们", "用户反馈"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_setting);
		btn_exit = (Button)findViewById(R.id.btn_exit_setting);
		listView = (ListView)findViewById(R.id.list_setting);
		progressDialog = new ProgressDialog(this);
		
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < setting.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(NormalString.List.LEFT, setting[i]);
			list.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_setting_item, 
				new String[] {NormalString.List.LEFT}, 
				new int[] {R.id.txt_list_setting, });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
				case 0:
					Intent intent0 = new Intent(SettingActivity.this, SettingSIActivity.class);
					startActivity(intent0);
					break;
					
				case 1:
					Intent intent1 = new Intent(SettingActivity.this, SettingVFActivity.class);
					startActivity(intent1);
					break;
					
				case 2:
					showProgressDialog();
					FinalBitmap.create(SettingActivity.this).clearCache();
					ToastUtil.prompt(SettingActivity.this, "清除成功");
					dismissProgressDialog();
					break;
					
				case 3:
					Intent intent3 = new Intent(SettingActivity.this, SettingAUActivity.class);
					startActivity(intent3);
					break;
					
				case 4:
					Intent intent4 = new Intent(SettingActivity.this, SettingUFActivity.class);
					startActivity(intent4);
					break;
				}
			}
		});
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_exit.setOnClickListener(listener);
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
	
	private void doExitTask() {
		showProgressDialog();
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, SettingActivity.class.getSimpleName());
		Task task = new Task(Task.FAN_EXIT, taskParams);
		IfanService.addTask(task);
	}

	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		dismissProgressDialog();
		switch (taskId) {
		case Task.FAN_EXIT:
			String result = (String) map.get(JsonString.Return.RESULT);
			refreshExit(result);
			break;
		}
	}
	
	private void refreshExit(String result) {
		/**
		 * 这里一定要记得退出环信聊天服务器
		 * 不然的话新的账户又登录，数据可能会乱
		 */
		EMChatManager.getInstance().logout();	
		IfanService.emptyList();
		SPUtil.emptyDefaultUser(this);
		SPUtil.emptyPush(this);
		FriendService friendService = new FriendService(this);
		friendService.emptyFriendDB();
		friendService.closeDBHelper();
		StatusService statusService = new StatusService(this);
		statusService.emptyStatusDB();
		statusService.closeDBHelper();
		PersonService personService = new PersonService(this);
		personService.emptyPersonDB();
		personService.closeDBHelper();
		Intent intent = new Intent(this, AuthoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_setting:
				finish();
				break;
				
			case R.id.btn_exit_setting:
				doExitTask();
				break;
			}
		}
	}

}
