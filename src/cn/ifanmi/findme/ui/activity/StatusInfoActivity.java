package cn.ifanmi.findme.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import cn.ifanmi.findme.adapter.InfoStatusAdapter;
import cn.ifanmi.findme.bean.InfoStatus;
import cn.ifanmi.findme.bean.Status;
import cn.ifanmi.findme.bean.Task;
import cn.ifanmi.findme.logic.FanApplication;
import cn.ifanmi.findme.logic.IfanActivity;
import cn.ifanmi.findme.logic.IfanService;
import cn.ifanmi.findme.string.JsonString;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshBase.OnRefreshListener;
import cn.ifanmi.findme.ui.view.ptor.PullToRefreshListView;
import cn.ifanmi.findme.util.SPUtil;
import cn.ifanmi.findme.util.TimeUtil;
import cn.ifanmi.findme.util.ToastUtil;

public class StatusInfoActivity extends Activity implements IfanActivity {

	private Button btn_back, btn_setting;
	private PullToRefreshListView plist;
	private ListView listView;
	private List<InfoStatus> infoStatuses;
	private InfoStatusAdapter adapter;
	private String oldStatusId;
	private TextView txt_no_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_info);
		IfanService.addActivity(this);
		initData();
		initView();
	}
	
	@Override
	public void initData() {
		
	}

	@Override
	public void initView() {
		btn_back = (Button)findViewById(R.id.btn_back_status_info);
		btn_setting = (Button)findViewById(R.id.btn_status_info_setting);
		plist = (PullToRefreshListView)findViewById(R.id.plist_status_info);
		listView = plist.getRefreshableView();
		txt_no_info = (TextView)findViewById(R.id.txt_status_info_no_info);
		
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
				infoStatuses.get(arg2).setIsRead("1");
				adapter.notifyDataSetChanged();
				FanApplication application = (FanApplication) getApplication();
				application.setStatus(infoStatuses.get(arg2).getStatus());
				Intent intent = new Intent(StatusInfoActivity.this, StatusDetailActivity.class);
				startActivity(intent);
			}
		});
		plist.doPullRefreshing(true, 500);
		
		MyOnClickListener listener = new MyOnClickListener();
		btn_back.setOnClickListener(listener);
		btn_setting.setOnClickListener(listener);
	}
	
	private void doGetStatusTask(String mode, String id) {
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(Task.FAN_ACTIVITY, StatusInfoActivity.class.getSimpleName());
		taskParams.put(Task.FAN_MODE, mode);
		taskParams.put(Status.ID, id);
		Task task = new Task(Task.FAN_GETSTATUSINFO, taskParams);
		IfanService.addTask(task);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(int taskId, Map<String, Object> map) {
		switch (taskId) {
		case Task.FAN_GETSTATUSINFO:
			String mode = (String) map.get(Task.FAN_MODE);
			List<InfoStatus> newInfoStatus = (List<InfoStatus>) map.get(JsonString.Return.RESULT);
			refreshList(mode, newInfoStatus);
			break;
		}
	}
	
	private void refreshList(String mode, List<InfoStatus> newInfoStatus) {
		if (newInfoStatus != null) {
			if (newInfoStatus.size() > 0) {
				if (mode.equals(Task.FAN_PTOR)) {
					txt_no_info.setVisibility(View.GONE);
					infoStatuses = newInfoStatus;
					adapter = new InfoStatusAdapter(this, infoStatuses);
					listView.setAdapter(adapter);
					listView.setVisibility(View.VISIBLE);
					txt_no_info.setVisibility(View.GONE);
				} else if (mode.equals(Task.FAN_LM)) {
					infoStatuses.addAll(newInfoStatus);
					adapter.notifyDataSetChanged();
				}
				oldStatusId = infoStatuses.get(infoStatuses.size() - 1).getStatus().getId();
			} else {	//提示还没有内容或者没有更多。长度为0并不代表没网络
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
		/**
		 * 这一步对于新消息的显示太重要的。
		 * 如果是在进来这个页面的时候清空新消息，
		 * 那么有可能在这个页面的时候，新消息来了，退出去时，又看到新消息。
		 * 而在退出时把新消息清空，就不会有错了
		 */
		SPUtil.setPush(this, SPUtil.PUSH_SI, 0);
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
			case R.id.btn_back_status_info:
				/**
				 * 这一步对于新消息的显示太重要的。
				 * 如果是在进来这个页面的时候清空新消息，
				 * 那么有可能在这个页面的时候，新消息来了，退出去时，又看到新消息。
				 * 而在退出时把新消息清空，就不会有错了
				 */
				SPUtil.setPush(StatusInfoActivity.this, SPUtil.PUSH_SI, 0);
				finish();
				break;
				
			case R.id.btn_status_info_setting:
				showPromptDialog();
				break;
			}
		}
	}
	
	private void showPromptDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
        builder.setTitle("帮助"); 
        builder.setMessage("我发布的或我参于的帖子有了更新，就会出现在这个列表里");
        builder.setPositiveButton("知道了", new AlertDialog.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss(); 
            }  
        });  
        builder.create().show();  
	}

}
