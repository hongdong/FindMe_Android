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
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.adapter.FansAdapter;
import cn.ifanmi.findme.bean.Person;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshListView;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase.OnRefreshListener;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.TimeUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class FindFansActivity extends Activity implements IfanActivity {

	private Button btn_back;
	private PullToRefreshListView plist;
	private ListView listView;
	private List<Person> persons;
	private FansAdapter adapter;
	private String oldFansId;
	private TextView txt_no_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_fans);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		plist.doPullRefreshing(true, 500);
	}
	
	@Override
	public void initData() {

	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_find_fans);
		plist = (PullToRefreshListView)findViewById(R.id.plist_find_fans);
		listView = plist.getRefreshableView();
		txt_no_info = (TextView)findViewById(R.id.txt_find_fans_no_info);
		
		plist.setPullLoadEnabled(true);		//如果设置上拉有用，也要数据满一屏幕的时候才有用	
		plist.setScrollLoadEnabled(false);
		plist.setHasMoreData(true);			//这个标志要与setPullLoadEnabled配合使用
		plist.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onPullDownToRefresh( PullToRefreshBase<ListView> refreshView) {
				doGetFansTask(Task.FAN_PTOR, null);
			}
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				doGetFansTask(Task.FAN_LM, oldFansId);
			}
		});
		listView.setDivider(null);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setSelector(R.drawable.no_selector);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(FindFansActivity.this, FindFDActivity.class);
				FanApplication application = (FanApplication) getApplication();
				application.setPerson(persons.get(arg2));
				startActivity(intent);
			}
		});
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
	}
	
	private void doGetFansTask(String mode, String oldFansId) {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, FindFansActivity.class.getSimpleName());
		taskParams.put(Task.FAN_MODE, mode);
		taskParams.put(Person.ID, oldFansId);
		Task task = new Task(Task.FAN_GETFANS, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		switch (taskId) {
		case Task.FAN_GETFANS:
			String mode = (String) map.get(Task.FAN_MODE);
			List<Person> newPersons = (List<Person>) map.get(JsonString.Return.RESULT);
			refreshList(mode, newPersons);
			break;
		}
	}
	
	private void refreshList(String mode, List<Person> newPersons) {
		if (newPersons != null) {
			if (newPersons.size() > 0) {
				if (mode.equals(Task.FAN_PTOR)) {
					txt_no_info.setVisibility(View.GONE);
					persons = newPersons;
					adapter = new FansAdapter(this, persons);
					listView.setAdapter(adapter);
					listView.setVisibility(View.VISIBLE);
					txt_no_info.setVisibility(View.GONE);
				} else if (mode.equals(Task.FAN_LM)) {
					persons.addAll(newPersons);
					adapter.notifyDataSetChanged();
				}
				oldFansId = persons.get(persons.size() - 1).getId();
			} else {
				if (mode.equals(Task.FAN_PTOR)) {
					listView.setVisibility(View.GONE);
					txt_no_info.setVisibility(View.VISIBLE);
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
	public void onBackPressed() {
		SPUtil.setPush(this, SPUtil.PUSH_FF, 0);
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		IfanService.removeActivity(this);
	}
	
	class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back_find_fans:
				SPUtil.setPush(FindFansActivity.this, SPUtil.PUSH_FF, 0);
				finish();
				break;
			}
		}
	}

}
