package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.StatusAdapter;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.dbService.StatusService;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase.OnRefreshListener;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshListView;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.TimeUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class GroupActivity extends Activity implements IfanActivity {

	private StatusService service;
	private List<Status> statuses;
	private PullToRefreshListView plist;
	private ListView listView;
	private StatusAdapter adapter;
	private String oldStatusId;
	private Button btn_write, btn_info;
	private TextView txt_new_info, txt_no_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshNewInfo();
	}
	
	public void refreshNewInfo() {
		int push_si = SPUtil.getPush(this, SPUtil.PUSH_SI);
		if (push_si > 0) {
			txt_new_info.setText(String.valueOf(push_si));
			txt_new_info.setVisibility(View.VISIBLE);
		} else {
			txt_new_info.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void initData() {
		service = new StatusService(this);
		statuses = service.getStatuses();
	}

	@Override
	public void initView() {
		plist = (PullToRefreshListView)findViewById(R.id.plist_status);
		listView = plist.getRefreshableView();
		btn_write = (Button)findViewById(R.id.btn_write_status);
		btn_info = (Button)findViewById(R.id.btn_status_info);
		txt_new_info = (TextView)findViewById(R.id.txt_status_new_info);
		txt_no_status = (TextView)findViewById(R.id.txt_status_no_info);
		
		plist.setPullLoadEnabled(true);		//如果设置上拉有用，也要数据满一屏幕的时候才有用	
		plist.setScrollLoadEnabled(false);
		plist.setHasMoreData(true);			//这个标志要与setPullLoadEnabled配合使用
		plist.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onPullDownToRefresh( PullToRefreshBase<ListView> refreshView) {
				doGetStatusTask(Task.FAN_PTOR, null);
			}
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				doGetStatusTask(Task.FAN_LM, oldStatusId);
			}
		});
		listView.setDivider(null);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setSelector(R.drawable.no_selector);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FanApplication application = (FanApplication) getApplication();
				application.setStatus(statuses.get(arg2));
				Intent intent = new Intent(GroupActivity.this, StatusDetailActivity.class);
				startActivity(intent);
			}
		});
		plist.doPullRefreshing(true, 500);
		if (statuses != null) {
			adapter = new StatusAdapter(this, statuses);
			listView.setAdapter(adapter);
		}
		MyOnClickListener listener = new MyOnClickListener();
		btn_write.setOnClickListener(listener);
		btn_info.setOnClickListener(listener);
	}
	
	private void doGetStatusTask(String mode, String id) {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, GroupActivity.class.getSimpleName());
		taskParams.put(Task.FAN_MODE, mode);
		taskParams.put(Status.ID, id);
		Task task = new Task(Task.FAN_GETSTATUS, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		switch (taskId) {
		case Task.FAN_GETSTATUS:
			String mode = (String) map.get(Task.FAN_MODE);
			List<Status> newStatuses = (List<Status>) map.get(JsonString.Return.RESULT);
			refreshList(mode, newStatuses);
			break;
		}
	}
	
	private void refreshList(String mode, List<Status> newsStatuses) {
		if (newsStatuses != null) {
			if (newsStatuses.size() > 0) {
				if (mode.equals(Task.FAN_PTOR)) {
					txt_no_status.setVisibility(View.GONE);
					statuses = newsStatuses;
					adapter = new StatusAdapter(this, statuses);
					listView.setAdapter(adapter);
					new Thread() {
						public void run() {
							super.run();
							service.emptyStatusDB();
							service.insertStatuses(statuses);
						}
					}.start();
				} else if (mode.equals(Task.FAN_LM)) {
					statuses.addAll(newsStatuses);
					adapter.notifyDataSetChanged();
				}
				oldStatusId = statuses.get(statuses.size() - 1).getId();
			} else {
				if (mode.equals(Task.FAN_PTOR)) {
					txt_no_status.setVisibility(View.VISIBLE);
				} else if (mode.equals(Task.FAN_LM)) {
					ToastUtil.prompt(this, "已经加载全部内容");
				}
			}
		} else {
			ToastUtil.noNet(this);
		}
		plist.onPullDownRefreshComplete();
		plist.onPullUpRefreshComplete();
		plist.setLastUpdatedLabel(TimeUtil.setLastestUpdata());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case IntentString.RequestCode.GROUP_WRITESTATUS:
			if (IntentString.ResultCode.WRITESTATUS_GROUP == resultCode) {
				listView.setSelection(0);
				ToastUtil.prompt(this, "发布成功");
				plist.doPullRefreshing(true, 500);
			}
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_write_status:
				Intent writeIntent = new Intent(GroupActivity.this, WriteStatusActivity.class);
				startActivityForResult(writeIntent, IntentString.RequestCode.GROUP_WRITESTATUS);
				break;
			case R.id.btn_status_info:
				Intent infoIntent = new Intent(GroupActivity.this, StatusInfoActivity.class);
				startActivity(infoIntent);
				break;
			}
		}
	}

}
